/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package me.davidteresi.elsewhere

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import androidx.work.WorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.VectorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.TextClock
import android.widget.TextView
import androidx.core.view.updateMargins
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import kotlin.math.roundToInt
import org.json.JSONObject

import me.davidteresi.elsewhere.util.StringPostRequest

/**
 * Make sure the URL is an HTTPS URL (i.e. starts with https://)
 * Should only be used when the server is guaranteed to support HTTPS
 * (and doesn't work without it)
 */
fun forceHttps(url: String): String {
    if (url.startsWith("http:")) {
        return url.substring(0, 4) + "s" + url.substring(4, url.length)
    }
    return url
}

/**
 * Construct a SPARQl query to search Wikidata for images around the
 * specified coordinates.
 */
fun constructSparqlQuery(lat: Float, lon: Float): String {
    return """
        SELECT ?image
        WHERE
        {
          VALUES ?loc { "Point($lon $lat)"^^geo:wktLiteral } .
          SERVICE wikibase:around {
              ?place wdt:P625 ?location .
              bd:serviceParam wikibase:center ?loc .
              bd:serviceParam wikibase:radius "50" .
          }
          ?place wdt:P18 ?image .
          BIND(geof:distance(?loc, ?location) as ?dist)
        }
        ORDER BY ?dist
        LIMIT 1"""
}

class MainActivity : AppCompatActivity() {
    lateinit var place: Place
    var newPlace: Place? = null
    var weather: Weather? = null
    var imageUrl: String? = null

    private val TAG = "MainActivity"

    companion object {
        var maybeGettingNewPlace = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSystemBarStyles()

        val wikipediaChip = findViewById<Chip>(R.id.wikipedia_chip)
        val mapChip = findViewById<Chip>(R.id.map_chip)

        wikipediaChip.setOnClickListener { onWikipediaChipClick() }
        mapChip.setOnClickListener { onMapChipClick() }

        val weatherUpdateWorkRequest = 
            PeriodicWorkRequestBuilder<WeatherUpdateWorker>(15, TimeUnit.MINUTES)
                .setInitialDelay(15, TimeUnit.MINUTES)
                .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "WeatherUpdateWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            weatherUpdateWorkRequest)
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    fun onMapChipClick() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("geo:${place.coord.lat},${place.coord.lon}")
        startActivity(intent)
    }

