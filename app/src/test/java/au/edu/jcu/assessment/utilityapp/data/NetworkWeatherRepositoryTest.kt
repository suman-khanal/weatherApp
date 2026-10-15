package au.edu.jcu.assessment.utilityapp.data

import java.net.Proxy
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkWeatherRepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var repository: NetworkWeatherRepository
    private val json = """{"timezone":"Australia/Brisbane","current":{"time":"2026-10-15T09:00","temperature_2m":22.5,"apparent_temperature":23.0,"weather_code":61,"precipitation":0.4,"wind_speed_10m":12.0}}"""

    @Before fun setUp() {
        server = MockWebServer()
        server.start()
        val api = Retrofit.Builder().baseUrl(server.url("/"))
            .client(OkHttpClient.Builder().proxy(Proxy.NO_PROXY).build())
            .addConverterFactory(GsonConverterFactory.create()).build().create(WeatherApi::class.java)
        repository = NetworkWeatherRepository(api)
    }

    @After fun tearDown() { server.shutdown() }

    @Test fun sendsCoordinatesAndExplicitUnitsAndMapsJson() = runTest {
        server.enqueue(MockResponse().setBody(json))
        val result = repository.load(cities.first())
        assertEquals(22.5, result.temperature, 0.0)
        assertEquals(0.4, result.rain, 0.0)
        assertEquals(61, result.code)
        val url = server.takeRequest().requestUrl!!
        assertEquals("/v1/forecast", url.encodedPath)
        assertEquals("-27.4698", url.queryParameter("latitude"))
        assertEquals("153.0251", url.queryParameter("longitude"))
        assertEquals("auto", url.queryParameter("timezone"))
        assertEquals("celsius", url.queryParameter("temperature_unit"))
        assertEquals("kmh", url.queryParameter("wind_speed_unit"))
        assertEquals("mm", url.queryParameter("precipitation_unit"))
        assertTrue(url.queryParameter("current")!!.contains("weather_code"))
    }

    @Test fun missingCurrentIsRejected() = runTest {
        server.enqueue(MockResponse().setBody("""{"timezone":"Australia/Brisbane"}"""))
        assertNotNull(runCatching { repository.load(cities.first()) }.exceptionOrNull())
    }

    @Test fun missingTemperatureIsRejectedRatherThanShownAsZero() = runTest {
        server.enqueue(MockResponse().setBody(json.replace("\"temperature_2m\":22.5,", "")))
        assertTrue(runCatching { repository.load(cities.first()) }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test fun invalidMeasurementsAndTimezoneAreRejected() = runTest {
        listOf(
            json.replace("\"wind_speed_10m\":12.0", "\"wind_speed_10m\":-1.0"),
            json.replace("\"temperature_2m\":22.5", "\"temperature_2m\":999.0"),
            json.replace("Australia/Brisbane", "invalid/zone"),
            json.replace("2026-10-15T09:00", "not-a-time")
        ).forEach { invalid ->
            server.enqueue(MockResponse().setBody(invalid))
            assertNotNull(runCatching { repository.load(cities.first()) }.exceptionOrNull())
        }
    }

    @Test fun serviceFailureIsNotReplacedWithFakeWeather() = runTest {
        server.enqueue(MockResponse().setResponseCode(503))
        val error = runCatching { repository.load(cities.first()) }.exceptionOrNull()
        assertTrue(error is HttpException)
        assertEquals(503, (error as HttpException).code())
    }
}
