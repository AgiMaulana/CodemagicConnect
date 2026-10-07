package io.github.agimaulana.codemagicconnect.feature.connect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
internal fun ConnectRoute(
    modifier: Modifier = Modifier,
    viewModel: ConnectViewModel = hiltViewModel(),
    onNavigationEvent: (ConnectViewModel.NavigationEvent) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.init()
        launch {
            viewModel.navigationEvent
                .flowWithLifecycle(lifecycleOwner.lifecycle)
                .collectLatest { onNavigationEvent(it) }
        }
        launch {
            viewModel.uiEvent
                .flowWithLifecycle(lifecycleOwner.lifecycle)
                .collectLatest { event ->
                    // handle ui event like snackbars
                }
        }
    }

    ConnectScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
