package io.github.agimaulana.codemagicconnect.feature.apps

import app.cash.turbine.test
import io.github.agimaulana.codemagicconnect.core.testing.CoroutineMainDispatcherRule
import io.github.agimaulana.codemagicconnect.domain.model.AppPreferences
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.GetApplicationsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ObserveAppPreferencesUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ObserveFavoriteApplicationsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.SetDefaultApplicationUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.SetOpenAppAutomaticallyUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ToggleFavoriteApplicationUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppsViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule()

    private val getApplicationsUseCase = mockk<GetApplicationsUseCase>()
    private val setDefaultApplicationUseCase = mockk<SetDefaultApplicationUseCase>()
    private val toggleFavoriteApplicationUseCase = mockk<ToggleFavoriteApplicationUseCase>()
    private val observeAppPreferencesUseCase = mockk<ObserveAppPreferencesUseCase>()
    private val observeFavoriteApplicationsUseCase = mockk<ObserveFavoriteApplicationsUseCase>()
    private val setOpenAppAutomaticallyUseCase = mockk<SetOpenAppAutomaticallyUseCase>()

    private val viewModel = AppsViewModel(
        getApplicationsUseCase = getApplicationsUseCase,
        setDefaultApplicationUseCase = setDefaultApplicationUseCase,
        toggleFavoriteApplicationUseCase = toggleFavoriteApplicationUseCase,
        observeAppPreferencesUseCase = observeAppPreferencesUseCase,
        observeFavoriteApplicationsUseCase = observeFavoriteApplicationsUseCase,
        setOpenAppAutomaticallyUseCase = setOpenAppAutomaticallyUseCase
    )

    private val apps = listOf(
        CodemagicApplication("1", "acme", "github.com/acme", null, teamId = "team-9"),
        CodemagicApplication("2", "kiosk", "gitlab.com/acme/kiosk", null, teamId = "team-9")
    )

    private fun stubSources(
        applications: List<CodemagicApplication> = apps,
        preferences: AppPreferences = AppPreferences(),
        favorites: Set<String> = emptySet()
    ) {
        coEvery { getApplicationsUseCase() } returns applications
        every { observeAppPreferencesUseCase() } returns flowOf(preferences)
        every { observeFavoriteApplicationsUseCase() } returns flowOf(favorites)
    }

    @Test
    fun `given applications when init then state is populated and loading stops`() = runTest {
        stubSources(preferences = AppPreferences(defaultAppId = "2", openAppAutomatically = true))

        viewModel.init()

        assertEquals(2, viewModel.uiState.value.apps.size)
        assertTrue(viewModel.uiState.value.openAppAutomatically)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.apps.first { it.id == "2" }.isDefault)
    }

    @Test
    fun `given favorites when init then matching applications are flagged`() = runTest {
        stubSources(favorites = setOf("1"))

        viewModel.init()

        assertTrue(viewModel.uiState.value.apps.first { it.id == "1" }.isFavorite)
    }

    @Test
    fun `given application when selected then it becomes default and is persisted`() = runTest {
        stubSources()
        coEvery { setDefaultApplicationUseCase(any()) } returns Unit
        viewModel.init()

        viewModel.onAction(AppsViewModel.Action.SelectApp(apps[1]))

        assertTrue(viewModel.uiState.value.apps.first { it.id == "2" }.isDefault)
        coVerify(exactly = 1) { setDefaultApplicationUseCase(apps[1]) }
    }

    @Test
    fun `given application when favorite toggled then it is persisted`() = runTest {
        stubSources()
        coEvery { toggleFavoriteApplicationUseCase("1") } returns Unit
        viewModel.init()

        viewModel.onAction(AppsViewModel.Action.ToggleFavorite(apps[0]))

        assertTrue(viewModel.uiState.value.apps.first { it.id == "1" }.isFavorite)
        coVerify(exactly = 1) { toggleFavoriteApplicationUseCase("1") }
    }

    @Test
    fun `given open automatically toggled when action then it is persisted`() = runTest {
        stubSources()
        coEvery { setOpenAppAutomaticallyUseCase(true) } returns Unit
        viewModel.init()

        viewModel.onAction(AppsViewModel.Action.ToggleOpenAutomatically(true))

        assertTrue(viewModel.uiState.value.openAppAutomatically)
        coVerify(exactly = 1) { setOpenAppAutomaticallyUseCase(true) }
    }

    @Test
    fun `given default application when continue then navigate to its builds`() = runTest {
        stubSources(preferences = AppPreferences(defaultAppId = "2"))
        viewModel.init()

        viewModel.navigationEvent.test {
            viewModel.onAction(AppsViewModel.Action.ContinueToBuilds)
            assertEquals(AppsViewModel.NavigationEvent.NavigateToBuilds("2"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given applications fail to load when init then an error is emitted`() = runTest {
        coEvery { getApplicationsUseCase() } throws IllegalStateException("Network down")
        every { observeAppPreferencesUseCase() } returns flowOf(AppPreferences())
        every { observeFavoriteApplicationsUseCase() } returns flowOf(emptySet())

        viewModel.uiEvent.test {
            viewModel.init()
            assertEquals(
                AppsViewModel.UiEvent.ShowError("Network down"),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}
