package io.github.agimaulana.codemagicconnect.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OverTheAirUpdateDto(
    val id: String,
    @SerialName("app_id") val appId: String = "",
    val version: String = "",
    @SerialName("created_at") val createdAt: String = ""
)
