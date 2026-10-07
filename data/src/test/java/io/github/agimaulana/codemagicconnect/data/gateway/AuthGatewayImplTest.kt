package io.github.agimaulana.codemagicconnect.data.gateway

import io.github.agimaulana.codemagicconnect.core.dispatcher.DispatcherProvider
import io.github.agimaulana.codemagicconnect.data.local.dao.AuthTokenDao
import io.github.agimaulana.codemagicconnect.data.local.entity.AuthTokenEntity
import io.github.agimaulana.codemagicconnect.data.remote.api.CodemagicApiService
import io.github.agimaulana.codemagicconnect.data.remote.dto.PageDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.UserAppDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class AuthGatewayImplTest {

    private val api = mockk<CodemagicApiService>()
    private val authTokenDao = mockk<AuthTokenDao>()
    private val dispatcherProvider = object : DispatcherProvider {
        override fun io(): CoroutineDispatcher = Dispatchers.Unconfined
    }
    private val gateway = AuthGatewayImpl(api, authTokenDao, dispatcherProvider)

    @Test
    fun `given accepted token when verify then true`() = runTest {
        coEvery { api.getUserAppsWithToken("token") } returns PageDto<UserAppDto>()

        assertTrue(gateway.verifyToken("token"))
    }

    @Test
    fun `given unauthorized response when verify then false`() = runTest {
        coEvery { api.getUserAppsWithToken("token") } throws httpException(401)

        assertFalse(gateway.verifyToken("token"))
    }

    @Test
    fun `given server error when verify then rethrows`() = runTest {
        coEvery { api.getUserAppsWithToken("token") } throws httpException(500)

        val error = runCatching { gateway.verifyToken("token") }.exceptionOrNull()

        assertTrue(error is HttpException)
    }

    @Test
    fun `given existing token when save then preserves added date and refreshes verification`() = runTest {
        coEvery { authTokenDao.get() } returns AuthTokenEntity(
            token = "old",
            addedAtEpochMillis = 100L,
            lastVerifiedAtEpochMillis = 100L
        )
        coEvery { authTokenDao.upsert(any()) } returns Unit

        gateway.saveToken("new")

        coVerify {
            authTokenDao.upsert(
                match { it.token == "new" && it.addedAtEpochMillis == 100L && it.lastVerifiedAtEpochMillis > 100L }
            )
        }
    }

    private fun httpException(code: Int): HttpException {
        val body = "".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }
}
