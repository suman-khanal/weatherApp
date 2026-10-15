package au.edu.jcu.assessment.utilityapp.di

import au.edu.jcu.assessment.utilityapp.data.NetworkWeatherRepository
import au.edu.jcu.assessment.utilityapp.data.WeatherApi
import au.edu.jcu.assessment.utilityapp.data.WeatherRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Manual dependency injection: construct dependencies once and pass them in. */
class AppContainer {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()
    private val api = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(WeatherApi::class.java)
    val repository: WeatherRepository = NetworkWeatherRepository(api)
}
