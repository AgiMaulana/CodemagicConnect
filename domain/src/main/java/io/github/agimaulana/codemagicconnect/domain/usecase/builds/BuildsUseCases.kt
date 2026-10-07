package io.github.agimaulana.codemagicconnect.domain.usecase.builds

import io.github.agimaulana.codemagicconnect.domain.gateway.ApplicationsGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.ArtifactsGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.BuildsGateway
import io.github.agimaulana.codemagicconnect.domain.gateway.PreferencesGateway
import io.github.agimaulana.codemagicconnect.domain.model.ArtifactDownload
import io.github.agimaulana.codemagicconnect.domain.model.BuildArtifact
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicBuild
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface GetApplicationUseCase {
    suspend operator fun invoke(appId: String): CodemagicApplication?
}

interface GetBuildsUseCase {
    suspend operator fun invoke(appId: String): List<CodemagicBuild>
}

interface DownloadArtifactUseCase {
    suspend operator fun invoke(buildId: String, artifact: BuildArtifact)
}

interface ObserveArtifactDownloadsUseCase {
    operator fun invoke(): Flow<List<ArtifactDownload>>
}

interface InstallArtifactUseCase {
    suspend operator fun invoke(artifactId: String)
}

internal class GetApplicationUseCaseImpl @Inject constructor(
    private val applicationsGateway: ApplicationsGateway
) : GetApplicationUseCase {

    override suspend fun invoke(appId: String): CodemagicApplication? =
        applicationsGateway.getApplications().firstOrNull { it.id == appId }
}

internal class GetBuildsUseCaseImpl @Inject constructor(
    private val applicationsGateway: ApplicationsGateway,
    private val buildsGateway: BuildsGateway
) : GetBuildsUseCase {

    override suspend fun invoke(appId: String): List<CodemagicBuild> {
        val app = applicationsGateway.getApplications().firstOrNull { it.id == appId }
            ?: return emptyList()
        return buildsGateway.getTeamBuilds(app.teamId, appId)
    }
}

internal class DownloadArtifactUseCaseImpl @Inject constructor(
    private val artifactsGateway: ArtifactsGateway
) : DownloadArtifactUseCase {

    override suspend fun invoke(buildId: String, artifact: BuildArtifact) {
        artifactsGateway.download(buildId, artifact)
    }
}

internal class ObserveArtifactDownloadsUseCaseImpl @Inject constructor(
    private val artifactsGateway: ArtifactsGateway
) : ObserveArtifactDownloadsUseCase {

    override fun invoke(): Flow<List<ArtifactDownload>> = artifactsGateway.observeDownloads()
}

internal class InstallArtifactUseCaseImpl @Inject constructor(
    private val artifactsGateway: ArtifactsGateway,
    private val preferencesGateway: PreferencesGateway
) : InstallArtifactUseCase {

    override suspend fun invoke(artifactId: String) {
        artifactsGateway.install(artifactId)
        if (preferencesGateway.observePreferences().first().deleteApkAfterInstall) {
            artifactsGateway.deleteDownload(artifactId)
        }
    }
}
