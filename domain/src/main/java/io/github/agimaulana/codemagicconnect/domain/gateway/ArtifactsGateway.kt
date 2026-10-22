package io.github.agimaulana.codemagicconnect.domain.gateway

import io.github.agimaulana.codemagicconnect.domain.model.ArtifactDownload
import io.github.agimaulana.codemagicconnect.domain.model.BuildArtifact
import kotlinx.coroutines.flow.Flow

interface ArtifactsGateway {

    suspend fun download(buildId: String, artifact: BuildArtifact)

    fun observeDownloads(): Flow<List<ArtifactDownload>>

    suspend fun install(artifactId: String)

    suspend fun deleteDownload(artifactId: String)

    suspend fun clearDownloadedFiles()

    suspend fun downloadedFilesSizeBytes(): Long
}
