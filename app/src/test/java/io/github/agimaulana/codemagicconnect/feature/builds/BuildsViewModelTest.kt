package io.github.agimaulana.codemagicconnect.feature.builds

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.github.agimaulana.codemagicconnect.core.testing.CoroutineMainDispatcherRule
import io.github.agimaulana.codemagicconnect.domain.model.ArtifactDownload
import io.github.agimaulana.codemagicconnect.domain.model.BuildArtifact
import io.github.agimaulana.codemagicconnect.domain.model.BuildStatus
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicBuild
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.DownloadArtifactUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.GetApplicationUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.GetBuildsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.InstallArtifactUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.ObserveArtifactDownloadsUseCase
import io.github.agimaulana.codemagicconnect.platform.DeviceAbiProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class BuildsViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule()

    private val getApplicationUseCase = mockk<GetApplicationUseCase>()
    private val getBuildsUseCase = mockk<GetBuildsUseCase>()
    private val downloadArtifactUseCase = mockk<DownloadArtifactUseCase>()
    private val observeArtifactDownloadsUseCase = mockk<ObserveArtifactDownloadsUseCase>()
    private val installArtifactUseCase = mockk<InstallArtifactUseCase>()

    private val downloads = MutableSharedFlow<List<ArtifactDownload>>()

    private val deviceAbiProvider = object : DeviceAbiProvider {
        override val supportedAbis: List<String> = listOf("arm64-v8a")
    }

    private val app = CodemagicApplication("app-1", "acme", "github.com/acme", null, teamId = "team-9")
    private val artifact = BuildArtifact("a1", "app-release.apk", 48_000_000, downloadUrl = "https://x/app.apk")
    private val build = CodemagicBuild(
        id = "b1",
        appId = "app-1",
        workflowId = "android-release",
        branch = "main",
        status = BuildStatus.FINISHED,
        startedAt = "Today",
        finishedAt = null,
        artifacts = listOf(artifact),
        triggerer = ""
    )

    private fun viewModel() = BuildsViewModel(
        savedStateHandle = SavedStateHandle(mapOf(APP_ID_ARG to "app-1")),
        getApplicationUseCase = getApplicationUseCase,
        getBuildsUseCase = getBuildsUseCase,
        downloadArtifactUseCase = downloadArtifactUseCase,
        observeArtifactDownloadsUseCase = observeArtifactDownloadsUseCase,
        installArtifactUseCase = installArtifactUseCase,
        buildTimestampFormatter = BuildTimestampFormatter(),
        deviceAbiProvider = deviceAbiProvider
    )

    private fun stubSources() {
        stubSources(build)
    }

    private fun stubSources(vararg builds: CodemagicBuild) {
        coEvery { getApplicationUseCase("app-1") } returns app
        coEvery { getBuildsUseCase("app-1") } returns builds.toList()
        every { observeArtifactDownloadsUseCase() } returns downloads
    }

    private fun isoAtTodayNoon(): String = isoAtNoonMinusDays(0)

    private fun isoAtTodayNoonMinusDays(days: Int): String = isoAtNoonMinusDays(days)

    private fun isoAtNoonMinusDays(days: Int): String {
        val noon = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(noon.time)
    }

    @Test
    fun `given app and builds when init then state is populated`() = runTest {
        stubSources()
        val viewModel = viewModel()

        viewModel.init()

        assertEquals(app, viewModel.uiState.value.app)
        assertEquals(1, viewModel.uiState.value.builds.size)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given artifact when download then use case runs and completion sheet opens`() = runTest {
        stubSources()
        coEvery { downloadArtifactUseCase("b1", artifact) } returns Unit
        val viewModel = viewModel()
        viewModel.init()

        viewModel.onAction(BuildsViewModel.Action.DownloadArtifact("b1", "a1"))

        coVerify(exactly = 1) { downloadArtifactUseCase("b1", artifact) }
        assertEquals(BuildArtifact.DownloadStatus.DOWNLOADED, viewModel.uiState.value.showDownloadComplete?.downloadStatus)
    }

    @Test
    fun `given download progress when observed then artifacts are merged`() = runTest {
        stubSources()
        val viewModel = viewModel()
        viewModel.init()

        downloads.emit(
            listOf(ArtifactDownload("a1", "b1", BuildArtifact.DownloadStatus.DOWNLOADING, progress = 0.5f, downloadedBytes = 24_000_000))
        )

        val merged = viewModel.uiState.value.builds.single().build.artifacts.single()
        assertEquals(BuildArtifact.DownloadStatus.DOWNLOADING, merged.downloadStatus)
        assertEquals(0.5f, merged.downloadProgress, 0.001f)
        assertEquals(24_000_000L, merged.downloadSizeSoFarBytes)
    }

    @Test
    fun `given downloaded artifact when install confirmed then installer runs`() = runTest {
        stubSources()
        coEvery { downloadArtifactUseCase("b1", artifact) } returns Unit
        coEvery { installArtifactUseCase("a1") } returns Unit
        val viewModel = viewModel()
        viewModel.init()

        viewModel.onAction(BuildsViewModel.Action.DownloadArtifact("b1", "a1"))
        viewModel.onAction(BuildsViewModel.Action.CloseDownloadComplete(install = true))

        coVerify(exactly = 1) { installArtifactUseCase("a1") }
        assertEquals(null, viewModel.uiState.value.showDownloadComplete)
    }

    @Test
    fun `given refresh when action then builds are reloaded`() = runTest {
        stubSources()
        val viewModel = viewModel()
        viewModel.init()

        viewModel.onAction(BuildsViewModel.Action.Refresh)

        coVerify(exactly = 2) { getBuildsUseCase("app-1") }
    }

    @Test
    fun `given build started today when init then timestamp is Today`() = runTest {
        stubSources(
            build.copy(startedAt = isoAtTodayNoon()),
            build.copy(startedAt = isoAtTodayNoonMinusDays(1)),
            build.copy(startedAt = "2020-01-15T10:00:00Z")
        )
        val viewModel = viewModel()

        viewModel.init()

        val timestamps = viewModel.uiState.value.builds.map { it.timestamp }
        assertTrue(timestamps[0] is BuildsViewModel.BuildTimestamp.Today)
        assertTrue(timestamps[1] is BuildsViewModel.BuildTimestamp.Yesterday)
        assertTrue(timestamps[2] is BuildsViewModel.BuildTimestamp.OnDate)
    }

    @Test
    fun `given multiple branches when select branch then only that branch builds are shown`() = runTest {
        stubSources(
            build.copy(id = "b1", workflowId = "android-release", branch = "main"),
            build.copy(id = "b2", workflowId = "android-debug", branch = "feature/login")
        )
        val viewModel = viewModel()
        viewModel.init()

        viewModel.onAction(BuildsViewModel.Action.SelectBranch("feature/login"))

        assertEquals("feature/login", viewModel.uiState.value.selectedBranch)
        assertEquals(listOf("b2"), viewModel.uiState.value.builds.map { it.build.id })
    }

    @Test
    fun `given builds when init then branch options are distinct and sorted`() = runTest {
        stubSources(
            build.copy(id = "b1", branch = "main"),
            build.copy(id = "b2", branch = "main"),
            build.copy(id = "b3", branch = "feature/login")
        )
        val viewModel = viewModel()

        viewModel.init()

        assertEquals(listOf("feature/login", "main"), viewModel.uiState.value.branches)
    }

    @Test
    fun `given build with blank branch when init then blank option is omitted`() = runTest {
        stubSources(
            build.copy(id = "b1", branch = ""),
            build.copy(id = "b2", branch = "   "),
            build.copy(id = "b3", branch = "main")
        )
        val viewModel = viewModel()

        viewModel.init()

        assertEquals(listOf("main"), viewModel.uiState.value.branches)
    }

    @Test
    fun `given build with blank workflow when init then blank option is omitted`() = runTest {
        stubSources(
            build.copy(id = "b1", workflowId = ""),
            build.copy(id = "b2", workflowId = "android-release")
        )
        val viewModel = viewModel()

        viewModel.init()

        assertEquals(listOf("android-release"), viewModel.uiState.value.workflows)
    }

    @Test
    fun `given selected branch when select all branches then every build is shown`() = runTest {
        stubSources(
            build.copy(id = "b1", branch = "main"),
            build.copy(id = "b2", branch = "feature/login")
        )
        val viewModel = viewModel()
        viewModel.init()
        viewModel.onAction(BuildsViewModel.Action.SelectBranch("main"))

        viewModel.onAction(BuildsViewModel.Action.SelectBranch(null))

        assertEquals(null, viewModel.uiState.value.selectedBranch)
        assertEquals(listOf("b1", "b2"), viewModel.uiState.value.builds.map { it.build.id })
    }

    @Test
    fun `given multiple workflows when select workflow then only that workflow builds are shown`() = runTest {
        stubSources(
            build.copy(id = "b1", workflowId = "android-release", branch = "main"),
            build.copy(id = "b2", workflowId = "android-debug", branch = "main")
        )
        val viewModel = viewModel()
        viewModel.init()

        viewModel.onAction(BuildsViewModel.Action.SelectWorkflow("android-debug"))

        assertEquals("android-debug", viewModel.uiState.value.selectedWorkflow)
        assertEquals(listOf("b2"), viewModel.uiState.value.builds.map { it.build.id })
    }

    @Test
    fun `given builds when init then workflow options are distinct and sorted`() = runTest {
        stubSources(
            build.copy(id = "b1", workflowId = "android-release"),
            build.copy(id = "b2", workflowId = "android-release"),
            build.copy(id = "b3", workflowId = "ios-release")
        )
        val viewModel = viewModel()

        viewModel.init()

        assertEquals(listOf("android-release", "ios-release"), viewModel.uiState.value.workflows)
    }

    @Test
    fun `given selected branch when downloads update then filter is preserved`() = runTest {
        stubSources(
            build.copy(id = "b1", branch = "main"),
            build.copy(id = "b2", branch = "feature/login")
        )
        val viewModel = viewModel()
        viewModel.init()
        viewModel.onAction(BuildsViewModel.Action.SelectBranch("feature/login"))

        downloads.emit(
            listOf(ArtifactDownload("a1", "b2", BuildArtifact.DownloadStatus.DOWNLOADING, progress = 0.5f, downloadedBytes = 24_000_000))
        )

        assertEquals(listOf("b2"), viewModel.uiState.value.builds.map { it.build.id })
    }

    @Test
    fun `given apk filter selected when filter changes then only builds with apk artifacts are shown`() = runTest {
        val aabArtifact = BuildArtifact("aab1", "app-release.aab", 30_000_000)
        stubSources(
            build.copy(id = "b1", artifacts = listOf(artifact, aabArtifact)),
            build.copy(id = "b2", artifacts = listOf(aabArtifact))
        )
        val viewModel = viewModel()
        viewModel.init()

        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.APK_ONLY))

        assertEquals(BuildsViewModel.ArtifactFilter.APK_ONLY, viewModel.uiState.value.selectedArtifactFilter)
        assertEquals(listOf("b1"), viewModel.uiState.value.builds.map { it.build.id })
        assertEquals(listOf("a1"), viewModel.uiState.value.builds.single().build.artifacts.map { it.id })
    }

    @Test
    fun `given apk filter selected when all artifacts filter selected then every build is shown`() = runTest {
        val aabArtifact = BuildArtifact("aab1", "app-release.aab", 30_000_000)
        stubSources(
            build.copy(id = "b1", artifacts = listOf(artifact)),
            build.copy(id = "b2", artifacts = listOf(aabArtifact))
        )
        val viewModel = viewModel()
        viewModel.init()
        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.APK_ONLY))

        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.ALL))

        assertEquals(BuildsViewModel.ArtifactFilter.ALL, viewModel.uiState.value.selectedArtifactFilter)
        assertEquals(listOf("b1", "b2"), viewModel.uiState.value.builds.map { it.build.id })
        assertEquals(listOf("aab1"), viewModel.uiState.value.builds[1].build.artifacts.map { it.id })
    }

    @Test
    fun `given uppercase apk extension when apk filter selected then artifact is shown`() = runTest {
        stubSources(build.copy(artifacts = listOf(artifact.copy(name = "APP-RELEASE.APK"))))
        val viewModel = viewModel()
        viewModel.init()

        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.APK_ONLY))

        assertEquals(listOf("b1"), viewModel.uiState.value.builds.map { it.build.id })
    }

    @Test
    fun `given apk filter selected when downloads update then filter is preserved`() = runTest {
        val aabArtifact = BuildArtifact("aab1", "app-release.aab", 30_000_000)
        stubSources(
            build.copy(id = "b1", artifacts = listOf(artifact)),
            build.copy(id = "b2", artifacts = listOf(aabArtifact))
        )
        val viewModel = viewModel()
        viewModel.init()
        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.APK_ONLY))

        downloads.emit(
            listOf(ArtifactDownload("a1", "b1", BuildArtifact.DownloadStatus.DOWNLOADING, progress = 0.5f, downloadedBytes = 24_000_000))
        )

        assertEquals(listOf("b1"), viewModel.uiState.value.builds.map { it.build.id })
    }

    @Test
    fun `given compatible filter selected when filter changes then only apks matching this device are shown`() = runTest {
        val arm64Apk = BuildArtifact("a64", "app-arm64-v8a-release.apk", 48_000_000)
        val x86Apk = BuildArtifact("ax86", "app-x86_64-release.apk", 48_000_000)
        val aabArtifact = BuildArtifact("aab1", "app-release.aab", 30_000_000)
        stubSources(build.copy(artifacts = listOf(arm64Apk, x86Apk, aabArtifact)))
        val viewModel = viewModel()
        viewModel.init()

        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.COMPATIBLE_APK))

        assertEquals(BuildsViewModel.ArtifactFilter.COMPATIBLE_APK, viewModel.uiState.value.selectedArtifactFilter)
        assertEquals(listOf("a64"), viewModel.uiState.value.builds.single().build.artifacts.map { it.id })
    }

    @Test
    fun `given build without compatible apk when compatible filter selected then build is hidden`() = runTest {
        val arm64Apk = BuildArtifact("a64", "app-arm64-v8a-release.apk", 48_000_000)
        val x86Apk = BuildArtifact("ax86", "app-x86_64-release.apk", 48_000_000)
        stubSources(
            build.copy(id = "b1", artifacts = listOf(arm64Apk)),
            build.copy(id = "b2", artifacts = listOf(x86Apk))
        )
        val viewModel = viewModel()
        viewModel.init()

        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.COMPATIBLE_APK))

        assertEquals(listOf("b1"), viewModel.uiState.value.builds.map { it.build.id })
    }

    @Test
    fun `given universal apk when compatible filter selected then it is shown`() = runTest {
        val universalApk = BuildArtifact("auni", "app-release.apk", 48_000_000)
        stubSources(build.copy(artifacts = listOf(universalApk)))
        val viewModel = viewModel()
        viewModel.init()

        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.COMPATIBLE_APK))

        assertEquals(listOf("auni"), viewModel.uiState.value.builds.single().build.artifacts.map { it.id })
    }

    @Test
    fun `given compatible filter selected when apk filter selected then incompatible apks are shown`() = runTest {
        val arm64Apk = BuildArtifact("a64", "app-arm64-v8a-release.apk", 48_000_000)
        val x86Apk = BuildArtifact("ax86", "app-x86_64-release.apk", 48_000_000)
        stubSources(build.copy(artifacts = listOf(arm64Apk, x86Apk)))
        val viewModel = viewModel()
        viewModel.init()
        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.COMPATIBLE_APK))

        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.APK_ONLY))

        assertEquals(BuildsViewModel.ArtifactFilter.APK_ONLY, viewModel.uiState.value.selectedArtifactFilter)
        assertEquals(
            listOf("a64", "ax86"),
            viewModel.uiState.value.builds.single().build.artifacts.map { it.id }
        )
    }

    @Test
    fun `given compatible filter selected when all artifacts selected then every artifact is shown`() = runTest {
        val arm64Apk = BuildArtifact("a64", "app-arm64-v8a-release.apk", 48_000_000)
        val aabArtifact = BuildArtifact("aab1", "app-release.aab", 30_000_000)
        stubSources(build.copy(artifacts = listOf(arm64Apk, aabArtifact)))
        val viewModel = viewModel()
        viewModel.init()
        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.COMPATIBLE_APK))

        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.ALL))

        assertEquals(BuildsViewModel.ArtifactFilter.ALL, viewModel.uiState.value.selectedArtifactFilter)
        assertEquals(
            listOf("a64", "aab1"),
            viewModel.uiState.value.builds.single().build.artifacts.map { it.id }
        )
    }

    @Test
    fun `given compatible filter selected when downloads update then filter is preserved`() = runTest {
        val arm64Apk = BuildArtifact("a64", "app-arm64-v8a-release.apk", 48_000_000)
        val x86Apk = BuildArtifact("ax86", "app-x86_64-release.apk", 48_000_000)
        stubSources(build.copy(id = "b1", artifacts = listOf(arm64Apk)), build.copy(id = "b2", artifacts = listOf(x86Apk)))
        val viewModel = viewModel()
        viewModel.init()
        viewModel.onAction(BuildsViewModel.Action.SelectArtifactFilter(BuildsViewModel.ArtifactFilter.COMPATIBLE_APK))

        downloads.emit(
            listOf(ArtifactDownload("a64", "b1", BuildArtifact.DownloadStatus.DOWNLOADING, progress = 0.5f, downloadedBytes = 24_000_000))
        )

        assertEquals(listOf("b1"), viewModel.uiState.value.builds.map { it.build.id })
    }

    @Test
    fun `given settings clicked when action then navigation event is emitted`() = runTest {
        stubSources()
        val viewModel = viewModel()
        viewModel.init()

        viewModel.navigationEvent.test {
            viewModel.onSettingsClicked()
            assertEquals(BuildsViewModel.NavigationEvent.NavigateToSettings, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given app selector clicked when action then navigation event is emitted`() = runTest {
        stubSources()
        val viewModel = viewModel()
        viewModel.init()

        viewModel.navigationEvent.test {
            viewModel.onAppSelectorClicked()
            assertEquals(BuildsViewModel.NavigationEvent.NavigateToApps, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
