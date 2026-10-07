package io.github.agimaulana.codemagicconnect.data.download

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtifactInstaller @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun install(apkFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}$FILE_PROVIDER_SUFFIX",
            apkFile
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, APK_MIME_TYPE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    companion object {
        private const val FILE_PROVIDER_SUFFIX = ".fileprovider"
        private const val APK_MIME_TYPE = "application/vnd.android.package-archive"
    }
}
