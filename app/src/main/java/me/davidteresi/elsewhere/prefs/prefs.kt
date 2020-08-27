package prefs

import android.content.Context

import me.davidteresi.elsewhere.R
import me.davidteresi.elsewhere.Place
import me.davidteresi.elsewhere.Weather


/**
 * @return the current place, as saved in SharedPreferences
 */
fun getPlace(context: Context): Place? {
    val prefs = context.getSharedPreferences(
        context.getString(R.string.weather_data_preference),
        Context.MODE_PRIVATE
    ) ?: return null

    return Place.fromSharedPreferences(prefs, context)
}

/**
 * @return the weather, as saved in SharedPreferences
 */
fun getWeather(context: Context): Weather? {
    val prefs = context.getSharedPreferences(
        context.getString(R.string.weather_data_preference),
        Context.MODE_PRIVATE
    ) ?: return null

    return Weather.fromSharedPreferences(prefs, context)
}