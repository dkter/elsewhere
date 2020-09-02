/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

 package me.davidteresi.elsewhere

import android.content.Context
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

open class PrimaryDispatcher(
    private val context: Context = InstrumentationRegistry.getInstrumentation().context
) : okhttp3.mockwebserver.Dispatcher() {

    private val OWM_ENDPOINT = "/data/2.5/weather"
    private val WP_ENDPOINT = "/w/api.php"
    private val WD_ENDPOINT = "/sparql"

    override fun dispatch(request: RecordedRequest): MockResponse {
        val error404response = MockResponse().setResponseCode(404)
        val path = Uri.parse(request.path).path
            ?: return error404response

        if (path.startsWith(OWM_ENDPOINT)) {
            return dispatchOwm(request)
        }
        else if (path.startsWith(WP_ENDPOINT)) {
            return dispatchWikipedia(request)
        }
        else if (path.startsWith(WD_ENDPOINT)) {
            return dispatchWikidata(request)
        }
        else {
            return error404response
        }
    }

    open fun dispatchOwm(request: RecordedRequest?): MockResponse {
        val gson = Gson()
        val json = gson.toJson(test_attrs.weather)
        return MockResponse()
            .setResponseCode(200)
            .setBody(json)
    }

    open fun dispatchWikipedia(request: RecordedRequest?): MockResponse {
        val gson = Gson()
        val json = gson.toJson(test_attrs.wikipediaGeosearchResult)
        return MockResponse()
            .setResponseCode(200)
            .setBody(json)
    }

    open fun dispatchWikidata(request: RecordedRequest?): MockResponse {
        val gson = Gson()
        val json = gson.toJson(test_attrs.wikidataQueryResult)
        return MockResponse()
            .setResponseCode(200)
            .setBody(json)
    }
}