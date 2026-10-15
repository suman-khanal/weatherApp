package au.edu.jcu.assessment.utilityapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = DayTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB5F2E8),
    onPrimaryContainer = Color(0xFF00201D),
    secondary = Color(0xFF4A635F),
    secondaryContainer = Color(0xFFCDE8E1),
    onSecondaryContainer = Color(0xFF06201B),
    tertiary = SunGold,
    tertiaryContainer = Color(0xFFFFDEA5),
    onTertiaryContainer = Color(0xFF291800),
    background = Color(0xFFF5FAF7),
    surface = Color(0xFFF5FAF7),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF0F7F2),
    surfaceContainer = Color(0xFFEAF2ED),
    surfaceContainerHigh = Color(0xFFE2EDE6),
    surfaceContainerHighest = Color(0xFFDAE6DE),
    outline = Color(0xFF6F7973),
    outlineVariant = Color(0xFFBEC9C1),
    onSurface = Color(0xFF171D1B),
    surfaceVariant = Color(0xFFDAE5DF),
    onSurfaceVariant = Color(0xFF3F4945)
)

private val DarkColors = darkColorScheme(
    primary = NightTeal,
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF005049),
    onPrimaryContainer = Color(0xFFB5F2E8),
    secondary = Color(0xFFB1CCC5),
    secondaryContainer = Color(0xFF334B45),
    onSecondaryContainer = Color(0xFFCDE8E1),
    tertiary = NightGold,
    tertiaryContainer = Color(0xFF614000),
    onTertiaryContainer = Color(0xFFFFDEA5),
    background = Color(0xFF0F1513),
    surface = Color(0xFF0F1513),
    surfaceContainerLowest = Color(0xFF0A100D),
    surfaceContainerLow = Color(0xFF17201B),
    surfaceContainer = Color(0xFF1B261F),
    surfaceContainerHigh = Color(0xFF253028),
    surfaceContainerHighest = Color(0xFF303B33),
    outline = Color(0xFF88938B),
    outlineVariant = Color(0xFF3F4945),
    onSurface = Color(0xFFDFE4DF),
    surfaceVariant = Color(0xFF3F4945),
    onSurfaceVariant = Color(0xFFBEC9C2)
)

@Composable
fun MobileTech_UtilityAppTheme(
    mode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val dark = when (mode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MaterialTheme(colorScheme = if (dark) DarkColors else LightColors, typography = Typography, content = content)
}
