package io.github.agimaulana.codemagicconnect.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.agimaulana.codemagicconnect.data.local.dao.AuthTokenDao
import io.github.agimaulana.codemagicconnect.data.local.dao.DownloadDao
import io.github.agimaulana.codemagicconnect.data.local.dao.FavoriteDao
import io.github.agimaulana.codemagicconnect.data.local.dao.PreferenceDao
import io.github.agimaulana.codemagicconnect.data.local.entity.AuthTokenEntity
import io.github.agimaulana.codemagicconnect.data.local.entity.DownloadEntity
import io.github.agimaulana.codemagicconnect.data.local.entity.FavoriteEntity
import io.github.agimaulana.codemagicconnect.data.local.entity.PreferenceEntity

@Database(
    entities = [
        AuthTokenEntity::class,
        PreferenceEntity::class,
        FavoriteEntity::class,
        DownloadEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CodemagicDatabase : RoomDatabase() {

    abstract fun authTokenDao(): AuthTokenDao

    abstract fun preferenceDao(): PreferenceDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun downloadDao(): DownloadDao
}
