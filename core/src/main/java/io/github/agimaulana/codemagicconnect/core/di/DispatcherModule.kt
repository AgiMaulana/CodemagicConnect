package io.github.agimaulana.codemagicconnect.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.agimaulana.codemagicconnect.core.dispatcher.DefaultDispatcherProvider
import io.github.agimaulana.codemagicconnect.core.dispatcher.DispatcherProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DispatcherModule {

    @Binds
    @Singleton
    internal abstract fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
}
