package io.github.agimaulana.codemagicconnect.ui.theme

import io.github.agimaulana.codemagicconnect.domain.model.ThemeMode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeModeTest {

    @Test
    fun `given follow system when system is dark then dark theme is used`() {
        assertTrue(ThemeMode.SYSTEM.toDarkTheme(systemInDarkTheme = true))
    }

    @Test
    fun `given follow system when system is light then light theme is used`() {
        assertFalse(ThemeMode.SYSTEM.toDarkTheme(systemInDarkTheme = false))
    }

    @Test
    fun `given light theme when system is dark then light theme is used`() {
        assertFalse(ThemeMode.LIGHT.toDarkTheme(systemInDarkTheme = true))
    }

    @Test
    fun `given dark theme when system is light then dark theme is used`() {
        assertTrue(ThemeMode.DARK.toDarkTheme(systemInDarkTheme = false))
    }
}
