package me.davidteresi.elsewhere

data class WeatherMain(
    val temp: Double
)

data class Weather(
    val main: WeatherMain
)