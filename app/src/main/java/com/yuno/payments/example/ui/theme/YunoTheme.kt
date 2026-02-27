package com.yuno.payments.example.ui.theme

/**
 * Simple demo theme for the Yuno SDK example app.
 *
 * This is NOT a production theme — it uses a minimal neutral dark-gray palette to keep
 * the focus on SDK integration code rather than UI design. In a real app, replace this
 * with your own design system / brand theme.
 *
 * Key details:
 * - Supports light/dark mode and optional Material You dynamic colors (Android 12+)
 * - Extended colors (success/warning) are provided via LocalYunoColors for the StatusCard
 * - safeDrawingPadding() is applied at the theme level to handle Android 15 edge-to-edge
 */

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// Yuno brand colors — neutral dark gray palette
val YunoDarkGray = Color(0xFF2B2B2B)        // Primary brand color (dark charcoal)
val YunoDarkGrayDeep = Color(0xFF1A1A1A)    // Darker variant
val YunoDarkGrayLight = Color(0xFFB0B0B0)   // Lighter variant
val YunoMediumGray = Color(0xFF6E6E6E)      // Secondary
val YunoWarmGray = Color(0xFF7A7268)         // Tertiary (slightly warmer neutral)
val YunoGreen = Color(0xFF00C48C)
val YunoRed = Color(0xFFFF4757)
val YunoAmber = Color(0xFFFFB020)
val YunoGray = Color(0xFF8E8E93)
val YunoLightGray = Color(0xFFF2F2F7)
val YunoNearBlack = Color(0xFF1C1C1E)       // Background (dark mode)
val YunoWhite = Color(0xFFFFFFFF)
val YunoBlack = Color(0xFF000000)

// Extended color roles for status indicators
data class YunoExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
)

private val LightExtendedColors = YunoExtendedColors(
    success = YunoGreen,
    onSuccess = YunoWhite,
    successContainer = Color(0xFFD4F5E9),
    onSuccessContainer = Color(0xFF003822),
    warning = YunoAmber,
    onWarning = YunoBlack,
    warningContainer = Color(0xFFFFF0D4),
    onWarningContainer = Color(0xFF3D2E00),
)

private val DarkExtendedColors = YunoExtendedColors(
    success = Color(0xFF55DBA4),
    onSuccess = YunoBlack,
    successContainer = Color(0xFF1B4332),
    onSuccessContainer = Color(0xFFD4F5E9),
    warning = Color(0xFFFFCB5C),
    onWarning = YunoBlack,
    warningContainer = Color(0xFF3D2E00),
    onWarningContainer = Color(0xFFFFF0D4),
)

val LocalYunoColors = staticCompositionLocalOf { LightExtendedColors }

private val LightColorScheme = lightColorScheme(
    primary = YunoDarkGray,
    onPrimary = YunoWhite,
    primaryContainer = Color(0xFFE0E0E0),
    onPrimaryContainer = Color(0xFF1A1A1A),
    secondary = YunoMediumGray,
    onSecondary = YunoWhite,
    tertiary = YunoWarmGray,
    onTertiary = YunoWhite,
    error = YunoRed,
    onError = YunoWhite,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = YunoWhite,
    onBackground = YunoBlack,
    surface = YunoWhite,
    onSurface = YunoBlack,
    surfaceVariant = YunoLightGray,
    onSurfaceVariant = YunoGray,
    outline = YunoGray,
)

private val DarkColorScheme = darkColorScheme(
    primary = YunoDarkGrayLight,
    onPrimary = YunoBlack,
    primaryContainer = YunoDarkGrayDeep,
    onPrimaryContainer = YunoWhite,
    secondary = Color(0xFF9E9E9E),
    onSecondary = YunoBlack,
    tertiary = Color(0xFF9A9189),
    onTertiary = YunoBlack,
    error = YunoRed,
    onError = YunoWhite,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = YunoNearBlack,
    onBackground = YunoWhite,
    surface = YunoNearBlack,
    onSurface = YunoWhite,
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = YunoGray,
    outline = YunoGray,
)

val YunoTypography = Typography(
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
)

val YunoShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun YunoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalYunoColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = YunoTypography,
            shapes = YunoShapes,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
                color = MaterialTheme.colorScheme.background,
                content = content,
            )
        }
    }
}
