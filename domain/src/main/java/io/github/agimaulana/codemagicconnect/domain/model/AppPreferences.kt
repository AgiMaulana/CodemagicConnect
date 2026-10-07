package io.github.agimaulana.codemagicconnect.domain.model

data class AppPreferences(
    val openAppAutomatically: Boolean = false,
    val wifiOnly: Boolean = true,
    val deleteApkAfterInstall: Boolean = false,
    val defaultAppId: String? = null,
    val defaultAppName: String? = null
)
