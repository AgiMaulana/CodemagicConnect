package io.github.agimaulana.codemagicconnect.domain.model

data class CodemagicBuild(
    val id: String,
    val appId: String,
    val workflowId: String,
    val branch: String,
    val status: BuildStatus,
    val startedAt: String,
    val finishedAt: String?,
    val artifacts: List<BuildArtifact>,
    val triggerer: String
)