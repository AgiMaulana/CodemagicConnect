package io.github.agimaulana.codemagicconnect.domain.model

data class CodemagicApplication(
    val id: String,
    val name: String,
    val repositoryUrl: String,
    val lastBuildTime: String?,
    val isFavorite: Boolean = false,
    val isDefault: Boolean = false,
    val teamId: String = ""
)