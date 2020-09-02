/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package me.davidteresi.elsewhere

import java.util.concurrent.TimeUnit
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy

class NoWikipediaDispatcher : PrimaryDispatcher() {

    override fun dispatchWikipedia(request: RecordedRequest?): MockResponse {
        return MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START);
    }
}