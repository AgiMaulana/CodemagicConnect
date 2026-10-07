package io.github.agimaulana.codemagicconnect.domain.model

data class Settings(
    val isTokenConnected: Boolean,
    val token: String?,
    val tokenAddedAtEpochMillis: Long?,
    val tokenLastVerifiedAtEpochMillis: Long?,
    val defaultAppId: String?,
    val defaultAppName: String?,
    val wifiOnly: Boolean,
    val deleteApkAfterInstall: Boolean,
    val downloadedFilesBytes: Long
)
