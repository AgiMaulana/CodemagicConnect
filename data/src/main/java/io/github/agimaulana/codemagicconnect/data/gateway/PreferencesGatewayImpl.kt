package io.github.agimaulana.codemagicconnect.data.gateway

import io.github.agimaulana.codemagicconnect.data.local.dao.FavoriteDao
import io.github.agimaulana.codemagicconnect.data.local.dao.PreferenceDao
import io.github.agimaulana.codemagicconnect.data.local.entity.FavoriteEntity
import io.github.agimaulana.codemagicconnect.data.local.entity.PreferenceEntity
import io.github.agimaulana.codemagicconnect.domain.gateway.PreferencesGateway
import io.github.agimaulana.codemagicconnect.domain.model.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesGatewayImpl @Inject constructor(
    private val preferenceDao: PreferenceDao,
    private val favoriteDao: FavoriteDao
) : PreferencesGateway {

    override fun observePreferences(): Flow<AppPreferences> = preferenceDao.observeAll().map { rows ->
        val values = rows.associate { it.key to it.value }
        AppPreferences(
            openAppAutomatically = values[KEY_OPEN_APP_AUTOMATICALLY]?.toBoolean() ?: false,
            wifiOnly = values[KEY_WIFI_ONLY]?.toBoolean() ?: true,
            deleteApkAfterInstall = values[KEY_DELETE_APK_AFTER_INSTALL]?.toBoolean() ?: false,
            defaultAppId = values[KEY_DEFAULT_APP_ID],
            defaultAppName = values[KEY_DEFAULT_APP_NAME]
        )
    }

    override fun observeFavoriteIds(): Flow<Set<String>> =
        favoriteDao.observeIds().map { it.toSet() }

    override suspend fun setOpenAppAutomatically(enabled: Boolean) {
        putBoolean(KEY_OPEN_APP_AUTOMATICALLY, enabled)
    }

    override suspend fun setWifiOnly(enabled: Boolean) {
        putBoolean(KEY_WIFI_ONLY, enabled)
    }

    override suspend fun setDeleteApkAfterInstall(enabled: Boolean) {
        putBoolean(KEY_DELETE_APK_AFTER_INSTALL, enabled)
    }

    override suspend fun setDefaultApplication(id: String, name: String) {
        preferenceDao.upsert(PreferenceEntity(KEY_DEFAULT_APP_ID, id))
        preferenceDao.upsert(PreferenceEntity(KEY_DEFAULT_APP_NAME, name))
    }

    override suspend fun clearDefaultApplication() {
        preferenceDao.delete(KEY_DEFAULT_APP_ID)
        preferenceDao.delete(KEY_DEFAULT_APP_NAME)
    }

    override suspend fun toggleFavorite(appId: String) {
        if (favoriteDao.exists(appId)) {
            favoriteDao.delete(appId)
        } else {
            favoriteDao.insert(FavoriteEntity(appId))
        }
    }

    private suspend fun putBoolean(key: String, value: Boolean) {
        preferenceDao.upsert(PreferenceEntity(key, value.toString()))
    }

    companion object {
        private const val KEY_OPEN_APP_AUTOMATICALLY = "open_app_automatically"
        private const val KEY_WIFI_ONLY = "wifi_only"
        private const val KEY_DELETE_APK_AFTER_INSTALL = "delete_apk_after_install"
        private const val KEY_DEFAULT_APP_ID = "default_app_id"
        private const val KEY_DEFAULT_APP_NAME = "default_app_name"
    }
}
