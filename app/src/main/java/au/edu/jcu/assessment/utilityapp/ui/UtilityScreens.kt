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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.data.*
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import kotlin.math.roundToInt

/** Hosts the two required screens without introducing unnecessary navigation libraries. */
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
            NavigationBarItem(
                selected = !settings, onClick = { settings = false },
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = { Text("Weather") }
            )
            NavigationBarItem(
                selected = settings, onClick = { settings = true },
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                label = { Text("Settings") }
            )
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
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("☀  DayReady", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Your weather, your day.", style = MaterialTheme.typography.bodyLarge)
        Text("CURRENT CONDITIONS  •  ${state.city.name.uppercase()}",
            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)

        if (state.loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Text("Checking the latest conditions…")
        }
        state.error?.let { message ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(message, color = MaterialTheme.colorScheme.onErrorContainer)
                    if (state.weather != null) Text("Previously loaded conditions are shown; they may be outdated.")
                    TextButton(onClick = onRefresh, enabled = !state.loading) { Text("Try again") }
                }
            }
        }
        state.weather?.let { weather ->
            ElevatedCard(Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )) {
                Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(weatherSymbol(weather.code), style = MaterialTheme.typography.displayMedium)
                    Text(temperatureLabel(weather.temperature, state.fahrenheit),
                        style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                    Text(conditionLabel(weather.code), style = MaterialTheme.typography.headlineSmall)
                    Text("Feels like ${temperatureLabel(weather.feelsLike, state.fahrenheit)}")
                    HorizontalDivider()
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        WeatherMetric("💨", "Wind", "${weather.wind.roundToInt()} km/h", Modifier.weight(1f))
                        WeatherMetric("☔", "Rain", "${weather.rain} mm", Modifier.weight(1f))
                    }
                    Text("Updated for ${weather.time.replace('T', ' ')} • ${weather.timezone}",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
            if (state.showTips) {
                Text("Ready for the day?", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold)
                preparationTips(weather).forEach { tip ->
                    OutlinedCard(Modifier.fillMaxWidth()) {
                        Text("✓  $tip", Modifier.padding(16.dp))
                    }
                }
                Text("Tips use current conditions, not a full-day forecast.",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        if (!state.loading && state.weather == null && state.error == null) {
            Text("Weather will appear here when it becomes available.")
        }
        Button(onClick = onRefresh, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) {
            Text("↻  Refresh weather")
        }
        TextButton(onClick = { uriHandler.openUri("https://open-meteo.com/") }) {
            Text("Weather data: Open-Meteo · CC BY 4.0")
        }
    }
}

@Composable
private fun WeatherMetric(symbol: String, label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("$symbol  $label", style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

private fun weatherSymbol(code: Int): String = when (code) {
    0 -> "☀️"
    1, 2 -> "🌤️"
    3 -> "☁️"
    45, 48 -> "🌫️"
    51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> "🌧️"
    71, 73, 75, 77, 85, 86 -> "❄️"
    95, 96, 99 -> "⛈️"
    else -> "🌡️"
}

@Composable
fun SettingsScreen(
    state: WeatherUiState,
    onCityChange: (City) -> Unit,
    onUnitsChange: (Boolean) -> Unit,
    onTipsChange: (Boolean) -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Preferences", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
        Text("Make DayReady work for you.")
        Text("Location", style = MaterialTheme.typography.titleLarge)
        Text("Choose a city to view its current weather.", style = MaterialTheme.typography.bodyMedium)
        // Radio buttons expose the selected city to accessibility services.
        cities.forEach { city ->
            val selected = state.city == city
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
            )) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selected, onClick = { onCityChange(city) })
                    TextButton(onClick = { onCityChange(city) }, modifier = Modifier.weight(1f)) {
                        Text(city.name, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
        HorizontalDivider()
        Text("Display", style = MaterialTheme.typography.titleLarge)
        PreferenceSwitch("Temperature in Fahrenheit", state.fahrenheit, onUnitsChange)
        PreferenceSwitch("Show preparation tips", state.showTips, onTipsChange)
        Text("Preferences survive rotation and reset when the app is restarted. " +
            "Locations use city-centre coordinates.", style = MaterialTheme.typography.bodySmall)
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
        UtilityApp(WeatherUiState(weather = Weather(26.0, 27.0, 2, 0.0, 12.0,
            "2026-10-12T09:00", "Australia/Brisbane")), {}, {}, {}, {})
    }
}
