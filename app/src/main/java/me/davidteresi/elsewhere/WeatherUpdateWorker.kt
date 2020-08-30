package me.davidteresi.elsewhere

import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result as WorkerResult
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.RequestFuture
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import org.json.JSONObject

import me.davidteresi.elsewhere.util.StringPostRequest

// apparently I can't name this Result even though I gave
// ListenableWorker.Result a different name in the import. this language is
// honestly incredible.
enum class ResultEnum {
    SUCCESS, FAILURE, RETRY
}

class WeatherUpdateWorker(val appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    override fun doWork(): WorkerResult {
        val isNewDay = (prefs.isNewDay(appContext)
            && !MainActivity.maybeGettingNewPlace)
        val place = if (isNewDay) {
            MainActivity.maybeGettingNewPlace = true
            util.getRandomPlace(appContext)
        }
        else
            prefs.getPlace(appContext)

        val weatherResult = refreshWeather(place)
        if (weatherResult == ResultEnum.FAILURE) {
            MainActivity.maybeGettingNewPlace = false
            return WorkerResult.failure()
        }
        else if (weatherResult == ResultEnum.RETRY) {
            MainActivity.maybeGettingNewPlace = false
            return WorkerResult.retry()
        }

        if (isNewDay) {
            // Get a new image for the place
            val imageResult = loadImage(place!!)
            if (imageResult == ResultEnum.FAILURE) {
                MainActivity.maybeGettingNewPlace = false
                return WorkerResult.failure()
            }
            else if (imageResult == ResultEnum.RETRY) {
                MainActivity.maybeGettingNewPlace = false
                return WorkerResult.retry()
            }

            // Since everything was successful, save the new place
            place.saveSharedPreferences(appContext)
            prefs.saveToday(appContext)
            MainActivity.maybeGettingNewPlace = false
        }
        return WorkerResult.success()
    }

    private fun refreshWeather(place: Place?): ResultEnum {
        Log.d("WeatherUpdateWorker", "Refreshing weather")

        if (place != null) {
            val queue = Volley.newRequestQueue(appContext)
            val future: RequestFuture<JSONObject> = RequestFuture.newFuture()
            val url = "https://api.openweathermap.org/data/2.5/weather?id=${place.id}&appid=${BuildConfig.OWM_KEY}"
            val request = JsonObjectRequest(Request.Method.GET, url, null, future, future)
            queue.add(request)

            try {
                val response = future.get(45, TimeUnit.SECONDS)
                val gson = Gson()
                val weather = gson.fromJson<Weather>(response.toString(), Weather::class.java)
                weather.saveSharedPreferences(appContext)
                return ResultEnum.SUCCESS
            } catch (e: Exception) {
                return ResultEnum.FAILURE
            }
        }
        else {
            return ResultEnum.FAILURE
        }
    }

    private fun loadImage(place: Place): ResultEnum {
        Log.d("WeatherUpdateWorker", "Loading place image")

        val queue = Volley.newRequestQueue(appContext)
        val wikidata_url = "https://query.wikidata.org/sparql?format=json"
        val sparql_query = constructSparqlQuery(place.coord.lat, place.coord.lon)
        val future: RequestFuture<String> = RequestFuture.newFuture()
        val wikidata_request: StringRequest = StringPostRequest(
            wikidata_url,
            mapOf("query" to sparql_query),
            future,
            future
        )
        queue.add(wikidata_request)

        try {
            val response = future.get(45, TimeUnit.SECONDS)
            val gson = Gson()
            val result = gson.fromJson<WikidataQueryResult>(response, WikidataQueryResult::class.java)
            val imageUrl = result?.results?.bindings?.getOrNull(0)?.image?.value
            return cacheImage(forceHttps(imageUrl!!))
        } catch (e: Exception) {
            return ResultEnum.FAILURE
        }
    }

    private fun cacheImage(imageUrl: String): ResultEnum {
        val future = Glide.with(appContext)
            .downloadOnly()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .load(imageUrl)
            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)

        try {
            future.get()
        } catch (e: Exception) {
            return ResultEnum.FAILURE
        }
        // it was successful, so save the image URL
        prefs.saveImageUrl(appContext, imageUrl)
        return ResultEnum.SUCCESS
    }
}
