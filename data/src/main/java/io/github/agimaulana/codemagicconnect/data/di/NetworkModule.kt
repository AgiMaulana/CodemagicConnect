package io.github.agimaulana.codemagicconnect.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.agimaulana.codemagicconnect.data.BuildConfig
import io.github.agimaulana.codemagicconnect.data.remote.JsonConverterFactory
import io.github.agimaulana.codemagicconnect.data.remote.api.CodemagicApiService
import io.github.agimaulana.codemagicconnect.data.remote.interceptor.ApiTokenInterceptor
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            redactHeader(CodemagicApiService.API_TOKEN_HEADER)
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        apiTokenInterceptor: ApiTokenInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(apiTokenInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Provides
    @Singleton
    @DownloadClient
    fun provideDownloadOkHttpClient(apiTokenInterceptor: ApiTokenInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(apiTokenInterceptor)
            .readTimeout(DOWNLOAD_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(JsonConverterFactory.create(json))
            .build()

    @Provides
    @Singleton
    fun provideCodemagicApiService(retrofit: Retrofit): CodemagicApiService =
        retrofit.create(CodemagicApiService::class.java)

    private const val BASE_URL = "https://codemagic.io/"
    private const val DOWNLOAD_READ_TIMEOUT_SECONDS = 60L
}
