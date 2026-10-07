package io.github.agimaulana.codemagicconnect.data.gateway

import io.github.agimaulana.codemagicconnect.core.dispatcher.DispatcherProvider
import io.github.agimaulana.codemagicconnect.data.local.dao.AuthTokenDao
import io.github.agimaulana.codemagicconnect.data.local.entity.AuthTokenEntity
import io.github.agimaulana.codemagicconnect.data.local.mapper.toDomain
import io.github.agimaulana.codemagicconnect.data.remote.api.CodemagicApiService
import io.github.agimaulana.codemagicconnect.domain.gateway.AuthGateway
import io.github.agimaulana.codemagicconnect.domain.model.TokenInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthGatewayImpl @Inject constructor(
    private val api: CodemagicApiService,
    private val authTokenDao: AuthTokenDao,
    private val dispatcherProvider: DispatcherProvider
) : AuthGateway {

    override suspend fun verifyToken(token: String): Boolean =
        try {
            withContext(dispatcherProvider.io()) { api.getUserAppsWithToken(token) }
            true
        } catch (unauthorized: HttpException) {
            if (unauthorized.code() == HTTP_UNAUTHORIZED) false else throw unauthorized
        }

    override suspend fun saveToken(token: String) {
        val now = System.currentTimeMillis()
        val previous = authTokenDao.get()
        authTokenDao.upsert(
            AuthTokenEntity(
                token = token,
                addedAtEpochMillis = previous?.addedAtEpochMillis ?: now,
                lastVerifiedAtEpochMillis = now
            )
        )
    }

    override fun observeToken(): Flow<TokenInfo?> = authTokenDao.observe().map { it?.toDomain() }

    override suspend fun removeToken() {
        authTokenDao.delete()
    }

    companion object {
        private const val HTTP_UNAUTHORIZED = 401
    }
}
