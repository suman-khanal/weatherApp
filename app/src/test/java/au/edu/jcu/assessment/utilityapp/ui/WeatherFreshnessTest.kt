package au.edu.jcu.assessment.utilityapp.ui

import au.edu.jcu.assessment.utilityapp.data.Weather
import java.time.Instant
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherFreshnessTest {
    private val weather = Weather(22.0, 23.0, 2, 0.0, 10.0, "2026-10-15T09:00", "Australia/Brisbane")

    @Test fun ageUsesCityTimezoneAndThirtyMinuteBoundary() {
        assertFalse(isWeatherStale(weather, Instant.parse("2026-10-14T23:29:59Z")))
        assertTrue(isWeatherStale(weather, Instant.parse("2026-10-14T23:30:00Z")))
    }

    @Test fun fractionalTimezoneOffsetIsHandled() {
        val kathmandu = weather.copy(timezone = "Asia/Kathmandu")
        assertFalse(isWeatherStale(kathmandu, Instant.parse("2026-10-15T03:44:59Z")))
        assertTrue(isWeatherStale(kathmandu, Instant.parse("2026-10-15T03:45:00Z")))
    }
}
