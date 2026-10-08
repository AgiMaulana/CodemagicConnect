package io.github.agimaulana.codemagicconnect.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.agimaulana.codemagicconnect.data.gateway.ApplicationsGatewayImpl
import io.github.agimaulana.codemagicconnect.data.gateway.ArtifactsGatewayImpl
import io.github.agimaulana.codemagicconnect.data.gateway.AuthGatewayImpl
import io.github.agimaulana.codemagicconnect.data.gateway.BuildsGatewayImpl
import io.github.agimaulana.codemagicconnect.data.gateway.NetworkMonitorGatewayImpl
import io.github.agimaulana.codemagicconnect.data.gateway.OverTheAirUpdatesGatewayImpl
import io.github.agimaulana.codemagicconnect.data.gateway.PreferencesGatewayImpl
import io.github.agimaulana.codemagicconnect.domain.gateway.ApplicationsGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.ArtifactsGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.AuthGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.BuildsGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.NetworkMonitorGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.OverTheAirUpdatesGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.PreferencesGateway
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GatewayModule {

    @Binds
    @Singleton
    abstract fun bindAuthGateway(impl: AuthGatewayImpl): AuthGateway

    @Binds
    @Singleton
    abstract fun bindApplicationsGateway(impl: ApplicationsGatewayImpl): ApplicationsGateway

    @Binds
    @Singleton
    abstract fun bindBuildsGateway(impl: BuildsGatewayImpl): BuildsGateway

    @Binds
    @Singleton
    abstract fun bindArtifactsGateway(impl: ArtifactsGatewayImpl): ArtifactsGateway

    @Binds
    @Singleton
    abstract fun bindPreferencesGateway(impl: PreferencesGatewayImpl): PreferencesGateway

    @Binds
    @Singleton
    abstract fun bindNetworkMonitorGateway(impl: NetworkMonitorGatewayImpl): NetworkMonitorGateway

    @Binds
    @Singleton
    abstract fun bindOverTheAirUpdatesGateway(
        impl: OverTheAirUpdatesGatewayImpl
    ): OverTheAirUpdatesGateway
}
