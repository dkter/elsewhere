/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

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
    val wind: WeatherWind,
    val timezone: Int
) {
    companion object {
        fun fromSharedPreferences(prefs: SharedPreferences, context: Context): Weather? {
            val temp = prefs.getFloat(context.getString(R.string.weather_data_temp), 0.0f)
            val humidity = prefs.getFloat(context.getString(R.string.weather_data_humidity), 0.0f)
            val cond_main = prefs.getString(context.getString(R.string.weather_data_condition_main), null)
            val cond_desc = prefs.getString(context.getString(R.string.weather_data_condition_description), null)
            val wind_speed = prefs.getFloat(context.getString(R.string.weather_data_wind_speed), 0.0f)
            val timezone = prefs.getInt(context.getString(R.string.weather_data_timezone), -1)
            if (cond_main == null || cond_desc == null || timezone == -1)
                return null
            else
                return Weather(
                    WeatherMain(temp, humidity),
                    listOf(WeatherCondition(cond_main!!, cond_desc!!)),
                    WeatherWind(wind_speed),
                    timezone
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
            putInt(context.getString(R.string.weather_data_timezone), timezone)

            apply()
        }
    }
}