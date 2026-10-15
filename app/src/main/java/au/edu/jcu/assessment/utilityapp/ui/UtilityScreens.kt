package au.edu.jcu.assessment.utilityapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.data.City
import au.edu.jcu.assessment.utilityapp.data.Weather
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import au.edu.jcu.assessment.utilityapp.ui.theme.ThemeMode

@Composable
fun UtilityApp(
    state: WeatherUiState,
    onRefresh: () -> Unit,
    onCityChange: (City) -> Unit,
    onUnitsChange: (Boolean) -> Unit,
    onTipsChange: (Boolean) -> Unit,
    onThemeChange: (ThemeMode) -> Unit
) {
    var settings by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = settings) { settings = false }
    Scaffold(bottomBar = {
        NavigationBar {
            NavigationBarItem(
                modifier = Modifier.testTag("today_tab"),
                selected = !settings,
                onClick = { settings = false },
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = { Text(stringResource(R.string.weather_tab)) }
            )
            NavigationBarItem(
                modifier = Modifier.testTag("settings_tab"),
                selected = settings,
                onClick = { settings = true },
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                label = { Text(stringResource(R.string.settings_title)) }
            )
        }
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (settings) {
                SettingsScreen(state, onCityChange, onUnitsChange, onTipsChange, onThemeChange)
            } else {
                UtilityScreen(state, onRefresh)
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, widthDp = 360, fontScale = 1.5f)
@Composable
private fun WeatherPreview() {
    MobileTech_UtilityAppTheme(mode = ThemeMode.LIGHT) {
        UtilityApp(
            WeatherUiState(weather = Weather(26.0, 27.0, 2, 0.0, 12.0,
                "2026-10-15T09:00", "Australia/Brisbane")),
            {}, {}, {}, {}, {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DarkWeatherPreview() {
    MobileTech_UtilityAppTheme(mode = ThemeMode.DARK) {
        UtilityApp(WeatherUiState(loading = true), {}, {}, {}, {}, {})
    }
}
