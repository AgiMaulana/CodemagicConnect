package io.github.agimaulana.codemagicconnect.data.gateway

import app.cash.turbine.test
import io.github.agimaulana.codemagicconnect.data.local.dao.FavoriteDao
import io.github.agimaulana.codemagicconnect.data.local.dao.PreferenceDao
import io.github.agimaulana.codemagicconnect.data.local.entity.FavoriteEntity
import io.github.agimaulana.codemagicconnect.data.local.entity.PreferenceEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PreferencesGatewayImplTest {

    private val preferenceDao = mockk<PreferenceDao>()
    private val favoriteDao = mockk<FavoriteDao>()
    private val gateway = PreferencesGatewayImpl(preferenceDao, favoriteDao)

    @Test
    fun `given stored preferences when observed then maps stored values`() = runTest {
        every { preferenceDao.observeAll() } returns flowOf(
            listOf(
                PreferenceEntity("wifi_only", "false"),
                PreferenceEntity("default_app_name", "acme"),
                PreferenceEntity("delete_apk_after_install", "true")
            )
        )

        gateway.observePreferences().test {
            val preferences = awaitItem()
            assertFalse(preferences.wifiOnly)
            assertTrue(preferences.deleteApkAfterInstall)
            assertEquals("acme", preferences.defaultAppName)
            assertFalse(preferences.openAppAutomatically)
            awaitComplete()
        }
    }

    @Test
    fun `given app not favourited when toggled then it is inserted`() = runTest {
        coEvery { favoriteDao.exists("1") } returns false
        coEvery { favoriteDao.insert(any()) } returns Unit

        gateway.toggleFavorite("1")

        coVerify(exactly = 1) { favoriteDao.insert(FavoriteEntity("1")) }
        coVerify(exactly = 0) { favoriteDao.delete(any()) }
    }

    @Test
    fun `given app favourited when toggled then it is removed`() = runTest {
        coEvery { favoriteDao.exists("1") } returns true
        coEvery { favoriteDao.delete("1") } returns Unit

        gateway.toggleFavorite("1")

        coVerify(exactly = 1) { favoriteDao.delete("1") }
    }

    @Test
    fun `given default app when set then id and name are persisted`() = runTest {
        coEvery { preferenceDao.upsert(any()) } returns Unit

        gateway.setDefaultApplication("1", "acme")

        coVerify(exactly = 1) { preferenceDao.upsert(PreferenceEntity("default_app_id", "1")) }
        coVerify(exactly = 1) { preferenceDao.upsert(PreferenceEntity("default_app_name", "acme")) }
    }
}
