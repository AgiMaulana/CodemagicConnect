package io.github.agimaulana.codemagicconnect.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserAppDto(
    val id: String,
    val name: String = "",
    @SerialName("icon_url") val iconUrl: String? = null
)

@Serializable
data class TeamAppDto(
    val id: String,
    val name: String = "",
    val repository: RepositoryDto? = null,
    @SerialName("last_build_id") val lastBuildId: String? = null,
    val archived: Boolean? = null
)

@Serializable
data class RepositoryDto(
    val url: String = ""
)