    fun onWikipediaChipClick() {
        val prefs = getSharedPreferences(
            getString(R.string.weather_data_preference),
            Context.MODE_PRIVATE
        )
        val wikipediaPage = prefs.getString(getString(R.string.weather_data_wikipedia_page), null)
        if (wikipediaPage != null) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://en.wikipedia.org/wiki/$wikipediaPage")
            startActivity(intent)
        }
    }

    fun onSettingsBtnClick(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    /**
     * Called every time the activity is resumed. Update the weather, get
     * a new place if it's the next day, etc.
     */
    private fun refresh() {
        updateTimeFmt()
        getCachedPlaceImage()
        val place = prefs.getPlace(this)
        val weather = prefs.getWeather(this)
        if (weather != null && place != null) {
            showChipGroup()
            this.place = place
            this.weather = weather
            updatePlaceDisplay()
            updateWeatherDisplay()
            updateTimezone()
            if (prefs.isNewDay(this)) {
                maybeGettingNewPlace = true
                this.newPlace = util.getRandomPlace(this)
            }
            getInternetWeather()
        }
        else {
            this.place = util.getRandomPlace(this)
            getInternetWeather()
        }
    }

    /**
     * Styles the status bar and navigation bar.
     * The status bar should always be translucentWhite. The navbar
     * should have the default translucent style, unless it's a gesture
     * bar such as in Android 10 and above.
     */
    private fun setSystemBarStyles() {
        val view = window.decorView
        view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

        window.statusBarColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getColor(R.color.translucentWhite)
        }
        else {
            resources.getColor(R.color.translucentWhite)
        }

        // Remove most bottom padding on CardView if we're on a system with a
        // transparent gesture bar
        val cardView = view.findViewById<CardView>(R.id.cardView)
        if (hasTransparentGestureBar()) {
            val params = (cardView.layoutParams as MarginLayoutParams)
            params.updateMargins(bottom = 2)
        }
    }

    /**
     * @return true if the system navigation bar is a gesture bar
     * that should be fully transparent
     */
    private fun hasTransparentGestureBar(): Boolean {
        val resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        if (resourceId > 0 && resources.getInteger(resourceId) == 2)
            return true
        else
            return false
    }

    /**
     * Show the chip group containing the Wikipedia and Map chips
     */
    private fun showChipGroup() {
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        chipGroup.setVisibility(View.VISIBLE)
    }

    /**
     * Update the displayed place
     */
    private fun updatePlaceDisplay() {
        val placeField = findViewById<TextView>(R.id.place)
        val country = Locale("", place.country)
        placeField.text = getString(
            R.string.place_name,
            place.name,
            country.getDisplayCountry()
        )
    }

    /**
     * Update the displayed weather
     */
    private fun updateWeatherDisplay() {
        val tempField = findViewById<TextView>(R.id.temp)
        val conditionField = findViewById<TextView>(R.id.condition)
        val humidityField = findViewById<TextView>(R.id.humidity)
        val windField = findViewById<TextView>(R.id.wind)

        tempField.text = formatTemp(weather!!.main.temp)
        conditionField.text = weather!!.weather[0].description
        humidityField.text = getString(R.string.humidity, weather!!.main.humidity.roundToInt())
        windField.text = formatWindSpeed(weather!!.wind.speed)
    }

    /**
     * Set the background image
     * @param cacheOnly whether to only load the image from cache
     */
    private fun setImageBackground(cacheOnly: Boolean = false) {
        Glide.with(this)
            .load(imageUrl)
            .onlyRetrieveFromCache(cacheOnly)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.default_bg)
            .into(findViewById<ImageView>(R.id.imageView))
    }

    /**
     * Update the weather from the internet (from OpenWeatherMap).
     */
    private fun getInternetWeather() {
        val place = this.newPlace ?: this.place
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?id=${place.id}&appid=${BuildConfig.OWM_KEY}"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                showChipGroup()

                val gson = Gson()
                weather = gson.fromJson<Weather>(response.toString(), Weather::class.java)
                
                // update everything only if getInternetWeather() succeeds
                if (this.newPlace != null) {
                    this.place = this.newPlace!!
                    this.newPlace = null
                    maybeGettingNewPlace = false
                    removeSavedWikipedia()
                }
                prefs.saveToday(this)
                updateTimezone()
                updatePlaceDisplay()
                updateWeatherDisplay()
                place.saveSharedPreferences(this)
                weather!!.saveSharedPreferences(this)
                getPlaceImage()
                getPlaceWpArticle()
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Error getting weather: $error")
                // nothing happens if this does nothing, so we don't need to
                // handle an error
                // (there's still the edge case where it's not set up yet and
                // getInternetWeather() fails. I've chosen to let it display a
                // placeholder place for now)
            }
        )
        queue.add(request)
    }

    /**
     * Update the timezone of the TextClock based on the data returned by
     * OpenWeatherMap
     */
    private fun updateTimezone() {
        val textClock = findViewById<TextClock>(R.id.text_clock)
        val timezone = TimeZone.getAvailableIDs(weather!!.timezone * 1000)[0]
        textClock.setTimeZone(timezone)
    }

    /**
     * Update the format of the time based on the user-set preference
     */
    private fun updateTimeFmt() {
        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        var timefmt = prefs.getString(getString(R.string.timefmt), "system")
        var textClock = findViewById<TextClock>(R.id.text_clock)

        // If the default time format is null, TextClock will fall back on the
        // other time format. By setting the time format we don't want to null,
        // we can force the other one.
        if (timefmt == "12h") {
            textClock.setFormat12Hour(TextClock.DEFAULT_FORMAT_12_HOUR)
            textClock.setFormat24Hour(null)
        }
        else if (timefmt == "24h") {
            textClock.setFormat12Hour(null)
            textClock.setFormat24Hour(TextClock.DEFAULT_FORMAT_24_HOUR)
        }
        else {
            // Let the system decide
            textClock.setFormat12Hour(TextClock.DEFAULT_FORMAT_12_HOUR)
            textClock.setFormat24Hour(TextClock.DEFAULT_FORMAT_24_HOUR)
        }
    }

    /**
     * Get the place image and set it as the background, but only from cache.
     */
    private fun getCachedPlaceImage() {
        val prefs = getSharedPreferences(
            getString(R.string.weather_data_preference),
            Context.MODE_PRIVATE
        )
        val url = prefs?.getString(getString(R.string.weather_data_image_url), null)
        if (url != null) {
            imageUrl = url
            setImageBackground(cacheOnly = true)
        }
    }

    /**
     * Get the place image and set it as the background
     */
    private fun getPlaceImage() {
        val prefs = getSharedPreferences(
            getString(R.string.weather_data_preference),
            Context.MODE_PRIVATE
        )
        val url = prefs?.getString(getString(R.string.weather_data_image_url), null)
        if (url != null) {
            imageUrl = url
            setImageBackground()
        }
        else
            getImageUrl()
    }

    /**
     * Get the Wikipedia article closest to the coordinates of the selected place.
     * NOTE: this is not necessarily the article that the image is retrieved from
     */
    private fun getPlaceWpArticle() {
        val queue = Volley.newRequestQueue(this)
        val geosearch_url = ("https://en.wikipedia.org/w/api.php"
            + "?action=query"
            + "&list=geosearch"
            + "&gscoord=${place.coord.lat}|${place.coord.lon}"
            + "&gsradius=10000"
            + "&gslimit=1"
            + "&format=json"
        )
        val geosearch_request = JsonObjectRequest(
            Request.Method.GET, geosearch_url, null,
            Response.Listener { response ->
                val gson = Gson()
                val result = gson.fromJson<WikipediaGeosearchResult>(response.toString(), WikipediaGeosearchResult::class.java)
                val title = result?.query?.geosearch?.getOrNull(0)?.title

                if (title != null) {
                    val prefs = getSharedPreferences(
                        getString(R.string.weather_data_preference),
                        Context.MODE_PRIVATE
                    )
                    with (prefs.edit()) {
                        putString(getString(R.string.weather_data_wikipedia_page), title)
                        apply()
                    }
                }
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Error getting Wikipedia article: $error")
            }
        )
        queue.add(geosearch_request)
    }

    /**
     * Get the nearest image of a place from the Wikidata database.
     */
    private fun getImageUrl() {
        val queue = Volley.newRequestQueue(this)
        val wikidata_url = "https://query.wikidata.org/sparql?format=json"
        val sparql_query = constructSparqlQuery(place.coord.lat, place.coord.lon)
        val wikidata_request: StringRequest = StringPostRequest(
            wikidata_url,
            mapOf("query" to sparql_query),
            Response.Listener { response ->
                val gson = Gson()
                val result = gson.fromJson<WikidataQueryResult>(response, WikidataQueryResult::class.java)
                imageUrl = result?.results?.bindings?.getOrNull(0)?.image?.value
                
                if (imageUrl != null) {
                    imageUrl = forceHttps(imageUrl!!)
                    val prefs = getSharedPreferences(
                        getString(R.string.weather_data_preference),
                        Context.MODE_PRIVATE
                    )
                    with (prefs.edit()) {
                        putString(getString(R.string.weather_data_image_url), imageUrl)
                        apply()
                    }
                    setImageBackground()
                }
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Error getting image: $error")
            }
        )
        queue.add(wikidata_request)
    }

    /**
     * Remove the saved Wikipedia page.
     * This prevents the app from using the previously saved Wikipedia article
     * if getting a new one fails.
     */
    private fun removeSavedWikipedia() {
        val prefs = getSharedPreferences(
            getString(R.string.weather_data_preference),
            Context.MODE_PRIVATE
        )
        with (prefs.edit()) {
            putString(getString(R.string.weather_data_wikipedia_page), null)
            putString(getString(R.string.weather_data_image_url), null)
            apply()
        }
    }

    /**
     * Format the temperature for display according to the user's preferences.
     * @param temp the temperature in Kelvin
     * @return the formatted temperature
     */
    private fun formatTemp(temp: Float): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val units = prefs.getString(getString(R.string.units), "metric")
        val friendlyTemp = if (units == "imperial") {
            temp * (9f/5f) - 459.67f
        }
        else {
            temp - 273
        }
        return "${friendlyTemp.roundToInt()}°"
    }

    /**
     * Format the wind speed for display according to the user's preferences.
     * @param windSpeed the wind speed in m/s
     * @return the formatted wind speed
     */
    private fun formatWindSpeed(windSpeed: Float): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val units = prefs.getString(getString(R.string.units), "metric")
        if (units == "imperial") {
            val friendlySpeed = windSpeed * 2.237f
            return getString(R.string.wind_imperial, friendlySpeed.roundToInt())
        }
        else {
            val friendlySpeed = windSpeed * 3600 / 1000
            return getString(R.string.wind_metric, friendlySpeed.roundToInt())
        }
    }
}