package me.davidteresi.elsewhere

import android.content.Context

import me.davidteresi.elsewhere.util.PlaceDataSource

class FakePlaceDataSource(override val context: Context, var place: Place) : PlaceDataSource(context) {
    override fun getRandomPlace(): Place {
        return place
    }
}