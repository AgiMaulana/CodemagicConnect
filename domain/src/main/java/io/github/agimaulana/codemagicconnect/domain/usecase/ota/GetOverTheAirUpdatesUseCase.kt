package io.github.agimaulana.codemagicconnect.domain.usecase.ota

import io.github.agimaulana.codemagicconnect.domain.gateway.OverTheAirUpdatesGateway
import io.github.agimaulana.codemagicconnect.domain.model.OverTheAirUpdate
import javax.inject.Inject

interface GetOverTheAirUpdatesUseCase {
    suspend operator fun invoke(): List<OverTheAirUpdate>
}

internal class GetOverTheAirUpdatesUseCaseImpl @Inject constructor(
    private val overTheAirUpdatesGateway: OverTheAirUpdatesGateway
) : GetOverTheAirUpdatesUseCase {

    override suspend fun invoke(): List<OverTheAirUpdate> =
        overTheAirUpdatesGateway.getOverTheAirUpdates()
}
