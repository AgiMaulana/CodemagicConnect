package io.github.agimaulana.codemagicconnect.data.local.mapper

import io.github.agimaulana.codemagicconnect.data.local.entity.AuthTokenEntity
import io.github.agimaulana.codemagicconnect.data.local.entity.DownloadEntity
import io.github.agimaulana.codemagicconnect.domain.model.ArtifactDownload
import io.github.agimaulana.codemagicconnect.domain.model.BuildArtifact
import io.github.agimaulana.codemagicconnect.domain.model.TokenInfo

internal fun AuthTokenEntity.toDomain(): TokenInfo = TokenInfo(
    token = token,
    addedAtEpochMillis = addedAtEpochMillis,
    lastVerifiedAtEpochMillis = lastVerifiedAtEpochMillis
)

internal fun DownloadEntity.toDomain(): ArtifactDownload = ArtifactDownload(
    artifactId = artifactId,
    buildId = buildId,
    status = status.toDownloadStatus(),
    progress = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f,
    downloadedBytes = downloadedBytes,
    totalBytes = totalBytes,
    localPath = localPath
)

private fun String.toDownloadStatus(): BuildArtifact.DownloadStatus =
    BuildArtifact.DownloadStatus.entries.firstOrNull { it.name == this }
        ?: BuildArtifact.DownloadStatus.NOT_DOWNLOADED
