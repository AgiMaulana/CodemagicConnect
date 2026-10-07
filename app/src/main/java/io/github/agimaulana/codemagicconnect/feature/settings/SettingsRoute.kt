package io.github.agimaulana.codemagicconnect.feature.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
internal fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigationEvent: (SettingsViewModel.NavigationEvent) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

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
                    when (event) {
                        is SettingsViewModel.UiEvent.ShowSnackbar ->
                            snackbarHostState.showSnackbar(event.message)
                    }
                }
        }
    }

    SettingsScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBackClicked = { onNavigationEvent(SettingsViewModel.NavigationEvent.NavigateBack) },
        snackbarHostState = snackbarHostState,
        onDefaultAppClicked = viewModel::onDefaultAppClicked,
        modifier = modifier
    )
}
