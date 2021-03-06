/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package me.davidteresi.elsewhere

import android.content.Context
import android.content.SharedPreferences

import me.davidteresi.elsewhere.prefs.PrefStateManager
import me.davidteresi.elsewhere.prefs.StateManager

class FakeStateManager(val context: Context, var newDay: Boolean) : StateManager {
    var wpTitle: String? = test_attrs.wikipediaTitle
    override fun getPlaceImageUrl(): String? = test_attrs.imageUrl
    override fun getPlace(): Place? = test_attrs.place
    override fun getWeather(): Weather? = test_attrs.weather
    override fun getWikipediaTitle(): String? { return wpTitle }
    override fun isNewDay(): Boolean { return newDay }
    override fun removeSavedWikipedia() {}
    override fun saveImageUrl(url: String) {}
    override fun saveToday() {}
    override fun saveWikipediaTitle(title: String) {}
}