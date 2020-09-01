package me.davidteresi.elsewhere

import android.app.Application

import me.davidteresi.elsewhere.util.PlaceDataSource
import me.davidteresi.elsewhere.prefs.PrefStateManager
import me.davidteresi.elsewhere.prefs.StateManager

open class ElsewhereApp: Application() {
    open lateinit var stateManager: StateManager
    open lateinit var placeDataSource: PlaceDataSource

    open val owmHost = "https://api.openweathermap.org"
    open val wikipediaHost = "https://en.wikipedia.org"
    open val wikidataHost = "https://query.wikidata.org"

    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    open fun initialize() {
        stateManager = PrefStateManager(this)
        placeDataSource = PlaceDataSource(this)
    }
}