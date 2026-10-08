package io.github.agimaulana.codemagicconnect.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.agimaulana.codemagicconnect.domain.policy.WifiOnlyThreshold

@Module
@InstallIn(SingletonComponent::class)
object PolicyModule {

    @Provides
    fun provideWifiOnlyThreshold(): WifiOnlyThreshold = WifiOnlyThreshold.Default
}
