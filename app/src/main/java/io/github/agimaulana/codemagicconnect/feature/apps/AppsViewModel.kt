package io.github.agimaulana.codemagicconnect.feature.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.GetApplicationsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ObserveAppPreferencesUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ObserveFavoriteApplicationsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.SetDefaultApplicationUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.SetOpenAppAutomaticallyUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ToggleFavoriteApplicationUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val getApplicationsUseCase: GetApplicationsUseCase,
    private val setDefaultApplicationUseCase: SetDefaultApplicationUseCase,
    private val toggleFavoriteApplicationUseCase: ToggleFavoriteApplicationUseCase,
    private val observeAppPreferencesUseCase: ObserveAppPreferencesUseCase,
    private val observeFavoriteApplicationsUseCase: ObserveFavoriteApplicationsUseCase,
    private val setOpenAppAutomaticallyUseCase: SetOpenAppAutomaticallyUseCase
) : ViewModel() {

    data class UiState(
        val apps: List<CodemagicApplication> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val openAppAutomatically: Boolean = false
    )

    sealed interface Action {
        data class UpdateSearchQuery(val query: String) : Action
        data class ToggleOpenAutomatically(val openAutomatically: Boolean) : Action
        data class SelectApp(val app: CodemagicApplication) : Action
        data class ToggleFavorite(val app: CodemagicApplication) : Action
        data object ContinueToBuilds : Action
    }

    sealed interface UiEvent {
        data class ShowError(val message: String) : UiEvent
    }

    sealed interface NavigationEvent {
        data class NavigateToBuilds(val appId: String) : NavigationEvent
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var allApps: List<CodemagicApplication> = emptyList()
    private var favoriteIds: Set<String> = emptySet()
    private var defaultAppId: String? = null
    private var selectedApp: CodemagicApplication? = null

    fun init() {
        observePreferences()
        loadApplications()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateSearchQuery -> _uiState.update { it.copy(searchQuery = action.query) }
            is Action.ToggleOpenAutomatically -> {
                _uiState.update { it.copy(openAppAutomatically = action.openAutomatically) }
                viewModelScope.launch { setOpenAppAutomaticallyUseCase(action.openAutomatically) }
            }
            is Action.SelectApp -> {
                selectedApp = action.app
                defaultAppId = action.app.id
                applyFlags()
                viewModelScope.launch { setDefaultApplicationUseCase(action.app) }
            }
            is Action.ToggleFavorite -> {
                favoriteIds = if (action.app.id in favoriteIds) {
                    favoriteIds - action.app.id
                } else {
                    favoriteIds + action.app.id
                }
                applyFlags()
                viewModelScope.launch { toggleFavoriteApplicationUseCase(action.app.id) }
            }
            Action.ContinueToBuilds -> {
                val target = selectedApp ?: _uiState.value.apps.firstOrNull { it.isDefault }
                target?.let { app ->
                    viewModelScope.launch {
                        _navigationEvent.emit(NavigationEvent.NavigateToBuilds(app.id))
                    }
                }
            }
        }
    }

    private fun loadApplications() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                allApps = getApplicationsUseCase()
                applyFlags()
                _uiState.update { it.copy(isLoading = false) }
            } catch (throwable: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.emit(UiEvent.ShowError(throwable.message ?: FAILED_TO_LOAD_MESSAGE))
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            observeAppPreferencesUseCase().collect { preferences ->
                defaultAppId = preferences.defaultAppId
                _uiState.update { it.copy(openAppAutomatically = preferences.openAppAutomatically) }
                applyFlags()
            }
        }
        viewModelScope.launch {
            observeFavoriteApplicationsUseCase().collect { ids ->
                favoriteIds = ids
                applyFlags()
            }
        }
    }

    private fun applyFlags() {
        val merged = allApps.map { app ->
            app.copy(isFavorite = app.id in favoriteIds, isDefault = app.id == defaultAppId)
        }
        _uiState.update { it.copy(apps = merged) }
    }

    companion object {
        private const val FAILED_TO_LOAD_MESSAGE = "Could not load your applications. Please try again."
    }
}
