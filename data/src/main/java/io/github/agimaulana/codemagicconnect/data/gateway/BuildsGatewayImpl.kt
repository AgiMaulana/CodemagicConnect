package io.github.agimaulana.codemagicconnect.data.gateway

import io.github.agimaulana.codemagicconnect.core.dispatcher.DispatcherProvider
import io.github.agimaulana.codemagicconnect.data.remote.api.CodemagicApiService
import io.github.agimaulana.codemagicconnect.data.remote.mapper.toDomain
import io.github.agimaulana.codemagicconnect.domain.gateway.BuildsGateway
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicBuild
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuildsGatewayImpl @Inject constructor(
    private val api: CodemagicApiService,
    private val dispatcherProvider: DispatcherProvider
) : BuildsGateway {

    override suspend fun getTeamBuilds(teamId: String, appId: String): List<CodemagicBuild> =
        withContext(dispatcherProvider.io()) {
            api.getTeamBuilds(teamId, appId).data.map { it.toDomain() }
        }

    override suspend fun getBuildDetails(buildId: String): CodemagicBuild =
        withContext(dispatcherProvider.io()) {
            api.getBuildDetails(buildId).data.toDomain()
        }
}
