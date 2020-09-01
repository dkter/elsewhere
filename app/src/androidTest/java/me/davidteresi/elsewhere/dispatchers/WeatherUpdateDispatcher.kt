package me.davidteresi.elsewhere

import java.util.concurrent.TimeUnit
import com.google.gson.Gson
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class WeatherUpdateDispatcher : PrimaryDispatcher() {
    override fun dispatchOwm(request: RecordedRequest?): MockResponse {
        val gson = Gson()
        val json = gson.toJson(test_attrs.updated.weather)
        return MockResponse()
            .setResponseCode(200)
            .setBody(json)
    }

    override fun dispatchWikipedia(request: RecordedRequest?): MockResponse {
        val gson = Gson()
        val json = gson.toJson(test_attrs.updated.wikipediaGeosearchResult)
        return MockResponse()
            .setResponseCode(200)
            .setBody(json)
    }

    override fun dispatchWikidata(request: RecordedRequest?): MockResponse {
        val gson = Gson()
        val json = gson.toJson(test_attrs.updated.wikidataQueryResult)
        return MockResponse()
            .setResponseCode(200)
            .setBody(json)
    }
}