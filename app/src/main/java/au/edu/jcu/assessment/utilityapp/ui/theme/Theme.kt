package au.edu.jcu.assessment.utilityapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val lightColors = lightColorScheme(
    primary = SkyBlue, onPrimary = Color.White,
    primaryContainer = CloudBlue, onPrimaryContainer = DeepNavy,
    secondary = DeepNavy, background = Color(0xFFF7FAFE),
    surface = Color.White, onSurface = DeepNavy
)
private val darkColors = darkColorScheme(
    primary = Aqua, onPrimary = DeepNavy,
    primaryContainer = Color(0xFF17466B), onPrimaryContainer = CloudBlue,
    secondary = Aqua, background = Color(0xFF0C1927),
    surface = Color(0xFF142A3D), onSurface = Color(0xFFEAF4FF)
)

/** Fixed brand colours keep the UI consistent with the launcher artwork. */
@Composable
fun MobileTech_UtilityAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColors else lightColors,
        typography = Typography,
        content = content
    )
}
