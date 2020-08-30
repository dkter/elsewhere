package me.davidteresi.elsewhere.util

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader

import me.davidteresi.elsewhere.R
import me.davidteresi.elsewhere.Place

class PlaceDataSource(val context: Context) {
    /**
     * @return a random place from the OpenWeatherMap place list
     */
    fun getRandomPlace(): Place {
        val places = loadPlaces()
        val randomPlaceIndex = places.indices.random()
        return places[randomPlaceIndex]
    }

    /**
     * Load the list of OpenWeatherMap places into memory
     * @return the list of places
     */
    private fun loadPlaces(): List<Place> {
        var placeList = ArrayList<Place>()
        val jsonFile = context.resources.openRawResource(R.raw.cities)
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
}
