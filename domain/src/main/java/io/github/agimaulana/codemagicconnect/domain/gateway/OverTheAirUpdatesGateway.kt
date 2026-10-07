package io.github.agimaulana.codemagicconnect.domain.gateway

import io.github.agimaulana.codemagicconnect.domain.model.OverTheAirUpdate

interface OverTheAirUpdatesGateway {

    suspend fun getOverTheAirUpdates(): List<OverTheAirUpdate>
}
