package io.github.agimaulana.codemagicconnect

import app.cash.turbine.test
import io.github.agimaulana.codemagicconnect.core.testing.CoroutineMainDispatcherRule
import io.github.agimaulana.codemagicconnect.domain.model.ThemeMode
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ObserveThemeModeUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule()

    private val observeThemeModeUseCase = mockk<ObserveThemeModeUseCase>()
    private val viewModel by lazy { MainViewModel(observeThemeModeUseCase) }

    @Test
    fun `given stored theme mode when observed then emits theme mode`() = runTest {
        every { observeThemeModeUseCase() } returns flowOf(ThemeMode.DARK)
        viewModel.init()

        viewModel.themeMode.test {
            assertEquals(ThemeMode.DARK, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
