package com.farmmanagement.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Core agricultural green palette, matched to the reference UI header/buttons.
val FarmGreenDark = Color(0xFF14532D)   // header background
val FarmGreen = Color(0xFF1E7A34)       // primary buttons / active states
val FarmGreenLight = Color(0xFFE7F5EA)  // soft card backgrounds
val AccentFemale = Color(0xFFD6336C)
val AccentMale = Color(0xFF1D6FE0)
val AccentContract = Color(0xFFE8871E)
val AccentPurchase = Color(0xFF7C4DFF)
val AccentStore = Color(0xFFB5850C)
val AccentExpense = Color(0xFFC62828)
val WarnAmber = Color(0xFFE8871E)
val SurfaceCard = Color(0xFFFFFFFF)
val BackgroundGray = Color(0xFFF4F6F5)

private val LightColors = lightColorScheme(
    primary = FarmGreen,
    onPrimary = Color.White,
    primaryContainer = FarmGreenLight,
    onPrimaryContainer = FarmGreenDark,
    secondary = AccentMale,
    background = BackgroundGray,
    surface = SurfaceCard,
    error = AccentExpense,
)

private val AppTypography = Typography(
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    bodyLarge = TextStyle(fontSize = 15.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.sp),
)

@Composable
fun FarmManagementTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content,
    )
}
