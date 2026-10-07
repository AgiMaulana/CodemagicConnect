package io.github.agimaulana.codemagicconnect.domain.usecase.apps

import io.github.agimaulana.codemagicconnect.domain.gateway.ApplicationsGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.PreferencesGateway
import io.github.agimaulana.codemagicconnect.domain.model.AppPreferences
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import app.cash.turbine.test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AppsUseCasesTest {

    private val applicationsGateway = mockk<ApplicationsGateway>()
    private val preferencesGateway = mockk<PreferencesGateway>()

    private val app = CodemagicApplication("1", "acme", "github.com/acme", null)

    @Test
    fun `given applications when get applications then returns gateway result`() = runTest {
        coEvery { applicationsGateway.getApplications() } returns listOf(app)

        assertEquals(listOf(app), GetApplicationsUseCaseImpl(applicationsGateway).invoke())
    }

    @Test
    fun `given application when set default then persists id and name`() = runTest {
        coEvery { preferencesGateway.setDefaultApplication("1", "acme") } returns Unit

        SetDefaultApplicationUseCaseImpl(preferencesGateway).invoke(app)

        coVerify(exactly = 1) { preferencesGateway.setDefaultApplication("1", "acme") }
    }

    @Test
    fun `given app id when toggle favorite then delegates to preferences`() = runTest {
        coEvery { preferencesGateway.toggleFavorite("1") } returns Unit

        ToggleFavoriteApplicationUseCaseImpl(preferencesGateway).invoke("1")

        coVerify(exactly = 1) { preferencesGateway.toggleFavorite("1") }
    }

    @Test
    fun `given preferences when observe then emits open app automatically flag`() = runTest {
        every { preferencesGateway.observePreferences() } returns flowOf(AppPreferences(openAppAutomatically = true))

        ObserveAppPreferencesUseCaseImpl(preferencesGateway).invoke().test {
            assertEquals(true, awaitItem().openAppAutomatically)
            awaitComplete()
        }
    }

    @Test
    fun `given no favorites when observe favorites then emits empty set`() = runTest {
        every { preferencesGateway.observeFavoriteIds() } returns flowOf(emptySet())

        ObserveFavoriteApplicationsUseCaseImpl(preferencesGateway).invoke().test {
            assertFalse(awaitItem().contains("1"))
            awaitComplete()
        }
    }
}
