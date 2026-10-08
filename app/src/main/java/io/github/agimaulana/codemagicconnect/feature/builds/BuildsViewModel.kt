package io.github.agimaulana.codemagicconnect.feature.builds

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.agimaulana.codemagicconnect.domain.model.ArtifactDownload
import io.github.agimaulana.codemagicconnect.domain.model.BuildArtifact
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicBuild
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.DownloadArtifactUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.GetApplicationUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.GetBuildsUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.InstallArtifactUseCase
import io.github.agimaulana.codemagicconnect.domain.usecase.builds.ObserveArtifactDownloadsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuildsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getApplicationUseCase: GetApplicationUseCase,
    private val getBuildsUseCase: GetBuildsUseCase,
    private val downloadArtifactUseCase: DownloadArtifactUseCase,
    private val observeArtifactDownloadsUseCase: ObserveArtifactDownloadsUseCase,
    private val installArtifactUseCase: InstallArtifactUseCase,
    private val buildTimestampFormatter: BuildTimestampFormatter
) : ViewModel() {

    private val appId: String = BuildsArgs(savedStateHandle).appId

    data class BuildListItem(
        val build: CodemagicBuild,
        val timestamp: BuildTimestamp
    )

    sealed interface BuildTimestamp {
        data class Today(val timeText: String) : BuildTimestamp
        data class Yesterday(val timeText: String) : BuildTimestamp
        data class OnDate(val dateText: String, val timeText: String) : BuildTimestamp
        data class Unknown(val rawValue: String) : BuildTimestamp
    }

    data class UiState(
        val app: CodemagicApplication? = null,
        val builds: List<BuildListItem> = emptyList(),
        val isLoading: Boolean = false,
        val showDownloadComplete: BuildArtifact? = null
    )

    sealed interface Action {
        data class DownloadArtifact(val buildId: String, val artifactId: String) : Action
        data class RetryDownload(val buildId: String, val artifactId: String) : Action
        data class InstallArtifact(val artifactId: String) : Action
        data class CloseDownloadComplete(val install: Boolean) : Action
        data object Refresh : Action
    }

    sealed interface UiEvent {
        data class ShowError(val message: String) : UiEvent
    }

    sealed interface NavigationEvent {
        data object NavigateToSettings : NavigationEvent
        data object NavigateToApps : NavigationEvent
        data object NavigateBack : NavigationEvent
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var baseBuilds: List<CodemagicBuild> = emptyList()
    private var downloadStates: Map<String, ArtifactDownload> = emptyMap()

    fun init() {
        observeDownloads()
        load()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.DownloadArtifact -> download(action.buildId, action.artifactId)
            is Action.RetryDownload -> download(action.buildId, action.artifactId)
            is Action.InstallArtifact -> install(action.artifactId)
            is Action.CloseDownloadComplete -> closeDownloadComplete(action.install)
            Action.Refresh -> load()
        }
    }

    fun onSettingsClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToSettings)
        }
    }

    fun onAppSelectorClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToApps)
        }
    }

    private fun load() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val app = getApplicationUseCase(appId)
                baseBuilds = getBuildsUseCase(appId)
                _uiState.update { it.copy(app = app, builds = mergeDownloads(baseBuilds), isLoading = false) }
            } catch (throwable: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.emit(UiEvent.ShowError(throwable.message ?: FAILED_TO_LOAD_MESSAGE))
            }
        }
    }

    private fun observeDownloads() {
        viewModelScope.launch {
            observeArtifactDownloadsUseCase().collect { downloads ->
                downloadStates = downloads.associateBy { it.artifactId }
                _uiState.update { it.copy(builds = mergeDownloads(baseBuilds)) }
            }
        }
    }

    private fun download(buildId: String, artifactId: String) {
        val artifact = findArtifact(buildId, artifactId) ?: return
        viewModelScope.launch {
            try {
                downloadArtifactUseCase(buildId, artifact)
                _uiState.update {
                    it.copy(showDownloadComplete = artifact.copy(downloadStatus = BuildArtifact.DownloadStatus.DOWNLOADED))
                }
            } catch (throwable: Exception) {
                _uiEvent.emit(UiEvent.ShowError(throwable.message ?: FAILED_TO_DOWNLOAD_MESSAGE))
            }
        }
    }

    private fun install(artifactId: String) {
        viewModelScope.launch {
            try {
                installArtifactUseCase(artifactId)
            } catch (throwable: Exception) {
                _uiEvent.emit(UiEvent.ShowError(throwable.message ?: FAILED_TO_INSTALL_MESSAGE))
            }
        }
    }

    private fun closeDownloadComplete(install: Boolean) {
        val artifact = _uiState.value.showDownloadComplete
        _uiState.update { it.copy(showDownloadComplete = null) }
        if (install && artifact != null) {
            install(artifact.id)
        }
    }

    private fun findArtifact(buildId: String, artifactId: String): BuildArtifact? = baseBuilds
        .firstOrNull { it.id == buildId }
        ?.artifacts
        ?.firstOrNull { it.id == artifactId }

    private fun mergeDownloads(builds: List<CodemagicBuild>): List<BuildListItem> = builds.map { build ->
        val mergedArtifacts = build.artifacts.map { artifact ->
            downloadStates[artifact.id]?.let { download ->
                artifact.copy(
                    downloadStatus = download.status,
                    downloadProgress = download.progress,
                    downloadSizeSoFarBytes = download.downloadedBytes
                )
            } ?: artifact
        }
        BuildListItem(
            build = build.copy(artifacts = mergedArtifacts),
            timestamp = buildTimestampFormatter.format(build.startedAt)
        )
    }

    companion object {
        private const val FAILED_TO_LOAD_MESSAGE = "Could not load builds. Please try again."
        private const val FAILED_TO_DOWNLOAD_MESSAGE = "Download failed. Please try again."
        private const val FAILED_TO_INSTALL_MESSAGE = "Could not start the installer."
    }
}
