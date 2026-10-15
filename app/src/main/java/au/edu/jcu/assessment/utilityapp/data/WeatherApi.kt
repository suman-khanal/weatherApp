package au.edu.jcu.assessment.utilityapp.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun forecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,apparent_temperature,weather_code,precipitation,wind_speed_10m",
        @Query("timezone") timezone: String = "auto",
        @Query("temperature_unit") temperatureUnit: String = "celsius",
        @Query("wind_speed_unit") windUnit: String = "kmh",
        @Query("precipitation_unit") precipitationUnit: String = "mm"
    ): ForecastResponse
}

data class ForecastResponse(val current: CurrentWeather?, val timezone: String?)
data class CurrentWeather(
    val time: String?,
    @SerializedName("temperature_2m") val temperature: Double?,
    @SerializedName("apparent_temperature") val feelsLike: Double?,
    @SerializedName("weather_code") val code: Int?,
    val precipitation: Double?,
    @SerializedName("wind_speed_10m") val windSpeed: Double?
)
