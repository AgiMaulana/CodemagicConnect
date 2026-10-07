package io.github.agimaulana.codemagicconnect.data.gateway

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.agimaulana.codemagicconnect.core.dispatcher.DispatcherProvider
import io.github.agimaulana.codemagicconnect.data.download.ArtifactDownloader
import io.github.agimaulana.codemagicconnect.data.download.ArtifactInstaller
import io.github.agimaulana.codemagicconnect.data.local.dao.DownloadDao
import io.github.agimaulana.codemagicconnect.data.local.mapper.toDomain
import io.github.agimaulana.codemagicconnect.domain.gateway.ArtifactsGateway
import io.github.agimaulana.codemagicconnect.domain.model.ArtifactDownload
import io.github.agimaulana.codemagicconnect.domain.model.BuildArtifact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtifactsGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadDao: DownloadDao,
    private val artifactDownloader: ArtifactDownloader,
    private val artifactInstaller: ArtifactInstaller,
    private val dispatcherProvider: DispatcherProvider
) : ArtifactsGateway {

    override suspend fun download(buildId: String, artifact: BuildArtifact) {
        artifactDownloader.download(buildId, artifact)
    }

    override fun observeDownloads(): Flow<List<ArtifactDownload>> =
        downloadDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override suspend fun install(artifactId: String) {
        val download = downloadDao.get(artifactId)
            ?: throw IllegalStateException("Artifact $artifactId has not been downloaded yet")
        val path = download.localPath
            ?: throw IllegalStateException("Artifact $artifactId has no local file")
        artifactInstaller.install(File(path))
    }

    override suspend fun clearDownloadedFiles() = withContext(dispatcherProvider.io()) {
        File(context.filesDir, ArtifactDownloader.DOWNLOAD_DIRECTORY).deleteRecursively()
        downloadDao.deleteAll()
    }

    override suspend fun downloadedFilesSizeBytes(): Long = withContext(dispatcherProvider.io()) {
        val directory = File(context.filesDir, ArtifactDownloader.DOWNLOAD_DIRECTORY)
        directory.listFiles()?.sumOf { it.length() } ?: 0L
    }
}
