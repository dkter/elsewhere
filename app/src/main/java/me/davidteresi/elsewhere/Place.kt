package me.davidteresi.elsewhere

import android.content.Context
import android.content.SharedPreferences

data class Coord(
    val lat: Float,
    val lon: Float
)

data class Place(
    val id: Int,
    val name: String,
    val state: String,
    val country: String,
    val coord: Coord
) {
    companion object {
        fun fromSharedPreferences(prefs: SharedPreferences, context: Context): Place? {
            val id = prefs.getInt(context.getString(R.string.weather_data_place_id), 0)
            val name = prefs.getString(context.getString(R.string.weather_data_place_name), null)
            val state = prefs.getString(context.getString(R.string.weather_data_place_state), null)
            val country = prefs.getString(context.getString(R.string.weather_data_place_country), null)
            val coord = Coord(
                prefs.getFloat(context.getString(R.string.weather_data_place_coord_lat), 0.0f),
                prefs.getFloat(context.getString(R.string.weather_data_place_coord_lon), 0.0f)
            )
            if (name == null || state == null || country == null)
                return null
            else
                return Place(id, name!!, state!!, country!!, coord)
        }
    }

    fun saveSharedPreferences(context: Context) {
        val prefs = context.getSharedPreferences(
            context.getString(R.string.weather_data_preference),
            Context.MODE_PRIVATE
        )
        with (prefs.edit()) {
            putInt(context.getString(R.string.weather_data_place_id), id)
            putString(context.getString(R.string.weather_data_place_name), name)
            putString(context.getString(R.string.weather_data_place_state), state)
            putString(context.getString(R.string.weather_data_place_country), country)
            putFloat(context.getString(R.string.weather_data_place_coord_lat), coord.lat)
            putFloat(context.getString(R.string.weather_data_place_coord_lon), coord.lon)

            apply()
        }
    }
}