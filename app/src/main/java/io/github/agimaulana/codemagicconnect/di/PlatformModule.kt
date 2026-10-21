package io.github.agimaulana.codemagicconnect.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.agimaulana.codemagicconnect.platform.AndroidDeviceAbiProvider
import io.github.agimaulana.codemagicconnect.platform.DeviceAbiProvider

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PlatformModule {

    @Binds
    internal abstract fun bindDeviceAbiProvider(impl: AndroidDeviceAbiProvider): DeviceAbiProvider
}
