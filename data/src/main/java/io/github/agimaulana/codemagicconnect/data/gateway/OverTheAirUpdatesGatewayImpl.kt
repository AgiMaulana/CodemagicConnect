package io.github.agimaulana.codemagicconnect.data.gateway

import io.github.agimaulana.codemagicconnect.core.dispatcher.DispatcherProvider
import io.github.agimaulana.codemagicconnect.data.remote.api.CodemagicApiService
import io.github.agimaulana.codemagicconnect.data.remote.mapper.toDomain
import io.github.agimaulana.codemagicconnect.domain.gateway.OverTheAirUpdatesGateway
import io.github.agimaulana.codemagicconnect.domain.model.OverTheAirUpdate
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverTheAirUpdatesGatewayImpl @Inject constructor(
    private val api: CodemagicApiService,
    private val dispatcherProvider: DispatcherProvider
) : OverTheAirUpdatesGateway {

    override suspend fun getOverTheAirUpdates(): List<OverTheAirUpdate> =
        withContext(dispatcherProvider.io()) {
            api.getOverTheAirUpdates().map { it.toDomain() }
        }
}
