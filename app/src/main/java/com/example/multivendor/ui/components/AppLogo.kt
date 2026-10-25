package com.example.multivendor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.multivendor.theme.BrandTeal
import com.example.multivendor.theme.BrandTealDark

/**
 * In-app brand mark mirroring the launcher icon:
 * teal-gradient rounded tile with a white shopping bag.
 */
@Composable
fun AppLogoMark(
    modifier: Modifier = Modifier,
    size: Int = 44
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape((size * 0.32).dp),
                spotColor = BrandTeal.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape((size * 0.32).dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF2DD4BF), BrandTeal, BrandTealDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle highlight dot for premium feel.
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .padding(top = (size * 0.18).dp, end = (size * 0.2).dp)
                .size((size * 0.12).dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Color.White.copy(alpha = 0.55f))
        )
        Icon(
            imageVector = Icons.Filled.ShoppingBag,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size((size * 0.52).dp)
        )
    }
}

@Composable
fun AppLogo(
    appName: String = "Avenue & Co",
    tagline: String = "Many sellers • One cart",
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AppLogoMark()
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = appName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
            )
            Text(
                text = tagline,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
