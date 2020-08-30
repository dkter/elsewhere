package me.davidteresi.elsewhere

import android.app.Application

import me.davidteresi.elsewhere.util.PlaceDataSource
import me.davidteresi.elsewhere.prefs.PrefStateManager

open class ElsewhereApp: Application() {
    open lateinit var stateManager: PrefStateManager
    open lateinit var placeDataSource: PlaceDataSource

    open val owmHost = "api.openweathermap.org"
    open val wikipediaHost = "en.wikipedia.org"
    open val wikidataHost = "query.wikidata.org"

    override fun onCreate() {
        super.onCreate()
        stateManager = PrefStateManager(this)
        placeDataSource = PlaceDataSource(this)
    }
}