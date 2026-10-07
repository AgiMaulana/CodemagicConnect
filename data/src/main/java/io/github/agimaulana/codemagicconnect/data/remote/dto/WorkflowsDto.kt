package io.github.agimaulana.codemagicconnect.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorkflowDto(
    val id: String,
    val name: String? = null
)
