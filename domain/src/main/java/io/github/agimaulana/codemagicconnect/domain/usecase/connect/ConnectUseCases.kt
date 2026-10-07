package io.github.agimaulana.codemagicconnect.domain.usecase.connect

import io.github.agimaulana.codemagicconnect.domain.gateway.AuthGateway
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface VerifyAndSaveTokenUseCase {
    /**
     * Verifies [token] against Codemagic and persists it when accepted.
     * @throws InvalidTokenException when the API rejects the token.
     */
    suspend operator fun invoke(token: String)
}

interface IsTokenStoredUseCase {
    suspend operator fun invoke(): Boolean
}

class InvalidTokenException : Exception("The token was rejected by Codemagic. Check the token and try again.")

internal class VerifyAndSaveTokenUseCaseImpl @Inject constructor(
    private val authGateway: AuthGateway
) : VerifyAndSaveTokenUseCase {

    override suspend fun invoke(token: String) {
        val trimmedToken = token.trim()
        require(trimmedToken.isNotEmpty()) { "Enter an API token first." }
        if (!authGateway.verifyToken(trimmedToken)) {
            throw InvalidTokenException()
        }
        authGateway.saveToken(trimmedToken)
    }
}

internal class IsTokenStoredUseCaseImpl @Inject constructor(
    private val authGateway: AuthGateway
) : IsTokenStoredUseCase {

    override suspend fun invoke(): Boolean = authGateway.observeToken().first() != null
}
