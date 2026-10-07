package io.github.agimaulana.codemagicconnect.data.remote.mapper

import io.github.agimaulana.codemagicconnect.data.remote.dto.ArtifactDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.BuildDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.OverTheAirUpdateDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.TeamAppDto
import io.github.agimaulana.codemagicconnect.data.remote.dto.WorkflowDto
import io.github.agimaulana.codemagicconnect.domain.model.BuildArtifact
import io.github.agimaulana.codemagicconnect.domain.model.BuildStatus
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicBuild
import io.github.agimaulana.codemagicconnect.domain.model.OverTheAirUpdate
import io.github.agimaulana.codemagicconnect.domain.model.Workflow

internal fun TeamAppDto.toDomain(teamId: String): CodemagicApplication = CodemagicApplication(
    id = id,
    name = name,
    repositoryUrl = repository?.url.orEmpty(),
    lastBuildTime = null,
    teamId = teamId
)

internal fun WorkflowDto.toDomain(appId: String): Workflow = Workflow(
    id = id,
    name = name.orEmpty(),
    appId = appId
)

internal fun BuildDto.toDomain(): CodemagicBuild = CodemagicBuild(
    id = id,
    appId = appId,
    workflowId = workflow?.id.orEmpty(),
    branch = branch.orEmpty(),
    status = status.toBuildStatus(),
    startedAt = startedAt.orEmpty(),
    finishedAt = finishedAt,
    artifacts = artifacts.mapIndexed { index, artifact -> artifact.toDomain("$id#$index") },
    triggerer = ""
)

internal fun ArtifactDto.toDomain(id: String): BuildArtifact = BuildArtifact(
    id = id,
    name = name,
    sizeBytes = sizeInBytes,
    downloadUrl = downloadUrl.ifBlank { null }
)

internal fun OverTheAirUpdateDto.toDomain(): OverTheAirUpdate = OverTheAirUpdate(
    id = id,
    appId = appId,
    version = version,
    createdAt = createdAt
)

internal fun String.toBuildStatus(): BuildStatus = when (lowercase()) {
    "finished" -> BuildStatus.FINISHED
    "failed", "timeout" -> BuildStatus.FAILED
    "canceled", "cancelled", "skipped" -> BuildStatus.CANCELED
    "building", "testing", "publishing", "finishing", "preparing", "fetching", "initializing" -> BuildStatus.BUILDING
    else -> BuildStatus.QUEUED
}
