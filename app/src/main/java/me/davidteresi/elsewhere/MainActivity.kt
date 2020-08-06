package me.davidteresi.elsewhere

import androidx.appcompat.app.AppCompatActivity
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
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    lateinit var place: Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = window.decorView
        view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

        refreshPlace()
        refreshWeather()
    }

    private fun refreshPlace() {
        val placeField = findViewById<TextView>(R.id.place)

        place = getRandomPlace()
        placeField.text = getString(R.string.place_name, place.name, place.country)
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

    private fun getRandomPlace(): Place {
        val places = loadPlaces()
        val randomPlaceIndex = places.indices.random()
        return places[randomPlaceIndex]
    }

    private fun updateWeatherDisplay(weather: Weather) {
        val tempField = findViewById<TextView>(R.id.temp)
        val conditionField = findViewById<TextView>(R.id.condition)
        val humidityField = findViewById<TextView>(R.id.humidity)
        val windField = findViewById<TextView>(R.id.wind)

        tempField.text = formatTemp(weather.main.temp)
        conditionField.text = weather.weather[0].description
        humidityField.text = getString(R.string.humidity, weather.main.humidity.roundToInt())
        windField.text = getString(R.string.wind, weather.wind.speed.roundToInt())
    }

    private fun refreshWeather() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?id=${place.id}&appid=${BuildConfig.OWM_KEY}"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val gson = Gson()
                val weather = gson.fromJson<Weather>(response.toString(), Weather::class.java)
                updateWeatherDisplay(weather)
            },
            Response.ErrorListener { error ->
                // TODO: handle request failure
            }
        )
        queue.add(request)
    }

    private fun formatTemp(temp: Double): String {
        val celsius = temp - 273
        return "${celsius.roundToInt()}°"
    }
}