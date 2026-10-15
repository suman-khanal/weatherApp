package au.edu.jcu.assessment.utilityapp.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import au.edu.jcu.assessment.utilityapp.data.City
import au.edu.jcu.assessment.utilityapp.data.Weather
import au.edu.jcu.assessment.utilityapp.data.WeatherRepository
import au.edu.jcu.assessment.utilityapp.data.cities
import au.edu.jcu.assessment.utilityapp.ui.theme.ThemeMode
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

enum class WeatherError { CONNECTION, SERVICE, INVALID_DATA }

data class WeatherUiState(
    val city: City = cities.first(),
    val fahrenheit: Boolean = false,
    val showTips: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val loading: Boolean = false,
    val weather: Weather? = null,
    val error: WeatherError? = null
)

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val savedState: SavedStateHandle = SavedStateHandle()
) : ViewModel() {
    private val mutableState = MutableStateFlow(
        WeatherUiState(
            city = cities.find { it.name == savedState.get<String>(CITY) } ?: cities.first(),
            fahrenheit = savedState[FAHRENHEIT] ?: false,
            showTips = savedState[SHOW_TIPS] ?: true,
            themeMode = ThemeMode.entries.find { it.name == savedState.get<String>(THEME) }
                ?: ThemeMode.SYSTEM
        )
    )
    val state = mutableState.asStateFlow()
    private var request: Job? = null
    private var requestVersion = 0

    init { refresh() }

    fun selectCity(city: City) {
        require(city in cities)
        if (city == state.value.city) return
        savedState[CITY] = city.name
        // Never leave the previous city's conditions beneath the new city's name.
        mutableState.update { it.copy(city = city, weather = null, error = null) }
        refresh()
    }

    fun setFahrenheit(value: Boolean) {
        savedState[FAHRENHEIT] = value
        mutableState.update { it.copy(fahrenheit = value) }
    }

    fun setShowTips(value: Boolean) {
        savedState[SHOW_TIPS] = value
        mutableState.update { it.copy(showTips = value) }
    }

    fun setThemeMode(value: ThemeMode) {
        savedState[THEME] = value.name
        mutableState.update { it.copy(themeMode = value) }
    }

    fun refresh() {
        request?.cancel()
        val version = ++requestVersion
        val city = state.value.city
        mutableState.update { it.copy(loading = true, error = null) }
        request = viewModelScope.launch {
            try {
                val weather = repository.load(city)
                // Also guard against implementations that do not cooperate with cancellation.
                if (version == requestVersion) {
                    mutableState.update { it.copy(loading = false, weather = weather) }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                val kind = when (error) {
                    is IOException -> WeatherError.CONNECTION
                    is HttpException -> WeatherError.SERVICE
                    else -> WeatherError.INVALID_DATA
                }
                if (version == requestVersion) {
                    mutableState.update { it.copy(loading = false, error = kind) }
                }
            }
        }
    }

    class Factory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            require(modelClass.isAssignableFrom(WeatherViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository, extras.createSavedStateHandle()) as T
        }
    }

    companion object {
        internal const val CITY = "city"
        internal const val FAHRENHEIT = "fahrenheit"
        internal const val SHOW_TIPS = "showTips"
        internal const val THEME = "theme"
    }
}
