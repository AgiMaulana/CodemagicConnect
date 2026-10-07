package io.github.agimaulana.codemagicconnect.domain.usecase.settings

import io.github.agimaulana.codemagicconnect.domain.gateway.ArtifactsGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.AuthGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.PreferencesGateway
import io.github.agimaulana.codemagicconnect.domain.model.AppPreferences
import io.github.agimaulana.codemagicconnect.domain.model.TokenInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import app.cash.turbine.test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsUseCasesTest {

    private val authGateway = mockk<AuthGateway>()
    private val preferencesGateway = mockk<PreferencesGateway>()
    private val artifactsGateway = mockk<ArtifactsGateway>()

    @Test
    fun `given stored token and preferences when observe settings then combines state`() = runTest {
        every { authGateway.observeToken() } returns flowOf(TokenInfo("cm_x", 10L, 20L))
        every {
            preferencesGateway.observePreferences()
        } returns flowOf(AppPreferences(wifiOnly = false, defaultAppName = "acme", defaultAppId = "1"))
        coEvery { artifactsGateway.downloadedFilesSizeBytes() } returns 2048L

        ObserveSettingsUseCaseImpl(authGateway, preferencesGateway, artifactsGateway).invoke().test {
            val settings = awaitItem()
            assertTrue(settings.isTokenConnected)
            assertEquals("cm_x", settings.token)
            assertEquals(10L, settings.tokenAddedAtEpochMillis)
            assertEquals("acme", settings.defaultAppName)
            assertFalse(settings.wifiOnly)
            assertEquals(2048L, settings.downloadedFilesBytes)
            awaitComplete()
        }
    }

    @Test
    fun `given stored token when test connection then returns verification result`() = runTest {
        coEvery { authGateway.observeToken() } returns flowOf(TokenInfo("cm", 1L, 1L))
        coEvery { authGateway.verifyToken("cm") } returns true

        assertTrue(TestConnectionUseCaseImpl(authGateway).invoke())
    }

    @Test
    fun `given no token when test connection then false without calling api`() = runTest {
        coEvery { authGateway.observeToken() } returns flowOf(null)

        assertFalse(TestConnectionUseCaseImpl(authGateway).invoke())
        coVerify(exactly = 0) { authGateway.verifyToken(any()) }
    }

    @Test
    fun `given action when remove token then delegates to auth gateway`() = runTest {
        coEvery { authGateway.removeToken() } returns Unit

        RemoveTokenUseCaseImpl(authGateway).invoke()

        coVerify(exactly = 1) { authGateway.removeToken() }
    }

    @Test
    fun `given toggles when wifi and delete apk setters then delegate to preferences`() = runTest {
        coEvery { preferencesGateway.setWifiOnly(false) } returns Unit
        coEvery { preferencesGateway.setDeleteApkAfterInstall(true) } returns Unit

        SetWifiOnlyUseCaseImpl(preferencesGateway).invoke(false)
        SetDeleteApkAfterInstallUseCaseImpl(preferencesGateway).invoke(true)

        coVerify(exactly = 1) { preferencesGateway.setWifiOnly(false) }
        coVerify(exactly = 1) { preferencesGateway.setDeleteApkAfterInstall(true) }
    }

    @Test
    fun `given action when clear downloaded files then delegates to artifacts gateway`() = runTest {
        coEvery { artifactsGateway.clearDownloadedFiles() } returns Unit

        ClearDownloadedFilesUseCaseImpl(artifactsGateway).invoke()

        coVerify(exactly = 1) { artifactsGateway.clearDownloadedFiles() }
    }
}
