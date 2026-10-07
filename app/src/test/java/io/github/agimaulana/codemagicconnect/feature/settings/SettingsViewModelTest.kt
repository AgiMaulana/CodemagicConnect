package io.github.agimaulana.codemagicconnect.feature.settings

import app.cash.turbine.test
import io.github.agimaulana.codemagicconnect.core.testing.CoroutineMainDispatcherRule
import io.github.agimaulana.codemagicconnect.domain.model.Settings
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ClearDefaultApplicationUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ClearDownloadedFilesUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ObserveSettingsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.RemoveTokenUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.SetDeleteApkAfterInstallUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.SetWifiOnlyUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.TestConnectionUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule()

    private val observeSettingsUseCase = mockk<ObserveSettingsUseCase>()
    private val testConnectionUseCase = mockk<TestConnectionUseCase>()
    private val removeTokenUseCase = mockk<RemoveTokenUseCase>()
    private val clearDefaultApplicationUseCase = mockk<ClearDefaultApplicationUseCase>()
    private val setWifiOnlyUseCase = mockk<SetWifiOnlyUseCase>()
    private val setDeleteApkAfterInstallUseCase = mockk<SetDeleteApkAfterInstallUseCase>()
    private val clearDownloadedFilesUseCase = mockk<ClearDownloadedFilesUseCase>()

    private val viewModel = SettingsViewModel(
        observeSettingsUseCase = observeSettingsUseCase,
        testConnectionUseCase = testConnectionUseCase,
        removeTokenUseCase = removeTokenUseCase,
        clearDefaultApplicationUseCase = clearDefaultApplicationUseCase,
        setWifiOnlyUseCase = setWifiOnlyUseCase,
        setDeleteApkAfterInstallUseCase = setDeleteApkAfterInstallUseCase,
        clearDownloadedFilesUseCase = clearDownloadedFilesUseCase
    )

    private val settings = Settings(
        isTokenConnected = true,
        token = "cm_abcdef1234567890",
        tokenAddedAtEpochMillis = 1_700_000_000_000L,
        tokenLastVerifiedAtEpochMillis = 1_700_000_000_000L,
        defaultAppId = "1",
        defaultAppName = "acme-mobile",
        wifiOnly = false,
        deleteApkAfterInstall = true,
        downloadedFilesBytes = 214L * 1024L * 1024L
    )

    @Test
    fun `given settings when init then state is mapped`() = runTest {
        every { observeSettingsUseCase() } returns flowOf(settings)

        viewModel.init()

        val state = viewModel.uiState.value
        assertTrue(state.isTokenConnected)
        assertTrue(state.tokenObfuscated.startsWith("cm_"))
        assertTrue(state.tokenObfuscated.endsWith("7890"))
        assertEquals("acme-mobile", state.defaultAppName)
        assertEquals(false, state.isWifiOnly)
        assertEquals(true, state.isDeleteApkAfterInstall)
        assertEquals("214 MB", state.downloadedFilesSize)
    }

    @Test
    fun `given no token when init then obfuscated value shows not connected`() = runTest {
        every { observeSettingsUseCase() } returns flowOf(
            settings.copy(isTokenConnected = false, token = null, downloadedFilesBytes = 0L)
        )

        viewModel.init()

        assertEquals("Not connected", viewModel.uiState.value.tokenObfuscated)
        assertEquals("0 MB", viewModel.uiState.value.downloadedFilesSize)
    }

    @Test
    fun `given success when test connection then snackbar is shown`() = runTest {
        every { observeSettingsUseCase() } returns flowOf(settings)
        coEvery { testConnectionUseCase() } returns true
        viewModel.init()

        viewModel.uiEvent.test {
            viewModel.onAction(SettingsViewModel.Action.TestConnection)
            assertEquals(
                SettingsViewModel.UiEvent.ShowSnackbar("Connection successful"),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given toggles when changed then they are persisted`() = runTest {
        every { observeSettingsUseCase() } returns flowOf(settings)
        coEvery { setWifiOnlyUseCase(false) } returns Unit
        coEvery { setDeleteApkAfterInstallUseCase(true) } returns Unit
        viewModel.init()

        viewModel.onAction(SettingsViewModel.Action.ToggleWifiOnly(false))
        viewModel.onAction(SettingsViewModel.Action.ToggleDeleteApkAfterInstall(true))

        coVerify(exactly = 1) { setWifiOnlyUseCase(false) }
        coVerify(exactly = 1) { setDeleteApkAfterInstallUseCase(true) }
    }

    @Test
    fun `given remove token when action then it is removed and navigates to connect`() = runTest {
        every { observeSettingsUseCase() } returns flowOf(settings)
        coEvery { removeTokenUseCase() } returns Unit
        viewModel.init()

        viewModel.navigationEvent.test {
            viewModel.onAction(SettingsViewModel.Action.RemoveTokenAndSignOut)
            assertEquals(SettingsViewModel.NavigationEvent.NavigateToConnect, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { removeTokenUseCase() }
    }

    @Test
    fun `given downloaded files when cleared then use case runs and snackbar shown`() = runTest {
        every { observeSettingsUseCase() } returns flowOf(settings)
        coEvery { clearDownloadedFilesUseCase() } returns Unit
        viewModel.init()

        viewModel.uiEvent.test {
            viewModel.onAction(SettingsViewModel.Action.ClearDownloadedFiles)
            assertEquals(
                SettingsViewModel.UiEvent.ShowSnackbar("Downloaded files cleared"),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { clearDownloadedFilesUseCase() }
    }
}
