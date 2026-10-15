package au.edu.jcu.assessment.utilityapp.ui

import android.content.ActivityNotFoundException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.data.Weather
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun UtilityScreen(state: WeatherUiState, onRefresh: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val attributionError = stringResource(R.string.attribution_error)
    // Reassess freshness even if the user leaves the screen open without refreshing.
    val now by produceState(initialValue = Instant.now()) {
        while (true) {
            value = Instant.now()
            delay(60_000)
        }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 840.dp).fillMaxSize()
                .verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            BrandHeader()
            if (state.weather == null) {
                Text(state.city.name, style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.semantics { heading() })
            }
            if (state.loading) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Text(stringResource(R.string.updating_weather), style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                }
            }
            state.error?.let { error -> ErrorCard(error, onRefresh, state.loading) }
            val weather = state.weather
            if (weather == null && state.error == null) {
                OutlinedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionHeading(stringResource(R.string.initial_loading))
                        Text(stringResource(R.string.initial_loading_detail))
                    }
                }
            }
            if (weather != null) {
                if (state.error != null || isWeatherStale(weather, now)) {
                    OutlinedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(stringResource(R.string.previous_weather), style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(R.string.stale_weather))
                        }
                    }
                }
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    if (maxWidth >= 600.dp && state.showTips) {
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            WeatherDetails(state, weather, Modifier.weight(1f))
                            PreparationChecklist(weather, Modifier.weight(1f))
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            WeatherDetails(state, weather)
                            if (state.showTips) PreparationChecklist(weather)
                        }
                    }
                }
            }
            if (weather != null || state.error == null) {
                Button(onClick = onRefresh, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.refresh_weather))
                }
            }
            TextButton(onClick = {
                try {
                    uriHandler.openUri("https://open-meteo.com/")
                } catch (_: ActivityNotFoundException) {
                    scope.launch { snackbar.showSnackbar(attributionError) }
                }
            }) { Text(stringResource(R.string.attribution)) }
        }
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun WeatherDetails(state: WeatherUiState, weather: Weather, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.current_conditions), style = MaterialTheme.typography.labelLarge)
                Text(state.city.name, style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.semantics { heading() })
                Text(temperatureLabel(weather.temperature, state.fahrenheit), style = MaterialTheme.typography.displayLarge)
                Text(conditionLabel(weather.code), style = MaterialTheme.typography.titleLarge)
                Text(stringResource(R.string.feels_like, temperatureLabel(weather.feelsLike, state.fahrenheit)))
            }
        }
        // Stack metrics on compact widths or with large text so values remain readable.
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            if (maxWidth >= 340.dp && androidx.compose.ui.platform.LocalDensity.current.fontScale <= 1.3f) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    WeatherMetric(stringResource(R.string.wind_label), stringResource(R.string.wind_value, weather.wind.roundToInt()), Modifier.weight(1f))
                    WeatherMetric(stringResource(R.string.rain_label), stringResource(R.string.rain_value, decimalLabel(weather.rain)), Modifier.weight(1f))
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    WeatherMetric(stringResource(R.string.wind_label), stringResource(R.string.wind_value, weather.wind.roundToInt()))
                    WeatherMetric(stringResource(R.string.rain_label), stringResource(R.string.rain_value, decimalLabel(weather.rain)))
                }
            }
        }
        Text(stringResource(R.string.rain_explanation), style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(R.string.weather_time, weatherTimeLabel(weather)), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun PreparationChecklist(weather: Weather, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeading(stringResource(R.string.before_you_leave))
        preparationTips(weather).forEachIndexed { index, tip ->
            OutlinedCard(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("%02d".format(index + 1), style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary)
                    Text(tip, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        Text(stringResource(R.string.tips_explanation), style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ErrorCard(error: WeatherError, onRefresh: () -> Unit, loading: Boolean) {
    val message = when (error) {
        WeatherError.CONNECTION -> R.string.connection_error
        WeatherError.SERVICE -> R.string.service_error
        WeatherError.INVALID_DATA -> R.string.invalid_data_error
    }
    Card(Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.error_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(message))
            TextButton(onClick = onRefresh, enabled = !loading) { Text(stringResource(R.string.retry)) }
        }
    }
}

internal fun decimalLabel(value: Double): String = String.format(Locale.getDefault(), "%.1f", value)

internal fun weatherTimeLabel(weather: Weather): String =
    LocalDateTime.parse(weather.time).format(DateTimeFormatter.ofPattern("HH:mm · d MMM", Locale.getDefault())) +
        " · " + weather.timezone

internal fun isWeatherStale(weather: Weather, now: Instant): Boolean {
    val observed = LocalDateTime.parse(weather.time).atZone(ZoneId.of(weather.timezone)).toInstant()
    return java.time.Duration.between(observed, now).toMinutes() >= 30
}
