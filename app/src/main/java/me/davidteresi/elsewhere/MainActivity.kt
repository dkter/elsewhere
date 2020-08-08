package me.davidteresi.elsewhere

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    lateinit var place: Place
    var newPlace: Place? = null
    var weather: Weather? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = window.decorView
        view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

        refresh()
    }

    private fun refresh() {
        val place = getLocalPlace()
        val weather = getLocalWeather()
        if (weather != null && place != null) {
            this.place = place
            this.weather = weather
            updatePlaceDisplay()
            updateWeatherDisplay()
        }
        else {
            this.place = getRandomPlace()
            getInternetWeather()
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
        windField.text = getString(R.string.wind, weather!!.wind.speed.roundToInt())
    }

    private fun getLocalWeather(): Weather? {
        val prefs = getSharedPreferences(
            getString(R.string.weather_data_preference),
            Context.MODE_PRIVATE
        ) ?: return null

        return Weather.fromSharedPreferences(prefs, this)
    }

    private fun getInternetWeather() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?id=${place.id}&appid=${BuildConfig.OWM_KEY}"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val gson = Gson()
                weather = gson.fromJson<Weather>(response.toString(), Weather::class.java)
                updatePlaceDisplay()
                updateWeatherDisplay()
                weather!!.saveSharedPreferences(this)
            },
            Response.ErrorListener { error ->
                // nothing happens if this does nothing, so we don't need to handle an error
                // (there's still the edge case where it's not set up yet and getInternetWeather() fails)
            }
        )
        queue.add(request)
    }

    private fun formatTemp(temp: Float): String {
        val celsius = temp - 273
        return "${celsius.roundToInt()}°"
    }
}