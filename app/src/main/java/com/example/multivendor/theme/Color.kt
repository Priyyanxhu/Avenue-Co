package com.example.multivendor.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// MultiVendor brand palette — Dark Royal Blue theme.
val BrandRoyalBlueDark = Color(0xFF0A192F) // Deep midnight royal blue
val BrandRoyalBlue = Color(0xFF0F2557)     // Dark Royal Blue primary
val BrandRoyalBlueRich = Color(0xFF1E3A8A) // Rich Dark Royal Blue
val BrandRoyalBlueAccent = Color(0xFF1D4ED8) // Vibrant royal blue accent
val BrandRoyalBlueLight = Color(0xFFDBEAFE) // Light royal blue tint
val BrandRoyalBlueTint = Color(0xFFEFF6FF)  // Soft tinted background

val BrandTeal = Color(0xFF0F2557)
val BrandTealDark = Color(0xFF0A192F)
val BrandTealDeep = Color(0xFF061122)
val BrandTealLight = Color(0xFFDBEAFE)
val BrandTealTint = Color(0xFFEFF6FF)
val BrandOrange = Color(0xFF2563EB)
val BrandOrangeDark = Color(0xFF1D4ED8)
val BrandOrangeLight = Color(0xFFDBEAFE)
val BrandOrangeTint = Color(0xFFEFF6FF)
val BrandInk = Color(0xFF0A192F)
val BrandInkSoft = Color(0xFF172554)
val BrandMuted = Color(0xFF64748B)
val BrandLine = Color(0xFFE2E8F0)
val StarAmber = Color(0xFFF59E0B)
val StarAmberDeep = Color(0xFFB45309)
val SuccessGreen = Color(0xFF15803D)
val SuccessGreenDark = Color(0xFF166534)
val SuccessBg = Color(0xFFDCFCE7)
val ErrorRed = Color(0xFFDC2626)
val ErrorBg = Color(0xFFFEE2E2)
val WishlistPink = Color(0xFFE11D48)
val WishlistPinkBg = Color(0xFFFFE4E6)

// Product image gradient pairs (cycled by gradientSeed).
val GradientPairs = listOf(
    Pair(Color(0xFF0EA5E9), Color(0xFF6366F1)),
    Pair(Color(0xFFF59E0B), Color(0xFFEF4444)),
    Pair(Color(0xFF10B981), Color(0xFF0D9488)),
    Pair(Color(0xFF8B5CF6), Color(0xFFD946EF)),
    Pair(Color(0xFFF97316), Color(0xFFEAB308)),
    Pair(Color(0xFFEC4899), Color(0xFF8B5CF6)),
    Pair(Color(0xFF14B8A6), Color(0xFF3B82F6)),
    Pair(Color(0xFFEF4444), Color(0xFF7C2D12)),
    Pair(Color(0xFF6366F1), Color(0xFF0EA5E9)),
    Pair(Color(0xFF22C55E), Color(0xFF15803D)),
    Pair(Color(0xFF0F766E), Color(0xFF134E4A)),
    Pair(Color(0xFFFB7185), Color(0xFFF59E0B)),
    Pair(Color(0xFF38BDF8), Color(0xFF1E40AF)),
    Pair(Color(0xFFA78BFA), Color(0xFF4C1D95))
)

fun gradientFor(seed: Int): Pair<Color, Color> =
    GradientPairs[Math.floorMod(seed, GradientPairs.size)]
