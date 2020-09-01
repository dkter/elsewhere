package me.davidteresi.elsewhere.prefs

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar
import java.text.SimpleDateFormat

import me.davidteresi.elsewhere.R
import me.davidteresi.elsewhere.Place
import me.davidteresi.elsewhere.Weather

interface StateManager {
    fun getPlaceImageUrl(): String?
    fun getPlace(): Place?
    fun getWeather(): Weather?
    fun getWikipediaTitle(): String?
    fun isNewDay(): Boolean
    fun removeSavedWikipedia()
    fun saveImageUrl(url: String)
    fun saveToday()
    fun saveWikipediaTitle(title: String)
}

class PrefStateManager(val context: Context): StateManager {

    var prefs: SharedPreferences? = context.getSharedPreferences(
        context.getString(R.string.weather_data_preference),
        Context.MODE_PRIVATE
    )

    /**
     * @return the place image URL, as saved in SharedPreferences
     */
    override fun getPlaceImageUrl(): String? {
        return prefs!!.getString(context.getString(R.string.weather_data_image_url), null)
    }                       

    /**
     * @return the current place, as saved in SharedPreferences
     */
    override fun getPlace(): Place? {
        return Place.fromSharedPreferences(prefs!!, context)
    }

    /**
     * @return the weather, as saved in SharedPreferences
     */
    override fun getWeather(): Weather? {
        return Weather.fromSharedPreferences(prefs!!, context)
    }

    /**
     * @return the saved Wikipedia page title, as saved in SharedPreferences
     */
    override fun getWikipediaTitle(): String? {
        return prefs!!.getString(context.getString(R.string.weather_data_wikipedia_page), null)
    }

    /**
     * @return true if the day has changed since it was last saved
     */
    override fun isNewDay(): Boolean {
        val savedDay = prefs!!.getString(context.getString(R.string.weather_data_day), null)

        val today = getToday()
        if (savedDay != null && today == savedDay!!)
            return false
        else
            return true
    }

    /**
     * Remove the saved Wikipedia page.
     * This prevents the app from using the previously saved Wikipedia article
     * if getting a new one fails.
     */
    override fun removeSavedWikipedia() {
        with (prefs!!.edit()) {
            putString(context.getString(R.string.weather_data_wikipedia_page), null)
            putString(context.getString(R.string.weather_data_image_url), null)
            apply()
        }
    }

    /**
     * Save an image URL
     * @param url the URL to save
     */
    override fun saveImageUrl(url: String) {
        with (prefs!!.edit()) {
            putString(context.getString(R.string.weather_data_image_url), url)
            apply()
        }
    }

    /**
     * Save the current date in SharedPreferences
     */
    override fun saveToday() {
        val today = getToday()

        with (prefs!!.edit()) {
            putString(context.getString(R.string.weather_data_day), today)
            apply()
        }
    }

    /**
     * Save a Wikipedia page title in SharedPreferences
     * @param title the page title to save
     */
    override fun saveWikipediaTitle(title: String) {
        with (prefs!!.edit()) {
            putString(context.getString(R.string.weather_data_wikipedia_page), title)
            apply()
        }
    }

    /**
     * @return the current date in yyyy-MM-dd format
     */
    private fun getToday(): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        val today = format.format(Calendar.getInstance().time)
        return today
    }
}