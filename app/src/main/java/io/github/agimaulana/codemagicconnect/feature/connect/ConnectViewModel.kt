package io.github.agimaulana.codemagicconnect.feature.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.agimaulana.codemagicconnect.domain.usecase.apps.ObserveAppPreferencesUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.connect.IsTokenStoredUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.connect.VerifyAndSaveTokenUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectViewModel @Inject constructor(
    private val verifyAndSaveTokenUseCase: VerifyAndSaveTokenUseCase,
    private val isTokenStoredUseCase: IsTokenStoredUseCase,
    private val observeAppPreferencesUseCase: ObserveAppPreferencesUseCase
) : ViewModel() {

    data class UiState(
        val token: String = "",
        val isLoading: Boolean = false,
        val isCheckingStoredToken: Boolean = false,
        val isTokenVisible: Boolean = false
    )

    sealed interface Action {
        data class UpdateToken(val token: String) : Action
        data object ToggleTokenVisibility : Action
        data object VerifyAndSaveToken : Action
        data object OpenCodemagicInBrowser : Action
    }

    sealed interface UiEvent {
        data class ShowError(val message: String) : UiEvent
    }

    sealed interface NavigationEvent {
        data object NavigateToApps : NavigationEvent
        data class NavigateToBuilds(val appId: String) : NavigationEvent
        data class OpenBrowser(val url: String) : NavigationEvent
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun init() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingStoredToken = true) }
            try {
                if (isTokenStoredUseCase()) {
                    val defaultAppId = observeAppPreferencesUseCase().first().defaultAppId
                    if (defaultAppId != null) {
                        _navigationEvent.emit(NavigationEvent.NavigateToBuilds(defaultAppId))
                    } else {
                        _navigationEvent.emit(NavigationEvent.NavigateToApps)
                    }
                }
            } finally {
                _uiState.update { it.copy(isCheckingStoredToken = false) }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateToken -> _uiState.update { it.copy(token = action.token) }
            Action.ToggleTokenVisibility -> _uiState.update { it.copy(isTokenVisible = !it.isTokenVisible) }
            Action.VerifyAndSaveToken -> verifyAndSaveToken()
            Action.OpenCodemagicInBrowser -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.OpenBrowser(CODEMAGIC_SETTINGS_URL))
                }
            }
        }
    }

    private fun verifyAndSaveToken() {
        val token = _uiState.value.token
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                verifyAndSaveTokenUseCase(token)
                _uiState.update { it.copy(isLoading = false) }
                _navigationEvent.emit(NavigationEvent.NavigateToApps)
            } catch (throwable: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.emit(UiEvent.ShowError(throwable.message ?: FAILED_VERIFICATION_MESSAGE))
            }
        }
    }

    companion object {
        private const val CODEMAGIC_SETTINGS_URL = "https://codemagic.io/settings"
        private const val FAILED_VERIFICATION_MESSAGE = "Could not verify the token. Please try again."
    }
}
