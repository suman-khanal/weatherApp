package au.edu.jcu.assessment.utilityapp.ui

import au.edu.jcu.assessment.utilityapp.data.Weather
import kotlin.math.roundToInt

fun temperatureLabel(celsius: Double, fahrenheit: Boolean): String =
    if (fahrenheit) "${(celsius * 9 / 5 + 32).roundToInt()} °F"
    else "${celsius.roundToInt()} °C"

fun conditionLabel(code: Int): String = when (code) {
    0 -> "Clear sky"
    1, 2 -> "Partly cloudy"
    3 -> "Overcast"
    45, 48 -> "Fog"
    51, 53, 55 -> "Drizzle"
    56, 57 -> "Freezing drizzle"
    61, 63, 65 -> "Rain"
    66, 67 -> "Freezing rain"
    71, 73, 75, 77 -> "Snow"
    80, 81, 82 -> "Rain showers"
    85, 86 -> "Snow showers"
    95, 96, 99 -> "Thunderstorms"
    else -> "Unknown conditions"
}

private val wetCodes = setOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99)
private val snowyCodes = setOf(71, 73, 75, 77, 85, 86)

/** Packing suggestions, deliberately separate from official safety advice or forecasts. */
fun preparationTips(weather: Weather): List<String> = buildList {
    if (weather.code in setOf(95, 96, 99)) {
        add("Thunderstorms reported. Check local weather warnings before heading out.")
    }
    if (weather.rain > 0 || weather.code in wetCodes) {
        add("Pack an umbrella or rain jacket.")
    }
    if (weather.code in setOf(56, 57, 66, 67)) {
        add("Freezing precipitation reported. Choose shoes with good grip.")
    }
    if (weather.feelsLike < 18) add("Take an extra layer for cooler conditions.")
    if (weather.feelsLike >= 28) add("Pack water and dress for warm conditions.")
    if (weather.wind >= 30) add("Expect wind; secure loose belongings.")
    if (weather.code in snowyCodes) add("Dress warmly for snowy conditions.")
    if (isEmpty()) add("Pack your usual essentials. No extra items suggested by these rules.")
}
