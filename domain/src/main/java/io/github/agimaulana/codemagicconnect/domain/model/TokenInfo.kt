package io.github.agimaulana.codemagicconnect.domain.model

data class TokenInfo(
    val token: String,
    val addedAtEpochMillis: Long,
    val lastVerifiedAtEpochMillis: Long
)
