package au.edu.jcu.assessment.utilityapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.data.City
import au.edu.jcu.assessment.utilityapp.data.cities
import au.edu.jcu.assessment.utilityapp.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    state: WeatherUiState,
    onCityChange: (City) -> Unit,
    onUnitsChange: (Boolean) -> Unit,
    onTipsChange: (Boolean) -> Unit,
    onThemeChange: (ThemeMode) -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 600.dp).fillMaxSize()
                .verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.semantics { heading() })
            Text(stringResource(R.string.settings_subtitle), color = MaterialTheme.colorScheme.onSurfaceVariant)
            SectionHeading(stringResource(R.string.location_title))
            Text(stringResource(R.string.location_description), style = MaterialTheme.typography.bodyMedium)
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.selectableGroup()) {
                    cities.forEach { city ->
                        ChoiceRow(city.name, city == state.city) { onCityChange(city) }
                    }
                }
            }
            SectionHeading(stringResource(R.string.display_title))
            Card(Modifier.fillMaxWidth()) {
                Column {
                    PreferenceSwitch(stringResource(R.string.fahrenheit_title),
                        stringResource(R.string.fahrenheit_description), state.fahrenheit, onUnitsChange)
                    PreferenceSwitch(stringResource(R.string.tips_title),
                        stringResource(R.string.tips_description), state.showTips, onTipsChange)
                }
            }
            SectionHeading(stringResource(R.string.appearance_title))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.selectableGroup()) {
                    ThemeMode.entries.forEach { mode ->
                        val label = stringResource(when (mode) {
                            ThemeMode.SYSTEM -> R.string.theme_system
                            ThemeMode.LIGHT -> R.string.theme_light
                            ThemeMode.DARK -> R.string.theme_dark
                        })
                        ChoiceRow(label, mode == state.themeMode) { onThemeChange(mode) }
                    }
                }
            }
            Text(stringResource(R.string.settings_lifecycle), style = MaterialTheme.typography.bodySmall)
            SectionHeading(stringResource(R.string.about_title))
            Text(stringResource(R.string.about_description), style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ChoiceRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().selectable(selected, role = Role.RadioButton, onClick = onClick).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun PreferenceSwitch(label: String, description: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().toggleable(checked, role = Role.Switch, onValueChange = onChange).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = null)
    }
}
