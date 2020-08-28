package util

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader

import me.davidteresi.elsewhere.R
import me.davidteresi.elsewhere.Place

/**
 * Load the list of OpenWeatherMap places into memory
 * @return the list of places
 */
fun loadPlaces(context: Context): List<Place> {
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

/**
 * @return a random place from the OpenWeatherMap place list
 */
fun getRandomPlace(context: Context): Place {
    val places = loadPlaces(context)
    val randomPlaceIndex = places.indices.random()
    return places[randomPlaceIndex]
}
