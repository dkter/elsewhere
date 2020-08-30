package prefs

import android.content.Context
import java.util.Calendar
import java.text.SimpleDateFormat

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

/**
 * Save the current image URL
 */
fun saveImageUrl(context: Context, url: String) {
    val prefs = context.getSharedPreferences(
        context.getString(R.string.weather_data_preference),
        Context.MODE_PRIVATE
    )
    with (prefs.edit()) {
        putString(context.getString(R.string.weather_data_image_url), url)
        apply()
    }
}

/**
 * @return true if the day has changed since it was last saved
 */
fun isNewDay(context: Context): Boolean {
    val prefs = context.getSharedPreferences(
        context.getString(R.string.weather_data_preference),
        Context.MODE_PRIVATE
    )

    val savedDay = prefs?.getString(context.getString(R.string.weather_data_day), null)

    val today = getToday()
    if (savedDay != null && today == savedDay!!)
        return false
    else
        return true
}

/**
 * Save the current date in SharedPreferences
 */
fun saveToday(context: Context) {
    val prefs = context.getSharedPreferences(
        context.getString(R.string.weather_data_preference),
        Context.MODE_PRIVATE
    )
    val today = getToday()

    with (prefs.edit()) {
        putString(context.getString(R.string.weather_data_day), today)
        apply()
    }
}

/**
 * Get the current date
 * @return the current date in yyyy-MM-dd format
 */
private fun getToday(): String {
    val format = SimpleDateFormat("yyyy-MM-dd")
    val today = format.format(Calendar.getInstance().time)
    return today
}