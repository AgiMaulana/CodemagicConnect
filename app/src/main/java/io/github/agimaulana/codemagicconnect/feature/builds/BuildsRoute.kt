package io.github.agimaulana.codemagicconnect.feature.builds

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
internal fun BuildsRoute(
    modifier: Modifier = Modifier,
    viewModel: BuildsViewModel = hiltViewModel(),
    onNavigationEvent: (BuildsViewModel.NavigationEvent) -> Unit
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
                        is BuildsViewModel.UiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                    }
                }
        }
    }

    BuildsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
        onSettingsClicked = viewModel::onSettingsClicked,
        onAppSelectorClicked = viewModel::onAppSelectorClicked,
        modifier = modifier
    )

    uiState.showDownloadComplete?.let { artifact ->
        DownloadCompleteBottomSheet(
            artifact = artifact,
            onDismiss = { viewModel.onAction(BuildsViewModel.Action.CloseDownloadComplete(false)) },
            onInstall = { viewModel.onAction(BuildsViewModel.Action.CloseDownloadComplete(true)) }
        )
    }
}
