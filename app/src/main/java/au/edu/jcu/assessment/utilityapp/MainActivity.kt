package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.assessment.utilityapp.ui.*
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
            MobileTech_UtilityAppTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                UtilityApp(
                    state, viewModel::refresh, viewModel::selectCity,
                    viewModel::setFahrenheit, viewModel::setShowTips
                )
            }
        }
    }
}
