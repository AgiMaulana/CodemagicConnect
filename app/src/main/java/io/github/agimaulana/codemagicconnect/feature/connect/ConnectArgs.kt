package io.github.agimaulana.codemagicconnect.feature.connect

import androidx.lifecycle.SavedStateHandle

internal class ConnectArgs(savedStateHandle: SavedStateHandle) {

    val suppressAutoRedirect: Boolean = when (
        val raw: Any? = savedStateHandle.get<Any>(CONNECT_SUPPRESS_AUTO_REDIRECT_ARG)
    ) {
        is Boolean -> raw
        is String -> raw.toBoolean()
        else -> false
    }
}
