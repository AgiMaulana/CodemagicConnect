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
        buildTimestampFormatter = BuildTimestampFormatter()
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
