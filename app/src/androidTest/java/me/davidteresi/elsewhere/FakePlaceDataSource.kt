package me.davidteresi.elsewhere

import android.content.Context

import me.davidteresi.elsewhere.util.PlaceDataSource

class FakePlaceDataSource(override val context: Context) : PlaceDataSource(context) {
    override fun getRandomPlace(): Place {
        return test_attrs.place
    }
}