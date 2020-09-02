/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

 package me.davidteresi.elsewhere

import java.util.concurrent.TimeUnit
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class FailingDispatcher : PrimaryDispatcher() {

    override fun dispatchOwm(request: RecordedRequest?): MockResponse {
        return MockResponse().throttleBody(1024, 5, TimeUnit.SECONDS)
    }

    override fun dispatchWikipedia(request: RecordedRequest?): MockResponse {
        return MockResponse().throttleBody(1024, 5, TimeUnit.SECONDS)
    }

    override fun dispatchWikidata(request: RecordedRequest?): MockResponse {
        return MockResponse().throttleBody(1024, 5, TimeUnit.SECONDS)
    }
}