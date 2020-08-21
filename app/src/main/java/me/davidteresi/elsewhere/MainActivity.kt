/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package me.davidteresi.elsewhere

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
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
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    lateinit var place: Place
    var newPlace: Place? = null
    var weather: Weather? = null
    var imageUrl: String? = null

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSystemBarStyles()

        val wikipediaChip = findViewById<Chip>(R.id.wikipedia_chip)
        val mapChip = findViewById<Chip>(R.id.map_chip)

        wikipediaChip.setOnClickListener { onWikipediaChipClick() }
        mapChip.setOnClickListener { onMapChipClick() }
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

    fun launchSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun refresh() {
        updateTimeFmt()
        val place = getLocalPlace()
        val weather = getLocalWeather()
        if (weather != null && place != null) {
            showChipGroup()
            this.place = place
            this.weather = weather
            updatePlaceDisplay()
            updateWeatherDisplay()
            if (isNewDay())
                this.newPlace = getRandomPlace()
            getInternetWeather()
        }
        else {
            this.place = getRandomPlace()
            getInternetWeather()
        }
    }

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

        // Remove bottom padding on CardView if we're on a system with a
        // transparent gesture bar
        val cardView = view.findViewById<CardView>(R.id.cardView)
        if (hasTransparentGestureBar()) {
            val params = (cardView.layoutParams as MarginLayoutParams)
            params.updateMargins(bottom = 0)
        }
    }

    private fun hasTransparentGestureBar(): Boolean {
        val resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        if (resourceId > 0 && resources.getInteger(resourceId) == 2)
            return true
        else
            return false
    }

    private fun showChipGroup() {
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        chipGroup.setVisibility(View.VISIBLE)
    }

    private fun getToday(): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        val today = format.format(Calendar.getInstance().time)
        return today
    }

    private fun isNewDay(): Boolean {
        val prefs = getSharedPreferences(
            getString(R.string.weather_data_preference),
            Context.MODE_PRIVATE
        )

        val savedDay = prefs?.getString(getString(R.string.weather_data_day), null)

        val today = getToday()
        if (savedDay != null && today == savedDay!!)
            return false
        else
            return true
    }

    private fun saveToday() {
        val prefs = getSharedPreferences(
            getString(R.string.weather_data_preference),
            Context.MODE_PRIVATE
        )
        val today = getToday()

        with (prefs.edit()) {
            putString(getString(R.string.weather_data_day), today)
            apply()
        }
    }

    private fun updatePlaceDisplay() {
        val placeField = findViewById<TextView>(R.id.place)
        val country = Locale("", place.country)
        placeField.text = getString(
            R.string.place_name,
            place.name,
            country.getDisplayCountry()
        )
    }

    private fun loadPlaces(): List<Place> {
        var placeList = ArrayList<Place>()
        val jsonFile = resources.openRawResource(R.raw.cities)
        val jsonReader = JsonReader(jsonFile.bufferedReader())
        val gson = GsonBuilder().create()

        jsonReader.beginArray()
        while (jsonReader.hasNext()) {
            val currentPlace = gson.fromJson<Place>(jsonReader, Place::class.java)
            placeList.add(currentPlace)
        }
        jsonReader.endArray()

        return placeList
    }

    private fun getLocalPlace(): Place? {
        val prefs = getSharedPreferences(
            getString(R.string.weather_data_preference),
            Context.MODE_PRIVATE
        ) ?: return null

        return Place.fromSharedPreferences(prefs, this)
    }

    private fun getRandomPlace(): Place {
        val places = loadPlaces()
        val randomPlaceIndex = places.indices.random()
        return places[randomPlaceIndex]
    }

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

    private fun getLocalWeather(): Weather? {
        val prefs = getSharedPreferences(
            getString(R.string.weather_data_preference),
            Context.MODE_PRIVATE
        ) ?: return null

        return Weather.fromSharedPreferences(prefs, this)
    }

    private fun setImageBackground() {
        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.default_bg)
            .into(findViewById<ImageView>(R.id.imageView))
    }

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
                    removeSavedWikipedia()
                }
                saveToday()
                updateTimezone()
                updatePlaceDisplay()
                updateWeatherDisplay()
                place.saveSharedPreferences(this)
                weather!!.saveSharedPreferences(this)
                getPlaceImage()
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "error getting weather? hmm $error")
                // nothing happens if this does nothing, so we don't need to handle an error
                // (there's still the edge case where it's not set up yet and getInternetWeather() fails)
            }
        )
        queue.add(request)
    }

    private fun updateTimezone() {
        val textClock = findViewById<TextClock>(R.id.text_clock)
        val timezone = TimeZone.getAvailableIDs(weather!!.timezone * 1000)[0]
        textClock.setTimeZone(timezone)
    }

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
            textClock.setFormat12Hour(TextClock.DEFAULT_FORMAT_12_HOUR)
            textClock.setFormat24Hour(TextClock.DEFAULT_FORMAT_24_HOUR)
        }
    }

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
            getWikipediaPlaces()
    }

    private fun getWikipediaPlaces() {
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
                    getWikipediaImage(title)
                }
            },
            Response.ErrorListener { error ->
            }
        )
        queue.add(geosearch_request)
    }

    private fun getWikipediaImage(title: String?) {
        val queue = Volley.newRequestQueue(this)
        val pageimage_url = ("https://en.wikipedia.org/w/api.php"
            + "?action=query"
            + "&prop=pageimages"
            + "&titles=$title"
            + "&piprop=original"
            + "&format=json"
            + "&formatversion=2"
        )
        val pageimage_request = JsonObjectRequest(
            Request.Method.GET, pageimage_url, null,
            Response.Listener { response ->
                val gson = Gson()
                val result = gson.fromJson<WikipediaPageImageResult>(response.toString(), WikipediaPageImageResult::class.java)
                imageUrl = result?.query?.pages?.getOrNull(0)?.original?.source
                
                if (imageUrl != null) {
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
            }
        )
        queue.add(pageimage_request)
    }

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