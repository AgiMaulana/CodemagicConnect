package io.github.agimaulana.codemagicconnect.domain.usecase.connect

import io.github.agimaulana.codemagicconnect.domain.gateway.AuthGateway
import io.github.agimaulana.codemagicconnect.domain.model.TokenInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConnectUseCasesTest {

    private val authGateway = mockk<AuthGateway>()

    @Test
    fun `given accepted token when verify and save then token is persisted`() = runTest {
        coEvery { authGateway.verifyToken("cm_valid") } returns true
        coEvery { authGateway.saveToken("cm_valid") } returns Unit

        VerifyAndSaveTokenUseCaseImpl(authGateway).invoke("  cm_valid  ")

        coVerify(exactly = 1) { authGateway.saveToken("cm_valid") }
    }

    @Test
    fun `given rejected token when verify and save then InvalidTokenException and nothing persisted`() = runTest {
        coEvery { authGateway.verifyToken("bad") } returns false

        val error = runCatching { VerifyAndSaveTokenUseCaseImpl(authGateway).invoke("bad") }.exceptionOrNull()

        assertTrue(error is InvalidTokenException)
        coVerify(exactly = 0) { authGateway.saveToken(any()) }
    }

    @Test
    fun `given stored token when is token stored then true`() = runTest {
        coEvery { authGateway.observeToken() } returns flowOf(TokenInfo("cm", 1L, 2L))

        assertTrue(IsTokenStoredUseCaseImpl(authGateway).invoke())
    }

    @Test
    fun `given no token when is token stored then false`() = runTest {
        coEvery { authGateway.observeToken() } returns flowOf(null)

        assertFalse(IsTokenStoredUseCaseImpl(authGateway).invoke())
    }
}
