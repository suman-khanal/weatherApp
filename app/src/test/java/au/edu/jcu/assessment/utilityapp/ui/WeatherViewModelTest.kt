package au.edu.jcu.assessment.utilityapp.ui

import androidx.lifecycle.SavedStateHandle
import au.edu.jcu.assessment.utilityapp.data.City
import au.edu.jcu.assessment.utilityapp.data.Weather
import au.edu.jcu.assessment.utilityapp.data.WeatherRepository
import au.edu.jcu.assessment.utilityapp.data.cities
import au.edu.jcu.assessment.utilityapp.ui.theme.ThemeMode
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val weather = Weather(22.0, 23.0, 2, 0.0, 10.0, "2026-10-15T09:00", "Australia/Brisbane")

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private class FakeRepository(var response: suspend (City) -> Weather) : WeatherRepository {
        val calls = mutableListOf<City>()
        override suspend fun load(city: City): Weather {
            calls.add(city)
            return response(city)
        }
    }

    @Test fun initialFetchShowsLoadingThenWeather() = runTest(dispatcher) {
        val repository = FakeRepository { weather }
        val model = WeatherViewModel(repository)
        assertTrue(model.state.value.loading)
        advanceUntilIdle()
        assertFalse(model.state.value.loading)
        assertEquals(weather, model.state.value.weather)
        assertEquals(listOf(cities.first()), repository.calls)
    }

    @Test fun failedRefreshKeepsPreviousWeatherAndCanRecover() = runTest(dispatcher) {
        val repository = FakeRepository { weather }
        val model = WeatherViewModel(repository)
        advanceUntilIdle()
        repository.response = { throw IOException("offline") }
        model.refresh()
        advanceUntilIdle()
        assertEquals(weather, model.state.value.weather)
        assertEquals(WeatherError.CONNECTION, model.state.value.error)
        assertFalse(model.state.value.loading)
        repository.response = { weather.copy(temperature = 25.0) }
        model.refresh()
        advanceUntilIdle()
        assertEquals(25.0, model.state.value.weather!!.temperature, 0.0)
        assertNull(model.state.value.error)
    }

    @Test fun changingCityClearsOldConditionsImmediately() = runTest(dispatcher) {
        val repository = FakeRepository { weather }
        val model = WeatherViewModel(repository)
        advanceUntilIdle()
        repository.response = { throw IOException() }
        model.selectCity(cities[1])
        assertNull(model.state.value.weather)
        assertEquals(cities[1], model.state.value.city)
        advanceUntilIdle()
        assertNull(model.state.value.weather)
        assertEquals(cities[1], repository.calls.last())
    }

    @Test fun lateOldCityResponseCannotReplaceNewCityWeather() = runTest(dispatcher) {
        val oldCityResult = CompletableDeferred<Weather>()
        val newWeather = weather.copy(temperature = 30.0)
        val repository = FakeRepository { city ->
            if (city == cities.first()) withContext(NonCancellable) { oldCityResult.await() }
            else newWeather
        }
        val model = WeatherViewModel(repository)
        runCurrent()
        model.selectCity(cities[1])
        runCurrent()
        oldCityResult.complete(weather)
        advanceUntilIdle()
        assertEquals(cities[1], model.state.value.city)
        assertEquals(newWeather, model.state.value.weather)
        assertNull(model.state.value.error)
    }

    @Test fun settingsRestoreFromHandleWithoutConvertingStoredWeather() = runTest(dispatcher) {
        val handle = SavedStateHandle()
        val repository = FakeRepository { weather }
        val first = WeatherViewModel(repository, handle)
        first.selectCity(cities.last())
        first.setFahrenheit(true)
        first.setShowTips(false)
        first.setThemeMode(ThemeMode.DARK)
        advanceUntilIdle()
        val restored = WeatherViewModel(repository, SavedStateHandle(mapOf(
            WeatherViewModel.CITY to handle.get<String>(WeatherViewModel.CITY),
            WeatherViewModel.FAHRENHEIT to handle.get<Boolean>(WeatherViewModel.FAHRENHEIT),
            WeatherViewModel.SHOW_TIPS to handle.get<Boolean>(WeatherViewModel.SHOW_TIPS),
            WeatherViewModel.THEME to handle.get<String>(WeatherViewModel.THEME)
        )))
        advanceUntilIdle()
        assertEquals(cities.last(), restored.state.value.city)
        assertTrue(restored.state.value.fahrenheit)
        assertFalse(restored.state.value.showTips)
        assertEquals(ThemeMode.DARK, restored.state.value.themeMode)
        assertEquals(22.0, restored.state.value.weather!!.temperature, 0.0)
    }

    @Test fun reselectingCurrentCityAndChangingDisplayDoNotRefetch() = runTest(dispatcher) {
        val repository = FakeRepository { weather }
        val model = WeatherViewModel(repository)
        advanceUntilIdle()
        model.selectCity(cities.first())
        model.setFahrenheit(true)
        model.setShowTips(false)
        model.setThemeMode(ThemeMode.LIGHT)
        advanceUntilIdle()
        assertEquals(1, repository.calls.size)
    }

    @Test fun invalidResponseHasDistinctErrorAndNoFabricatedWeather() = runTest(dispatcher) {
        val model = WeatherViewModel(FakeRepository { throw IllegalArgumentException("invalid") })
        advanceUntilIdle()
        assertEquals(WeatherError.INVALID_DATA, model.state.value.error)
        assertNull(model.state.value.weather)
        assertFalse(model.state.value.loading)
    }
}
