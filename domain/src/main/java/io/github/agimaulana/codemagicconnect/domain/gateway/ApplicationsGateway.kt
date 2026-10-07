package io.github.agimaulana.codemagicconnect.domain.gateway

import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import io.github.agimaulana.codemagicconnect.domain.model.Workflow

interface ApplicationsGateway {

    suspend fun getApplications(): List<CodemagicApplication>

    suspend fun getWorkflows(appId: String): List<Workflow>
}
