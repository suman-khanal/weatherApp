package au.edu.jcu.assessment.utilityapp

import au.edu.jcu.assessment.utilityapp.data.Weather
import au.edu.jcu.assessment.utilityapp.ui.*
import org.junit.Assert.*
import org.junit.Test

class PreparationTipsTest {
    private fun weather(feels: Double = 22.0, code: Int = 0, wind: Double = 10.0) =
        Weather(22.0, feels, code, 0.0, wind, "2026-10-12T09:00", "Australia/Brisbane")
    @Test fun freezingPointConversion() {
        assertEquals("32 °F", temperatureLabel(0.0, true))
    }
    @Test fun rainyCodeSuggestsUmbrellaEvenWhenRainAmountIsZero() {
        assertTrue(preparationTips(weather(code = 61)).any { "umbrella" in it })
    }
    @Test fun coldAndWindyConditionsProduceBothTips() {
        val tips = preparationTips(weather(feels = 17.0, wind = 30.0))
        assertTrue(tips.any { "layer" in it })
        assertTrue(tips.any { "wind" in it })
    }
    @Test fun hotThresholdIncludes28Degrees() {
        assertTrue(preparationTips(weather(feels = 28.0)).any { "water" in it })
    }
    @Test fun unknownCodeDoesNotInventConditions() {
        assertEquals("Unknown conditions", conditionLabel(999))
    }
}
