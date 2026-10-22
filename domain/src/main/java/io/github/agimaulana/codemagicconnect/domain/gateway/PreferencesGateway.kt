package io.github.agimaulana.codemagicconnect.domain.gateway

import io.github.agimaulana.codemagicconnect.domain.model.AppPreferences
import io.github.agimaulana.codemagicconnect.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface PreferencesGateway {

    fun observePreferences(): Flow<AppPreferences>

    fun observeFavoriteIds(): Flow<Set<String>>

    suspend fun setOpenAppAutomatically(enabled: Boolean)

    suspend fun setWifiOnly(enabled: Boolean)

    suspend fun setDeleteApkAfterInstall(enabled: Boolean)

    suspend fun setThemeMode(mode: ThemeMode)

    suspend fun setDefaultApplication(id: String, name: String)

    suspend fun clearDefaultApplication()

    suspend fun toggleFavorite(appId: String)
}
