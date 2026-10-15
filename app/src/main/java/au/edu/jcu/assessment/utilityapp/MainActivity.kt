package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.view.WindowCompat
import au.edu.jcu.assessment.utilityapp.ui.theme.ThemeMode
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.assessment.utilityapp.ui.UtilityApp
import au.edu.jcu.assessment.utilityapp.ui.WeatherViewModel
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as DayReadyApplication).container.repository
        val viewModel = ViewModelProvider(
            this, WeatherViewModel.Factory(repository)
        )[WeatherViewModel::class.java]
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val dark = when (state.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !dark
                    isAppearanceLightNavigationBars = !dark
                }
            }
            MobileTech_UtilityAppTheme(mode = state.themeMode) {
                UtilityApp(
                    state = state,
                    onRefresh = viewModel::refresh,
                    onCityChange = viewModel::selectCity,
                    onUnitsChange = viewModel::setFahrenheit,
                    onTipsChange = viewModel::setShowTips,
                    onThemeChange = viewModel::setThemeMode
                )
            }
        }
    }
}
