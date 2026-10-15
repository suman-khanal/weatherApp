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
    51, 53, 55, 56, 57 -> "Drizzle"
    61, 63, 65, 66, 67 -> "Rain"
    71, 73, 75, 77 -> "Snow"
    80, 81, 82 -> "Rain showers"
    85, 86 -> "Snow showers"
    95, 96, 99 -> "Thunderstorms"
    else -> "Unknown conditions"
}

/** Simple packing suggestions based on current conditions, not a day forecast. */
fun preparationTips(weather: Weather): List<String> = buildList {
    if (weather.rain > 0 || weather.code in listOf(51,53,55,56,57,61,63,65,66,67,80,81,82,95,96,99))
        add("Pack an umbrella or rain jacket.")
    if (weather.feelsLike < 18) add("Take an extra layer for cooler conditions.")
    if (weather.feelsLike >= 28) add("Pack water and dress for warm conditions.")
    if (weather.wind >= 30) add("Expect wind; secure loose belongings.")
    if (weather.code in listOf(71,73,75,77,85,86)) add("Dress warmly for snowy conditions.")
    if (isEmpty()) add("Comfortable conditions: pack your usual essentials.")
}
