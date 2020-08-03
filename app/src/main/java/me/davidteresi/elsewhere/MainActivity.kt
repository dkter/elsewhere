package me.davidteresi.elsewhere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = window.decorView
        view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

        refreshPlace()
    }

    private fun refreshPlace() {
        val placeField = findViewById<TextView>(R.id.place)

        val place = getRandomPlace()
        placeField.text = getString(R.string.place_name, place.name, place.country)
    }

    private fun loadPlaces(): List<Place> {
        var placeList = ArrayList<Place>()
        val jsonFile = resources.openRawResource(R.raw.cities)
        val jsonReader = JsonReader(jsonFile.bufferedReader())
        val gson = GsonBuilder().create()

        jsonReader.beginArray()
        while (jsonReader.hasNext()) {
            val place = gson.fromJson<Place>(jsonReader, Place::class.java)
            placeList.add(place)
        }
        jsonReader.endArray()

        return placeList
    }

    private fun getRandomPlace(): Place {
        val places = loadPlaces()
        val randomPlaceIndex = places.indices.random()
        return places[randomPlaceIndex]
    }
}