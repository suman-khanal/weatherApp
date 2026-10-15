package au.edu.jcu.assessment.utilityapp

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import au.edu.jcu.assessment.utilityapp.data.Weather
import au.edu.jcu.assessment.utilityapp.ui.UtilityScreen
import au.edu.jcu.assessment.utilityapp.ui.UtilityApp
import androidx.test.platform.app.InstrumentationRegistry
import android.graphics.Bitmap
import java.io.File
import au.edu.jcu.assessment.utilityapp.ui.WeatherError
import au.edu.jcu.assessment.utilityapp.ui.WeatherUiState
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class WeatherScreenTest {
    @get:Rule val compose = createComposeRule()
    private val weather = Weather(28.0, 29.0, 61, 0.4, 12.0, "2026-10-15T09:00", "Australia/Brisbane")

    @Test fun knownWeatherShowsUnitsAndRelevantPackingAdvice() {
        compose.setContent {
            MobileTech_UtilityAppTheme {
                UtilityApp(WeatherUiState(weather = weather, fahrenheit = true), {}, {}, {}, {}, {})
            }
        }
        compose.onNodeWithText("82 °F").performScrollTo().assertIsDisplayed()
        compose.runOnIdle {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            val folder = File(instrumentation.targetContext.getExternalFilesDir(null), "screenshots")
            folder.mkdirs()
            File(folder, "weather-fixture.png").outputStream().use { output ->
                instrumentation.uiAutomation.takeScreenshot().compress(Bitmap.CompressFormat.PNG, 100, output)
            }
        }
        compose.onNodeWithText("Pack an umbrella or rain jacket.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Pack water and dress for warm conditions.").performScrollTo().assertIsDisplayed()
    }

    @Test fun hidingTipsRemovesTheChecklist() {
        compose.setContent {
            MobileTech_UtilityAppTheme {
                UtilityScreen(WeatherUiState(weather = weather, showTips = false)) {}
            }
        }
        compose.onNodeWithText("Before you leave").assertDoesNotExist()
    }

    @Test fun offlineRefreshShowsPreviousDataAndRetryWorks() {
        var retries = 0
        compose.setContent {
            MobileTech_UtilityAppTheme {
                UtilityScreen(WeatherUiState(weather = weather, error = WeatherError.CONNECTION)) { retries++ }
            }
        }
        compose.onNodeWithText("Previous conditions").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Try again").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test fun refreshRemainsReachableAtDoubleFontScale() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                MobileTech_UtilityAppTheme {
                    UtilityScreen(WeatherUiState(weather = weather)) {}
                }
            }
        }
        compose.onNodeWithText("Refresh weather").performScrollTo().assertIsDisplayed()
    }
}
