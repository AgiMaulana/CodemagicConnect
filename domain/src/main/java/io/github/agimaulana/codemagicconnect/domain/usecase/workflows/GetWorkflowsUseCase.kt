package io.github.agimaulana.codemagicconnect.domain.usecase.workflows

import io.github.agimaulana.codemagicconnect.domain.gateway.ApplicationsGateway
import io.github.agimaulana.codemagicconnect.domain.model.Workflow
import javax.inject.Inject

interface GetWorkflowsUseCase {
    suspend operator fun invoke(appId: String): List<Workflow>
}

internal class GetWorkflowsUseCaseImpl @Inject constructor(
    private val applicationsGateway: ApplicationsGateway
) : GetWorkflowsUseCase {

    override suspend fun invoke(appId: String): List<Workflow> =
        applicationsGateway.getWorkflows(appId)
}
