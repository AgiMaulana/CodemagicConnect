package io.github.agimaulana.codemagicconnect.domain.gateway

import io.github.agimaulana.codemagicconnect.domain.model.TokenInfo
import kotlinx.coroutines.flow.Flow

interface AuthGateway {

    /**
     * Checks the given token against the Codemagic API without persisting it.
     * @return true when the token is accepted, false when it is rejected.
     */
    suspend fun verifyToken(token: String): Boolean

    suspend fun saveToken(token: String)

    fun observeToken(): Flow<TokenInfo?>

    suspend fun removeToken()
}
