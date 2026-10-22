package io.github.agimaulana.codemagicconnect.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Brand palette that Material's color scheme does not model: surface tiers, status tints and
 * dividers used across the feature screens. Screens read it through [CodemagicColors.current] so
 * every screen follows the active light or dark theme instead of hardcoded literals.
 */
internal data class CodemagicConnectColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val divider: Color,
    val border: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val warning: Color,
    val neutralContainer: Color
)

private val LightCodemagicConnectColors = CodemagicConnectColors(
    background = Color(0xFFF5F6F8),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF5F6F8),
    divider = Color(0xFFF5F6F8),
    border = Color(0xFFE2E8F0),
    onSurface = Color(0xFF191A1E),
    onSurfaceVariant = Color(0xFF5E6573),
    primary = Color(0xFF2E4DF5),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE8EEFC),
    onPrimaryContainer = Color(0xFF2E4DF5),
    success = Color(0xFF137333),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFE6F4EA),
    onSuccessContainer = Color(0xFF137333),
    error = Color(0xFFC5221F),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFCE8E6),
    onErrorContainer = Color(0xFFC5221F),
    warning = Color(0xFFF2B005),
    neutralContainer = Color(0xFFF1F3F4)
)

private val DarkCodemagicConnectColors = CodemagicConnectColors(
    background = Color(0xFF0F1116),
    surface = Color(0xFF191C22),
    surfaceVariant = Color(0xFF23272F),
    divider = Color(0xFF262B34),
    border = Color(0xFF333A47),
    onSurface = Color(0xFFE8EBF0),
    onSurfaceVariant = Color(0xFFA3ABB9),
    primary = Color(0xFF7E9BFF),
    onPrimary = Color(0xFF0A1020),
    primaryContainer = Color(0xFF232C4A),
    onPrimaryContainer = Color(0xFFC2CEFF),
    success = Color(0xFF4FC97E),
    onSuccess = Color(0xFF06240F),
    successContainer = Color(0xFF16301F),
    onSuccessContainer = Color(0xFF7FDBA0),
    error = Color(0xFFFF7A75),
    onError = Color(0xFF2A0605),
    errorContainer = Color(0xFF3A1B19),
    onErrorContainer = Color(0xFFFFB4AE),
    warning = Color(0xFFF5C544),
    neutralContainer = Color(0xFF262A33)
)

internal fun codemagicColors(darkTheme: Boolean): CodemagicConnectColors =
    if (darkTheme) DarkCodemagicConnectColors else LightCodemagicConnectColors

internal val LocalCodemagicColors = staticCompositionLocalOf { LightCodemagicConnectColors }

internal object CodemagicColors {
    val current: CodemagicConnectColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCodemagicColors.current
}
