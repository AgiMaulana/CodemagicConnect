package io.github.agimaulana.codemagicconnect.data.remote.interceptor

import io.github.agimaulana.codemagicconnect.data.local.dao.AuthTokenDao
import io.github.agimaulana.codemagicconnect.data.remote.api.CodemagicApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ApiTokenInterceptor @Inject constructor(
    private val authTokenDao: AuthTokenDao
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!request.header(CodemagicApiService.API_TOKEN_HEADER).isNullOrBlank()) {
            return chain.proceed(request)
        }
        val token = runBlocking { authTokenDao.get()?.token }
            ?: return chain.proceed(request)
        return chain.proceed(
            request.newBuilder()
                .header(CodemagicApiService.API_TOKEN_HEADER, token)
                .build()
        )
    }
}
