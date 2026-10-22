package io.github.agimaulana.codemagicconnect.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min

class CodemagicConnectColorsTest {

    @Test
    fun `given light theme when selecting palette then light tokens are returned`() {
        val colors = codemagicColors(darkTheme = false)

        assertEquals(Color(0xFFF5F6F8), colors.background)
        assertEquals(Color(0xFFFFFFFF), colors.surface)
        assertEquals(Color(0xFF191A1E), colors.onSurface)
    }

    @Test
    fun `given dark theme when selecting palette then dark tokens are returned`() {
        val colors = codemagicColors(darkTheme = true)

        assertEquals(Color(0xFF0F1116), colors.background)
        assertEquals(Color(0xFF191C22), colors.surface)
        assertEquals(Color(0xFFE8EBF0), colors.onSurface)
    }

    @Test
    fun `given dark theme when selecting palette then surfaces are darker than the light palette`() {
        val light = codemagicColors(darkTheme = false)
        val dark = codemagicColors(darkTheme = true)

        assertTrue(dark.background.luminance() < light.background.luminance())
        assertTrue(dark.surface.luminance() < light.surface.luminance())
        assertTrue(dark.surfaceVariant.luminance() < light.surfaceVariant.luminance())
        assertTrue(dark.onSurface.luminance() > light.onSurface.luminance())
        assertTrue(dark.onSurfaceVariant.luminance() > light.onSurfaceVariant.luminance())
    }

    @Test
    fun `given each palette when reading foreground tokens then they meet the minimum text contrast`() {
        listOf(codemagicColors(darkTheme = false), codemagicColors(darkTheme = true)).forEach { colors ->
            assertMinimumContrast(colors.onSurface, colors.background)
            assertMinimumContrast(colors.onSurface, colors.surface)
            assertMinimumContrast(colors.onSurfaceVariant, colors.surface)
            assertMinimumContrast(colors.onSurfaceVariant, colors.surfaceVariant)
            assertMinimumContrast(colors.onPrimary, colors.primary)
            assertMinimumContrast(colors.onPrimaryContainer, colors.primaryContainer)
            assertMinimumContrast(colors.onSuccess, colors.success)
            assertMinimumContrast(colors.onSuccessContainer, colors.successContainer)
            assertMinimumContrast(colors.onError, colors.error)
            assertMinimumContrast(colors.onErrorContainer, colors.errorContainer)
        }
    }

    private fun assertMinimumContrast(foreground: Color, background: Color) {
        val ratio = contrastRatio(foreground, background)
        assertTrue(
            "Expected at least $MINIMUM_TEXT_CONTRAST_RATIO:1 but was $ratio for $foreground on $background",
            ratio >= MINIMUM_TEXT_CONTRAST_RATIO
        )
    }

    private fun contrastRatio(foreground: Color, background: Color): Float {
        val lighter = max(foreground.luminance(), background.luminance())
        val darker = min(foreground.luminance(), background.luminance())
        return (lighter + CONTRAST_OFFSET) / (darker + CONTRAST_OFFSET)
    }

    private companion object {
        private const val MINIMUM_TEXT_CONTRAST_RATIO = 4.5f
        private const val CONTRAST_OFFSET = 0.05f
    }
}
