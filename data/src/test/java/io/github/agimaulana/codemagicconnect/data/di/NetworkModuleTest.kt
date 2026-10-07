package io.github.agimaulana.codemagicconnect.data.di

import io.github.agimaulana.codemagicconnect.data.local.dao.AuthTokenDao
import io.github.agimaulana.codemagicconnect.data.remote.interceptor.ApiTokenInterceptor
import io.mockk.mockk
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkModuleTest {

    private val apiTokenInterceptor = ApiTokenInterceptor(mockk<AuthTokenDao>(relaxed = true))

    @Test
    fun `given download client when built then it never logs response bodies`() {
        val client = NetworkModule.provideDownloadOkHttpClient(apiTokenInterceptor)

        assertFalse(client.interceptors.any { it is HttpLoggingInterceptor })
    }

    @Test
    fun `given api client when built then it logs through the logging interceptor`() {
        val client = NetworkModule.provideOkHttpClient(apiTokenInterceptor, HttpLoggingInterceptor())

        assertTrue(client.interceptors.any { it is HttpLoggingInterceptor })
    }
}
