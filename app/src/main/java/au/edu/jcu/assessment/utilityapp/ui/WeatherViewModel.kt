package au.edu.jcu.assessment.utilityapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import au.edu.jcu.assessment.utilityapp.data.*
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class WeatherUiState(
    val city: City = cities.first(),
    val fahrenheit: Boolean = false,
    val showTips: Boolean = true,
    val loading: Boolean = false,
    val weather: Weather? = null,
    val error: String? = null
)

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(WeatherUiState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null
    init { refresh() }

    fun selectCity(city: City) {
        if (city == state.value.city) return
        mutableState.update { it.copy(city = city, weather = null, error = null) }
        refresh()
    }
    fun setFahrenheit(value: Boolean) {
        mutableState.update { it.copy(fahrenheit = value) }
    }
    fun setShowTips(value: Boolean) {
        mutableState.update { it.copy(showTips = value) }
    }
    fun refresh() {
        request?.cancel()
        val city = state.value.city
        request = viewModelScope.launch {
            mutableState.update { it.copy(loading = true, error = null) }
            try {
                val weather = repository.load(city)
                mutableState.update { it.copy(loading = false, weather = weather) }
            } catch (cancelled: CancellationException) {
                // Preserve coroutine cancellation when switching locations.
                throw cancelled
            } catch (error: Exception) {
                val message = when (error) {
                    is IOException -> "Cannot connect. Check your internet and retry."
                    is HttpException -> "Weather service returned ${error.code()}. Please retry."
                    else -> "Weather data could not be read. Please retry."
                }
                mutableState.update { it.copy(loading = false, error = message) }
            }
        }
    }
    class Factory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(WeatherViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository) as T
        }
    }
}
