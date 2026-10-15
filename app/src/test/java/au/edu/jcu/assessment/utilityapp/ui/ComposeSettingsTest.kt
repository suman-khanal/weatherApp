package au.edu.jcu.assessment.utilityapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.graphics.Bitmap
import java.io.File
import android.graphics.Canvas
import android.view.View
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import au.edu.jcu.assessment.utilityapp.data.Weather
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Real Compose interaction tests that run without a device or a live API. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h852dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ComposeSettingsTest {
    @get:Rule val compose = createComposeRule()
    private val weather = Weather(28.0, 29.0, 61, 0.4, 12.0, java.time.LocalDateTime.now(java.time.ZoneId.of("Australia/Brisbane")).withNano(0).toString(),
        "Australia/Brisbane")

    @Test fun unitAndTipsSettingsChangeTheMainScreen() {
        var state by mutableStateOf(WeatherUiState(weather = weather))
        var hostView: View? = null
        compose.setContent {
            hostView = LocalView.current
            MobileTech_UtilityAppTheme {
                UtilityApp(state, {}, {},
                    { state = state.copy(fahrenheit = it) },
                    { state = state.copy(showTips = it) }, {})
            }
        }
        compose.onNodeWithText("28 °C").performScrollTo().assertIsDisplayed()
        val output = File("build/outputs/robolectric/dayready-test-fixture.png")
        output.parentFile?.mkdirs()
        compose.runOnIdle {
            val view = checkNotNull(hostView).rootView
            val image = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(image))
            output.outputStream().use { image.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
        compose.onNodeWithTag("settings_tab").performClick()
        compose.onNode(hasText("Use Fahrenheit") and isToggleable()).performScrollTo().performClick()
        compose.onNode(hasText("Preparation tips") and isToggleable()).performScrollTo().performClick()
        compose.onNodeWithTag("today_tab").performClick()
        compose.onNodeWithText("82 °F").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Before you leave").assertDoesNotExist()
    }

    @Test fun navigationTabRestoresFromSavedComposeState() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            MobileTech_UtilityAppTheme {
                UtilityApp(WeatherUiState(weather = weather), {}, {}, {}, {}, {})
            }
        }
        compose.onNodeWithTag("settings_tab").performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("settings_tab").assertIsSelected()
        compose.onNodeWithText("Weather location").assertIsDisplayed()
    }
}
