/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package me.davidteresi.elsewhere

class ElsewhereTestApp: ElsewhereApp() {
    override val owmHost = "http://127.0.0.1:8080"
    override val wikipediaHost = "http://127.0.0.1:8080"
    override val wikidataHost = "http://127.0.0.1:8080"

    override fun initialize() {
        stateManager = FakeStateManager(this, false)
        placeDataSource = FakePlaceDataSource(this, test_attrs.place)
    }
}