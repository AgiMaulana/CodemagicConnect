package io.github.agimaulana.codemagicconnect.domain.gateway

import io.github.agimaulana.codemagicconnect.domain.model.CodemagicBuild

interface BuildsGateway {

    suspend fun getTeamBuilds(teamId: String, appId: String): List<CodemagicBuild>

    suspend fun getBuildDetails(buildId: String): CodemagicBuild
}
