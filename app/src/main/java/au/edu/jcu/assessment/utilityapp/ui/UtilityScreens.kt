package au.edu.jcu.assessment.utilityapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import au.edu.jcu.assessment.utilityapp.data.*
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import kotlin.math.roundToInt

@Composable
fun UtilityApp(
    state: WeatherUiState,
    onRefresh: () -> Unit,
    onCityChange: (City) -> Unit,
    onUnitsChange: (Boolean) -> Unit,
    onTipsChange: (Boolean) -> Unit
) {
    var settings by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = settings) { settings = false }
    Scaffold(bottomBar = {
        NavigationBar {
            NavigationBarItem(selected = !settings, onClick = { settings = false },
                icon = { Icon(Icons.Default.Home, null) }, label = { Text("DayReady") })
            NavigationBarItem(selected = settings, onClick = { settings = true },
                icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") })
        }
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (settings) SettingsScreen(state, onCityChange, onUnitsChange, onTipsChange)
            else UtilityScreen(state, onRefresh)
        }
    }
}

@Composable
fun UtilityScreen(state: WeatherUiState, onRefresh: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("DayReady", style = MaterialTheme.typography.headlineLarge)
        Text("A quick check before you head out.")
        Text(state.city.name, style = MaterialTheme.typography.titleLarge)
        if (state.loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Text("Updating current weather…")
        }
        state.error?.let { message ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text(message)
                    if (state.weather != null) Text("Previous data is shown below; it may be out of date.")
                    TextButton(onClick = onRefresh, enabled = !state.loading) { Text("Retry") }
                }
            }
        }
        state.weather?.let { weather ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(temperatureLabel(weather.temperature, state.fahrenheit),
                        style = MaterialTheme.typography.displayLarge)
                    Text(conditionLabel(weather.code), style = MaterialTheme.typography.titleLarge)
                    Text("Feels like ${temperatureLabel(weather.feelsLike, state.fahrenheit)}")
                    Text("Wind ${weather.wind.roundToInt()} km/h • Rain ${weather.rain} mm")
                    Text("Weather time: ${weather.time.replace('T', ' ')}")
                    Text(weather.timezone, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (state.showTips) {
                Text("Before you leave", style = MaterialTheme.typography.titleLarge)
                preparationTips(weather).forEach { tip ->
                    OutlinedCard(Modifier.fillMaxWidth()) {
                        Text(tip, Modifier.padding(16.dp))
                    }
                }
                Text("Suggestions use current conditions, not a full-day forecast.",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        Button(onClick = onRefresh, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) {
            Text("Refresh weather")
        }
        TextButton(onClick = { uriHandler.openUri("https://open-meteo.com/") }) {
            Text("Weather data by Open-Meteo · CC BY 4.0")
        }
    }
}

@Composable
fun SettingsScreen(
    state: WeatherUiState, onCityChange: (City) -> Unit,
    onUnitsChange: (Boolean) -> Unit, onTipsChange: (Boolean) -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)
        Text("Changes apply immediately.")
        Text("Weather location", style = MaterialTheme.typography.titleLarge)
        // Full-width labelled buttons remain usable with large fonts and narrow screens.
        cities.forEach { city ->
            OutlinedButton(onClick = { onCityChange(city) }, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.city == city) "✓ ${city.name}" else city.name)
            }
        }
        PreferenceSwitch("Use Fahrenheit", state.fahrenheit, onUnitsChange)
        PreferenceSwitch("Show preparation tips", state.showTips, onTipsChange)
        Text("Locations use city-centre coordinates. Settings remain during rotation but reset when a new app session starts.",
            style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun PreferenceSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(label, Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange,
            modifier = Modifier.semantics { contentDescription = label })
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    MobileTech_UtilityAppTheme {
        UtilityApp(WeatherUiState(weather = Weather(26.0,27.0,2,0.0,12.0,
            "2026-10-12T09:00","Australia/Brisbane")), {}, {}, {}, {})
    }
}
