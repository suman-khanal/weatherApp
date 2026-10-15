package au.edu.jcu.assessment.utilityapp.data

/** UI and ViewModel depend on this interface, allowing a fake in tests. */
interface WeatherRepository {
    suspend fun load(city: City): Weather
}

data class City(val name: String, val latitude: Double, val longitude: Double)
val cities = listOf(
    City("Brisbane", -27.4698, 153.0251),
    City("Sydney", -33.8688, 151.2093),
    City("Melbourne", -37.8136, 144.9631),
    City("Perth", -31.9523, 115.8613),
    City("Kathmandu", 27.7172, 85.3240)
)
data class Weather(
    val temperature: Double, val feelsLike: Double, val code: Int,
    val rain: Double, val wind: Double, val time: String, val timezone: String
)

class NetworkWeatherRepository(private val api: WeatherApi) : WeatherRepository {
    override suspend fun load(city: City): Weather {
        val response = api.forecast(city.latitude, city.longitude)
        val current = requireNotNull(response.current) { "Weather data is unavailable." }
        // Missing fields must produce an error, never fabricated zero-valued weather.
        return Weather(
            requireNotNull(current.temperature), requireNotNull(current.feelsLike),
            requireNotNull(current.code), requireNotNull(current.precipitation),
            requireNotNull(current.windSpeed), requireNotNull(current.time),
            requireNotNull(response.timezone)
        )
    }
}
