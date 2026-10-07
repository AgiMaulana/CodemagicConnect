package io.github.agimaulana.codemagicconnect.domain.model

data class BuildArtifact(
    val id: String,
    val name: String,
    val sizeBytes: Long,
    val downloadStatus: DownloadStatus = DownloadStatus.NOT_DOWNLOADED,
    val downloadProgress: Float = 0f,
    val downloadSizeSoFarBytes: Long = 0L,
    val downloadUrl: String? = null
) {
    enum class DownloadStatus {
        NOT_DOWNLOADED,
        DOWNLOADING,
        DOWNLOADED,
        FAILED
    }
}