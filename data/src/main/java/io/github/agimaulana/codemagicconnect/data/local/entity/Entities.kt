package io.github.agimaulana.codemagicconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_token")
data class AuthTokenEntity(
    @PrimaryKey val id: Int = SINGLE_TOKEN_ROW_ID,
    val token: String,
    val addedAtEpochMillis: Long,
    val lastVerifiedAtEpochMillis: Long
) {
    companion object {
        const val SINGLE_TOKEN_ROW_ID = 0
    }
}

@Entity(tableName = "preference")
data class PreferenceEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "favorite")
data class FavoriteEntity(
    @PrimaryKey val appId: String
)

@Entity(tableName = "download")
data class DownloadEntity(
    @PrimaryKey val artifactId: String,
    val buildId: String,
    val fileName: String,
    val localPath: String?,
    val status: String,
    val downloadedBytes: Long,
    val totalBytes: Long
)
