package me.davidteresi.elsewhere

import android.content.Context
import android.content.SharedPreferences

import me.davidteresi.elsewhere.prefs.PrefStateManager
import me.davidteresi.elsewhere.prefs.StateManager

class FakeStateManager(val context: Context, var newDay: Boolean) : StateManager {
    override fun getPlaceImageUrl(): String? = test_attrs.imageUrl
    override fun getPlace(): Place? = test_attrs.place
    override fun getWeather(): Weather? = test_attrs.weather
    override fun getWikipediaTitle(): String? = test_attrs.wikipediaTitle
    override fun isNewDay(): Boolean { return newDay }
    override fun removeSavedWikipedia() {}
    override fun saveImageUrl(url: String) {}
    override fun saveToday() {}
    override fun saveWikipediaTitle(title: String) {}
}