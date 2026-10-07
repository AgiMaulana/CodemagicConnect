package io.github.agimaulana.codemagicconnect.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: String,
    val name: String = "",
    @SerialName("icon_url") val iconUrl: String? = null
)
