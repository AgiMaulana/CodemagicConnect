package io.github.agimaulana.codemagicconnect.data.download

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.agimaulana.codemagicconnect.core.dispatcher.DispatcherProvider
import io.github.agimaulana.codemagicconnect.data.di.DownloadClient
import io.github.agimaulana.codemagicconnect.data.local.dao.DownloadDao
import io.github.agimaulana.codemagicconnect.data.local.entity.DownloadEntity
import io.github.agimaulana.codemagicconnect.domain.model.BuildArtifact
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtifactDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
    @DownloadClient private val client: OkHttpClient,
    private val downloadDao: DownloadDao,
    private val dispatcherProvider: DispatcherProvider
) {

    suspend fun download(buildId: String, artifact: BuildArtifact) = withContext(dispatcherProvider.io()) {
        val url = artifact.downloadUrl
            ?: throw IllegalStateException("Artifact ${artifact.name} has no download URL")

        val directory = File(context.filesDir, DOWNLOAD_DIRECTORY).apply { mkdirs() }
        val fileName = resolveFileName(artifact.id, artifact.name)
        val target = File(directory, fileName)

        downloadDao.upsert(download(buildId, artifact, fileName, target, STATUS_DOWNLOADING, 0L, artifact.sizeBytes))

        try {
            client.newCall(Request.Builder().url(url).build()).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected HTTP ${response.code} while downloading ${artifact.name}")
                }
                val body = response.body ?: throw IOException("Empty response body for ${artifact.name}")
                val totalBytes = body.contentLength().takeIf { it > 0 } ?: artifact.sizeBytes
                write(body.byteStream(), buildId, artifact, fileName, target, totalBytes = totalBytes)
            }
            downloadDao.upsert(
                download(buildId, artifact, fileName, target, STATUS_DOWNLOADED, target.length(), target.length())
            )
        } catch (throwable: Throwable) {
            downloadDao.upsert(
                download(buildId, artifact, fileName, target, STATUS_FAILED, 0L, artifact.sizeBytes)
            )
            throw throwable
        }
    }

    private suspend fun write(
        input: InputStream,
        buildId: String,
        artifact: BuildArtifact,
        fileName: String,
        target: File,
        totalBytes: Long
    ) {
        var downloaded = 0L
        var lastReported = 0L
        target.outputStream().use { output ->
            val buffer = ByteArray(BUFFER_SIZE_BYTES)
            while (true) {
                val read = input.read(buffer)
                if (read == -1) break
                output.write(buffer, 0, read)
                downloaded += read
                if (downloaded - lastReported >= PROGRESS_UPDATE_BYTES) {
                    lastReported = downloaded
                    downloadDao.upsert(
                        download(buildId, artifact, fileName, target, STATUS_DOWNLOADING, downloaded, totalBytes)
                    )
                }
            }
        }
    }

    private fun download(
        buildId: String,
        artifact: BuildArtifact,
        fileName: String,
        target: File,
        status: String,
        downloadedBytes: Long,
        totalBytes: Long
    ) = DownloadEntity(
        artifactId = artifact.id,
        buildId = buildId,
        fileName = fileName,
        localPath = target.absolutePath,
        status = status,
        downloadedBytes = downloadedBytes,
        totalBytes = totalBytes
    )

    companion object {
        const val DOWNLOAD_DIRECTORY = "artifact_downloads"

        /**
         * Builds a file name from the artifact rather than from its URL: Codemagic download URLs
         * end in a long signed token, which overflows the 255-byte filesystem limit.
         */
        internal fun resolveFileName(artifactId: String, artifactName: String): String {
            val prefix = artifactId.replace(UNSAFE_FILE_NAME_CHARACTERS, "_")
            val sanitized = artifactName.replace(UNSAFE_FILE_NAME_CHARACTERS, "_").trim('.').ifBlank { FALLBACK_FILE_NAME }
            val extension = sanitized.substringAfterLast('.', missingDelimiterValue = "")
            val stem = if (extension.isEmpty()) sanitized else sanitized.substringBeforeLast('.')
            val suffix = if (extension.isEmpty()) "" else ".$extension"
            val stemBudget = (MAX_FILE_NAME_LENGTH - prefix.length - suffix.length - 1).coerceAtLeast(1)
            return "$prefix-${stem.take(stemBudget)}$suffix"
        }

        private val UNSAFE_FILE_NAME_CHARACTERS = Regex("[^A-Za-z0-9._-]")
        private const val FALLBACK_FILE_NAME = "artifact"
        private const val MAX_FILE_NAME_LENGTH = 120
        private const val STATUS_DOWNLOADING = "DOWNLOADING"
        private const val STATUS_DOWNLOADED = "DOWNLOADED"
        private const val STATUS_FAILED = "FAILED"
        private const val BUFFER_SIZE_BYTES = 8 * 1024
        private const val PROGRESS_UPDATE_BYTES = 256 * 1024L
    }
}
