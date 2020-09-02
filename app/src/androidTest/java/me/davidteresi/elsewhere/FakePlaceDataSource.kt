/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package me.davidteresi.elsewhere

import android.content.Context

import me.davidteresi.elsewhere.util.PlaceDataSource

class FakePlaceDataSource(override val context: Context, var place: Place) : PlaceDataSource(context) {
    override fun getRandomPlace(): Place {
        return place
    }
}