package io.github.agimaulana.codemagicconnect.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.agimaulana.codemagicconnect.data.local.dao.AuthTokenDao
import io.github.agimaulana.codemagicconnect.data.local.dao.DownloadDao
import io.github.agimaulana.codemagicconnect.data.local.dao.FavoriteDao
import io.github.agimaulana.codemagicconnect.data.local.dao.PreferenceDao
import io.github.agimaulana.codemagicconnect.data.local.db.CodemagicDatabase
import io.github.agimaulana.codemagicconnect.data.local.keystore.DatabasePassphraseProvider
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        passphraseProvider: DatabasePassphraseProvider
    ): CodemagicDatabase {
        System.loadLibrary(SQLCIPHER_LIBRARY)
        val factory = SupportOpenHelperFactory(passphraseProvider.passphrase())
        return Room.databaseBuilder(context, CodemagicDatabase::class.java, DATABASE_NAME)
            .openHelperFactory(factory)
            .build()
    }

    @Provides
    fun provideAuthTokenDao(database: CodemagicDatabase): AuthTokenDao = database.authTokenDao()

    @Provides
    fun providePreferenceDao(database: CodemagicDatabase): PreferenceDao = database.preferenceDao()

    @Provides
    fun provideFavoriteDao(database: CodemagicDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideDownloadDao(database: CodemagicDatabase): DownloadDao = database.downloadDao()

    private const val SQLCIPHER_LIBRARY = "sqlcipher"
    private const val DATABASE_NAME = "codemagic.db"
}
