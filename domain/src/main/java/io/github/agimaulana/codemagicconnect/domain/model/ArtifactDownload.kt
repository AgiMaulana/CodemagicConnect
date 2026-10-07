package io.github.agimaulana.codemagicconnect.domain.model

data class ArtifactDownload(
    val artifactId: String,
    val buildId: String,
    val status: BuildArtifact.DownloadStatus,
    val progress: Float = 0f,
    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val localPath: String? = null
)
