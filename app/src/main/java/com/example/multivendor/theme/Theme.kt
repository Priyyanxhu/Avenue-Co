package com.example.multivendor.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF5EEAD4),
    onPrimary = Color(0xFF04211E),
    primaryContainer = Color(0xFF0F766E),
    onPrimaryContainer = Color(0xFFCCFBF1),
    secondary = Color(0xFFFB923C),
    onSecondary = Color(0xFF2A1203),
    secondaryContainer = Color(0xFF7C2D12),
    onSecondaryContainer = Color(0xFFFFEDD5),
    tertiary = Color(0xFFF0ABFC),
    onTertiaryContainer = Color(0xFF2A0A33),
    background = Color(0xFF0B0F14),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFF9CA3AF),
    surfaceContainerLowest = Color(0xFF0B0F14),
    surfaceContainerLow = Color(0xFF111827),
    surfaceContainer = Color(0xFF1A2333),
    surfaceContainerHigh = Color(0xFF232E42),
    surfaceContainerHighest = Color(0xFF2E3B52),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1F2937),
    error = Color(0xFFFCA5A5),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandTeal,
    onPrimary = Color.White,
    primaryContainer = BrandTealLight,
    onPrimaryContainer = BrandTealDark,
    secondary = BrandOrange,
    onSecondary = Color.White,
    secondaryContainer = BrandOrangeLight,
    onSecondaryContainer = Color(0xFF7C2D12),
    tertiary = Color(0xFF7C3AED),
    onTertiaryContainer = Color(0xFFF5F3FF),
    background = Color(0xFFF6F8FA),
    onBackground = BrandInk,
    surface = Color.White,
    onSurface = BrandInk,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF8FAFC),
    surfaceContainer = Color(0xFFF1F5F9),
    surfaceContainerHigh = Color(0xFFE8EEF4),
    surfaceContainerHighest = Color(0xFFDEE6EF),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorBg,
    onErrorContainer = Color(0xFF7F1D1D)
)

@Composable
fun MultiVendorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Brand-first: dynamic color off by default so the marketplace keeps its identity.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, shapes = AppShapes, content = content)
}
