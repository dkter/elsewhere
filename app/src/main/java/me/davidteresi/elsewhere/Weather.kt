package me.davidteresi.elsewhere

import android.content.Context
import android.content.SharedPreferences

data class WeatherMain(
    val temp: Float,
    val humidity: Float
)

data class WeatherCondition(
    val main: String,
    val description: String
)

data class WeatherWind(
    val speed: Float
)

data class Weather(
    val main: WeatherMain,
    val weather: List<WeatherCondition>,
    val wind: WeatherWind
) {
    companion object {
        fun fromSharedPreferences(prefs: SharedPreferences, context: Context): Weather? {
            val temp = prefs.getFloat(context.getString(R.string.weather_data_temp), 0.0f)
            val humidity = prefs.getFloat(context.getString(R.string.weather_data_humidity), 0.0f)
            val cond_main = prefs.getString(context.getString(R.string.weather_data_condition_main), null)
            val cond_desc = prefs.getString(context.getString(R.string.weather_data_condition_description), null)
            val wind_speed = prefs.getFloat(context.getString(R.string.weather_data_wind_speed), 0.0f)
            if (cond_main == null || cond_desc == null)
                return null
            else
                return Weather(
                    WeatherMain(temp, humidity),
                    listOf(WeatherCondition(cond_main!!, cond_desc!!)),
                    WeatherWind(wind_speed)
                )
        }
    }

    fun saveSharedPreferences(context: Context) {
        val prefs = context.getSharedPreferences(
            context.getString(R.string.weather_data_preference),
            Context.MODE_PRIVATE
        )
        with (prefs.edit()) {
            putFloat(context.getString(R.string.weather_data_temp), main.temp)
            putFloat(context.getString(R.string.weather_data_humidity), main.humidity)
            putString(context.getString(R.string.weather_data_condition_main), weather[0].main)
            putString(context.getString(R.string.weather_data_condition_description), weather[0].description)
            putFloat(context.getString(R.string.weather_data_wind_speed), wind.speed)

            apply()
        }
    }
}