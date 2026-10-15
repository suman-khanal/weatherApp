package au.edu.jcu.assessment.utilityapp

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.ViewModelProvider
import au.edu.jcu.assessment.utilityapp.ui.WeatherViewModel
import au.edu.jcu.assessment.utilityapp.ui.theme.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsLifecycleTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun settingsAndSelectedTabSurviveActivityRecreation() {
        compose.onNodeWithTag("settings_tab").performClick()
        compose.onNodeWithText("Kathmandu").performScrollTo().performClick()
        compose.onNode(hasText("Use Fahrenheit") and isToggleable()).performScrollTo().performClick()
        compose.onNodeWithText("Dark").performScrollTo().performClick()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("settings_tab").assertIsSelected()
        compose.onNode(hasText("Use Fahrenheit") and isToggleable()).performScrollTo().assertIsOn()
        compose.onNodeWithText("Kathmandu").performScrollTo().assertIsSelected()
        compose.activityRule.scenario.onActivity { activity ->
            val state = ViewModelProvider(activity)[WeatherViewModel::class.java].state.value
            assertEquals("Kathmandu", state.city.name)
            assertTrue(state.fahrenheit)
            assertEquals(ThemeMode.DARK, state.themeMode)
        }
        compose.onNodeWithTag("today_tab").performClick().assertIsSelected()
    }
}
