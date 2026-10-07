package io.github.agimaulana.codemagicconnect.feature.connect

import app.cash.turbine.test
import io.github.agimaulana.codemagicconnect.core.testing.CoroutineMainDispatcherRule
import io.github.agimaulana.codemagicconnect.domain.model.AppPreferences
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ObserveAppPreferencesUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.connect.InvalidTokenException
import io.github.agimaulana.codemagicconnect.domain.usecase.connect.IsTokenStoredUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.connect.VerifyAndSaveTokenUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ConnectViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule()

    private val verifyAndSaveTokenUseCase = mockk<VerifyAndSaveTokenUseCase>()
    private val isTokenStoredUseCase = mockk<IsTokenStoredUseCase>()
    private val observeAppPreferencesUseCase = mockk<ObserveAppPreferencesUseCase>()

    private val viewModel = ConnectViewModel(
        verifyAndSaveTokenUseCase,
        isTokenStoredUseCase,
        observeAppPreferencesUseCase
    )

    @Test
    fun `given no stored token when init then stays on connect`() = runTest {
        coEvery { isTokenStoredUseCase() } returns false

        viewModel.navigationEvent.test {
            viewModel.init()
            expectNoEvents()
        }
    }

    @Test
    fun `given open connect screen when checking token availability then save and verify button shows loading`() = runTest {
        val tokenCheckGate = CompletableDeferred<Unit>()
        coEvery { isTokenStoredUseCase() } coAnswers { tokenCheckGate.await(); false }

        viewModel.uiState.test {
            assertFalse(awaitItem().isCheckingStoredToken)
            viewModel.init()
            assertTrue(awaitItem().isCheckingStoredToken)
            tokenCheckGate.complete(Unit)
            assertFalse(awaitItem().isCheckingStoredToken)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given stored token and no default app selected when init then navigates to apps`() = runTest {
        coEvery { isTokenStoredUseCase() } returns true
        every { observeAppPreferencesUseCase() } returns flowOf(AppPreferences())

        viewModel.navigationEvent.test {
            viewModel.init()
            assertEquals(ConnectViewModel.NavigationEvent.NavigateToApps, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given stored token and default app selected when init then navigates to builds`() = runTest {
        coEvery { isTokenStoredUseCase() } returns true
        every { observeAppPreferencesUseCase() } returns flowOf(
            AppPreferences(defaultAppId = "app_123", defaultAppName = "My App")
        )

        viewModel.navigationEvent.test {
            viewModel.init()
            val event = awaitItem()
            assertEquals(
                ConnectViewModel.NavigationEvent.NavigateToBuilds(appId = "app_123"),
                event as ConnectViewModel.NavigationEvent.NavigateToBuilds
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given valid token when verify and save then loading stops and navigates`() = runTest {
        coEvery { verifyAndSaveTokenUseCase("cm_valid") } returns Unit

        viewModel.onAction(ConnectViewModel.Action.UpdateToken("cm_valid"))
        viewModel.navigationEvent.test {
            viewModel.onAction(ConnectViewModel.Action.VerifyAndSaveToken)
            assertEquals(ConnectViewModel.NavigationEvent.NavigateToApps, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given token typed when verifying and saving then save and verify button shows loading`() = runTest {
        val verificationGate = CompletableDeferred<Unit>()
        coEvery { verifyAndSaveTokenUseCase("cm_valid") } coAnswers { verificationGate.await() }

        viewModel.onAction(ConnectViewModel.Action.UpdateToken("cm_valid"))
        viewModel.uiState.test {
            assertFalse(awaitItem().isLoading)
            viewModel.onAction(ConnectViewModel.Action.VerifyAndSaveToken)
            assertTrue(awaitItem().isLoading)
            verificationGate.complete(Unit)
            assertFalse(awaitItem().isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given rejected token when verify and save then error is emitted`() = runTest {
        coEvery { verifyAndSaveTokenUseCase("bad") } throws InvalidTokenException()

        viewModel.onAction(ConnectViewModel.Action.UpdateToken("bad"))
        viewModel.uiEvent.test {
            viewModel.onAction(ConnectViewModel.Action.VerifyAndSaveToken)
            val event = awaitItem()
            assertEquals(InvalidTokenException().message, (event as ConnectViewModel.UiEvent.ShowError).message)
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given browser action when emitted then settings url is navigated`() = runTest {
        viewModel.navigationEvent.test {
            viewModel.onAction(ConnectViewModel.Action.OpenCodemagicInBrowser)
            val event = awaitItem() as ConnectViewModel.NavigationEvent.OpenBrowser
            assertEquals("https://codemagic.io/settings", event.url)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given token input when update then state reflects it`() {
        viewModel.onAction(ConnectViewModel.Action.UpdateToken("cm_123"))

        assertEquals("cm_123", viewModel.uiState.value.token)
        coVerify(exactly = 0) { verifyAndSaveTokenUseCase(any()) }
    }
}
