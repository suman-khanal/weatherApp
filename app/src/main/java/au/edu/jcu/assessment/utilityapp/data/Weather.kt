package au.edu.jcu.assessment.utilityapp.data

/** Preset city centres avoid requesting device location or extra permissions. */
data class City(val name: String, val latitude: Double, val longitude: Double)

val cities = listOf(
    City("Brisbane", -27.4698, 153.0251),
    City("Sydney", -33.8688, 151.2093),
    City("Melbourne", -37.8136, 144.9631),
    City("Perth", -31.9523, 115.8613),
    City("Kathmandu", 27.7172, 85.3240)
)

/** SI values from the API; temperature conversion belongs to presentation only. */
data class Weather(
    val temperature: Double,
    val feelsLike: Double,
    val code: Int,
    val rain: Double,
    val wind: Double,
    val time: String,
    val timezone: String
)
