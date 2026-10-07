package io.github.agimaulana.codemagicconnect.data.gateway

import android.content.Context
import io.github.agimaulana.codemagicconnect.core.dispatcher.DispatcherProvider
import io.github.agimaulana.codemagicconnect.data.download.ArtifactDownloader
import io.github.agimaulana.codemagicconnect.data.download.ArtifactInstaller
import io.github.agimaulana.codemagicconnect.data.local.dao.DownloadDao
import io.github.agimaulana.codemagicconnect.data.local.entity.DownloadEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.File

class ArtifactsGatewayImplTest {

    private val context = mockk<Context>()
    private val downloadDao = mockk<DownloadDao>()
    private val artifactDownloader = mockk<ArtifactDownloader>()
    private val artifactInstaller = mockk<ArtifactInstaller>()
    private val dispatcherProvider = mockk<DispatcherProvider>()

    private val gateway = ArtifactsGatewayImpl(
        context = context,
        downloadDao = downloadDao,
        artifactDownloader = artifactDownloader,
        artifactInstaller = artifactInstaller,
        dispatcherProvider = dispatcherProvider
    )

    @Test
    fun `given downloaded file when deleteDownload then file and row are removed`() = runTest {
        every { dispatcherProvider.io() } returns Dispatchers.Unconfined
        val apk = File.createTempFile("auto-delete", ".apk")
        coEvery { downloadDao.get("a1") } returns DownloadEntity(
            artifactId = "a1",
            buildId = "b1",
            fileName = apk.name,
            localPath = apk.absolutePath,
            status = "DOWNLOADED",
            downloadedBytes = apk.length(),
            totalBytes = apk.length()
        )
        coEvery { downloadDao.delete("a1") } returns Unit

        gateway.deleteDownload("a1")

        assertFalse(apk.exists())
        coVerify(exactly = 1) { downloadDao.delete("a1") }
    }

    @Test
    fun `given missing download when deleteDownload then no crash without row delete`() = runTest {
        every { dispatcherProvider.io() } returns Dispatchers.Unconfined
        coEvery { downloadDao.get("missing") } returns null

        gateway.deleteDownload("missing")

        coVerify(exactly = 0) { downloadDao.delete(any()) }
    }
}
