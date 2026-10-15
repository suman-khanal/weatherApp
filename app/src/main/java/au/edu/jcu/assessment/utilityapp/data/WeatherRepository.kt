package au.edu.jcu.assessment.utilityapp.data

import java.time.LocalDateTime
import java.time.ZoneId

interface WeatherRepository {
    suspend fun load(city: City): Weather
}

class NetworkWeatherRepository(private val api: WeatherApi) : WeatherRepository {
    override suspend fun load(city: City): Weather {
        val response = api.forecast(city.latitude, city.longitude)
        val current = requireNotNull(response.current) { "Current conditions are missing." }
        // Reject incomplete or invalid responses instead of showing fabricated zeroes.
        val weather = Weather(
            temperature = requireNotNull(current.temperature),
            feelsLike = requireNotNull(current.feelsLike),
            code = requireNotNull(current.code),
            rain = requireNotNull(current.precipitation),
            wind = requireNotNull(current.windSpeed),
            time = requireNotNull(current.time),
            timezone = requireNotNull(response.timezone)
        )
        require(weather.temperature.isFinite() && weather.temperature in -100.0..70.0)
        require(weather.feelsLike.isFinite() && weather.feelsLike in -120.0..90.0)
        require(weather.rain.isFinite() && weather.rain >= 0)
        require(weather.wind.isFinite() && weather.wind >= 0)
        LocalDateTime.parse(weather.time)
        ZoneId.of(weather.timezone)
        return weather
    }
}
