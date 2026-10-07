package io.github.agimaulana.codemagicconnect.data.gateway

import io.github.agimaulana.codemagicconnect.core.dispatcher.DispatcherProvider
import io.github.agimaulana.codemagicconnect.data.remote.api.CodemagicApiService
import io.github.agimaulana.codemagicconnect.data.remote.mapper.toDomain
import io.github.agimaulana.codemagicconnect.domain.gateway.ApplicationsGateway
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import io.github.agimaulana.codemagicconnect.domain.model.Workflow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationsGatewayImpl @Inject constructor(
    private val api: CodemagicApiService,
    private val dispatcherProvider: DispatcherProvider
) : ApplicationsGateway {

    /**
     * The v3 `/user/apps` response carries no repository or team, so we walk the user's teams and
     * read each team's apps, which do include the repository URL. That also stamps the owning team
     * onto every application, which the builds endpoint requires.
     */
    override suspend fun getApplications(): List<CodemagicApplication> =
        withContext(dispatcherProvider.io()) {
            api.getUserTeams(PAGE_SIZE).data.flatMap { team ->
                api.getTeamApps(team.id, PAGE_SIZE).data.map { it.toDomain(team.id) }
            }
        }

    override suspend fun getWorkflows(appId: String): List<Workflow> =
        withContext(dispatcherProvider.io()) {
            api.getAppWorkflows(appId).data.map { it.toDomain(appId) }
        }

    companion object {
        private const val PAGE_SIZE = 100
    }
}
