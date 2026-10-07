package io.github.agimaulana.codemagicconnect.domain.usecase.settings

import io.github.agimaulana.codemagicconnect.domain.gateway.ArtifactsGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.AuthGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.PreferencesGateway
import io.github.agimaulana.codemagicconnect.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveSettingsUseCase {
    operator fun invoke(): Flow<Settings>
}

interface TestConnectionUseCase {
    suspend operator fun invoke(): Boolean
}

interface RemoveTokenUseCase {
    suspend operator fun invoke()
}

interface ClearDefaultApplicationUseCase {
    suspend operator fun invoke()
}

interface SetWifiOnlyUseCase {
    suspend operator fun invoke(enabled: Boolean)
}

interface SetDeleteApkAfterInstallUseCase {
    suspend operator fun invoke(enabled: Boolean)
}

interface ClearDownloadedFilesUseCase {
    suspend operator fun invoke()
}

internal class ObserveSettingsUseCaseImpl @Inject constructor(
    private val authGateway: AuthGateway,
    private val preferencesGateway: PreferencesGateway,
    private val artifactsGateway: ArtifactsGateway
) : ObserveSettingsUseCase {

    override fun invoke(): Flow<Settings> = combine(
        authGateway.observeToken(),
        preferencesGateway.observePreferences()
    ) { token, preferences -> token to preferences }
        .map { (token, preferences) ->
            Settings(
                isTokenConnected = token != null,
                token = token?.token,
                tokenAddedAtEpochMillis = token?.addedAtEpochMillis,
                tokenLastVerifiedAtEpochMillis = token?.lastVerifiedAtEpochMillis,
                defaultAppId = preferences.defaultAppId,
                defaultAppName = preferences.defaultAppName,
                wifiOnly = preferences.wifiOnly,
                deleteApkAfterInstall = preferences.deleteApkAfterInstall,
                downloadedFilesBytes = artifactsGateway.downloadedFilesSizeBytes()
            )
        }
}

internal class TestConnectionUseCaseImpl @Inject constructor(
    private val authGateway: AuthGateway
) : TestConnectionUseCase {

    override suspend fun invoke(): Boolean {
        val token = authGateway.observeToken().first()?.token ?: return false
        return authGateway.verifyToken(token)
    }
}

internal class RemoveTokenUseCaseImpl @Inject constructor(
    private val authGateway: AuthGateway
) : RemoveTokenUseCase {

    override suspend fun invoke() {
        authGateway.removeToken()
    }
}

internal class ClearDefaultApplicationUseCaseImpl @Inject constructor(
    private val preferencesGateway: PreferencesGateway
) : ClearDefaultApplicationUseCase {

    override suspend fun invoke() {
        preferencesGateway.clearDefaultApplication()
    }
}

internal class SetWifiOnlyUseCaseImpl @Inject constructor(
    private val preferencesGateway: PreferencesGateway
) : SetWifiOnlyUseCase {

    override suspend fun invoke(enabled: Boolean) {
        preferencesGateway.setWifiOnly(enabled)
    }
}

internal class SetDeleteApkAfterInstallUseCaseImpl @Inject constructor(
    private val preferencesGateway: PreferencesGateway
) : SetDeleteApkAfterInstallUseCase {

    override suspend fun invoke(enabled: Boolean) {
        preferencesGateway.setDeleteApkAfterInstall(enabled)
    }
}

internal class ClearDownloadedFilesUseCaseImpl @Inject constructor(
    private val artifactsGateway: ArtifactsGateway
) : ClearDownloadedFilesUseCase {

    override suspend fun invoke() {
        artifactsGateway.clearDownloadedFiles()
    }
}
