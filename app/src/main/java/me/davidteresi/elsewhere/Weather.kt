package me.davidteresi.elsewhere

data class WeatherMain(
    val temp: Double,
    val humidity: Double
)

data class WeatherCondition(
    val main: String,
    val description: String
)

data class WeatherWind(
    val speed: Double
)

data class Weather(
    val main: WeatherMain,
    val weather: List<WeatherCondition>,
    val wind: WeatherWind
)