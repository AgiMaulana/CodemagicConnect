package io.github.agimaulana.codemagicconnect.feature.builds

import androidx.lifecycle.SavedStateHandle

internal class BuildsArgs(savedStateHandle: SavedStateHandle) {

    val appId: String = checkNotNull(savedStateHandle[APP_ID_ARG]) {
        "Missing $APP_ID_ARG navigation argument for the builds screen"
    }
}
