package io.github.agimaulana.codemagicconnect.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.agimaulana.codemagicconnect.domain.model.Settings
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ClearDefaultApplicationUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ClearDownloadedFilesUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ObserveSettingsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.RemoveTokenUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.SetDeleteApkAfterInstallUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.SetWifiOnlyUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.TestConnectionUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val testConnectionUseCase: TestConnectionUseCase,
    private val removeTokenUseCase: RemoveTokenUseCase,
    private val clearDefaultApplicationUseCase: ClearDefaultApplicationUseCase,
    private val setWifiOnlyUseCase: SetWifiOnlyUseCase,
    private val setDeleteApkAfterInstallUseCase: SetDeleteApkAfterInstallUseCase,
    private val clearDownloadedFilesUseCase: ClearDownloadedFilesUseCase
) : ViewModel() {

    data class UiState(
        val isTokenConnected: Boolean = true,
        val tokenObfuscated: String = "cm_••••••••••••3f9a",
        val tokenAddedDate: String = "12 Sep 2026",
        val tokenVerifiedDate: String = "Verified today, 14:30",
        val defaultAppName: String? = "acme-mobile",
        val isWifiOnly: Boolean = true,
        val isDeleteApkAfterInstall: Boolean = false,
        val downloadedFilesSize: String = "214 MB"
    )

    sealed interface Action {
        data object TestConnection : Action
        data object ReplaceToken : Action
        data object RemoveTokenAndSignOut : Action
        data object ClearDefaultApp : Action
        data class ToggleWifiOnly(val isEnabled: Boolean) : Action
        data class ToggleDeleteApkAfterInstall(val isEnabled: Boolean) : Action
        data object ClearDownloadedFiles : Action
    }

    sealed interface UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent
    }

    sealed interface NavigationEvent {
        data object NavigateBack : NavigationEvent
        data class NavigateToConnect(val suppressAutoRedirect: Boolean = false) : NavigationEvent
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun init() {
        viewModelScope.launch {
            observeSettingsUseCase().collect { settings ->
                _uiState.value = settings.toUiState()
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.TestConnection -> viewModelScope.launch {
                val connected = testConnectionUseCase()
                _uiEvent.emit(
                    UiEvent.ShowSnackbar(if (connected) CONNECTION_SUCCESS_MESSAGE else CONNECTION_FAILED_MESSAGE)
                )
            }
            Action.ReplaceToken -> viewModelScope.launch {
                _navigationEvent.emit(NavigationEvent.NavigateToConnect(suppressAutoRedirect = true))
            }
            Action.RemoveTokenAndSignOut -> viewModelScope.launch {
                removeTokenUseCase()
                _navigationEvent.emit(NavigationEvent.NavigateToConnect(suppressAutoRedirect = false))
            }
            Action.ClearDefaultApp -> viewModelScope.launch {
                clearDefaultApplicationUseCase()
            }
            is Action.ToggleWifiOnly -> viewModelScope.launch {
                setWifiOnlyUseCase(action.isEnabled)
            }
            is Action.ToggleDeleteApkAfterInstall -> viewModelScope.launch {
                setDeleteApkAfterInstallUseCase(action.isEnabled)
            }
            Action.ClearDownloadedFiles -> viewModelScope.launch {
                clearDownloadedFilesUseCase()
                _uiEvent.emit(UiEvent.ShowSnackbar(FILES_CLEARED_MESSAGE))
            }
        }
    }

    private fun Settings.toUiState(): UiState = UiState(
        isTokenConnected = isTokenConnected,
        tokenObfuscated = obfuscateToken(token),
        tokenAddedDate = formatAddedAt(tokenAddedAtEpochMillis),
        tokenVerifiedDate = formatVerifiedAt(tokenLastVerifiedAtEpochMillis),
        defaultAppName = defaultAppName,
        isWifiOnly = wifiOnly,
        isDeleteApkAfterInstall = deleteApkAfterInstall,
        downloadedFilesSize = formatBytes(downloadedFilesBytes)
    )

    private fun obfuscateToken(token: String?): String {
        if (token.isNullOrBlank()) return NOT_CONNECTED_LABEL
        if (token.length <= OBFUSCATION_VISIBLE_CHARACTERS) return token
        return token.take(OBFUSCATION_PREFIX_CHARACTERS) +
            OBFUSCATION_DOTS +
            token.takeLast(OBFUSCATION_SUFFIX_CHARACTERS)
    }

    private fun formatAddedAt(epochMillis: Long?): String = epochMillis
        ?.let { Date(it).formatAs(ADDED_DATE_PATTERN) }
        .orEmpty()

    private fun formatVerifiedAt(epochMillis: Long?): String {
        if (epochMillis == null) return ""
        val time = Date(epochMillis).formatAs(TIME_PATTERN)
        return if (Date(epochMillis).isToday()) {
            "Verified today, $time"
        } else {
            "Verified ${Date(epochMillis).formatAs(VERIFIED_DATE_PATTERN)}, $time"
        }
    }

    private fun formatBytes(bytes: Long): String = when {
        bytes <= 0L -> "0 MB"
        bytes < BYTES_PER_MEGABYTE -> "${bytes / BYTES_PER_KILOBYTE} KB"
        else -> "${bytes / BYTES_PER_MEGABYTE} MB"
    }

    private fun Date.formatAs(pattern: String): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(this)

    private fun Date.isToday(): Boolean {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { time = this@isToday }
        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }

    companion object {
        private const val CONNECTION_SUCCESS_MESSAGE = "Connection successful"
        private const val CONNECTION_FAILED_MESSAGE = "Connection failed. Check your token."
        private const val FILES_CLEARED_MESSAGE = "Downloaded files cleared"
        private const val NOT_CONNECTED_LABEL = "Not connected"
        private const val OBFUSCATION_DOTS = "••••••••••••"
        private const val OBFUSCATION_PREFIX_CHARACTERS = 3
        private const val OBFUSCATION_SUFFIX_CHARACTERS = 4
        private const val OBFUSCATION_VISIBLE_CHARACTERS = OBFUSCATION_PREFIX_CHARACTERS + OBFUSCATION_SUFFIX_CHARACTERS
        private const val ADDED_DATE_PATTERN = "d MMM yyyy"
        private const val VERIFIED_DATE_PATTERN = "d MMM"
        private const val TIME_PATTERN = "HH:mm"
        private const val BYTES_PER_KILOBYTE = 1024L
        private const val BYTES_PER_MEGABYTE = 1024L * 1024L
    }
}
