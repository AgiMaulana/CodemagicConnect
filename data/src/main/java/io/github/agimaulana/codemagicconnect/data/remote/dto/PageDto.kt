package io.github.agimaulana.codemagicconnect.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageDto<T>(val data: List<T> = emptyList())

@Serializable
data class SingleDto<T>(val data: T)
