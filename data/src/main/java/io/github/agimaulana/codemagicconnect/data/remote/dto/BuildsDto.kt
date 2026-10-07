package io.github.agimaulana.codemagicconnect.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BuildDto(
    val id: String,
    @SerialName("app_id") val appId: String = "",
    val workflow: WorkflowDto? = null,
    val status: String = "",
    val branch: String? = null,
    @SerialName("started_at") val startedAt: String? = null,
    @SerialName("finished_at") val finishedAt: String? = null,
    val artifacts: List<ArtifactDto> = emptyList()
)

@Serializable
data class ArtifactDto(
    val name: String = "",
    @SerialName("size_in_bytes") val sizeInBytes: Long = 0L,
    @SerialName("short_lived_download_url") val downloadUrl: String = "",
    val type: String? = null
)
