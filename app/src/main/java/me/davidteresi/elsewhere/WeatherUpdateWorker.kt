package me.davidteresi.elsewhere

import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result as WorkerResult
import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import org.json.JSONObject

import me.davidteresi.elsewhere.util.PlaceDataSource
import me.davidteresi.elsewhere.prefs.PrefStateManager

// apparently I can't name this Result even though I gave
// ListenableWorker.Result a different name in the import. this language is
// honestly incredible.
enum class ResultEnum {
    SUCCESS, FAILURE, RETRY
}

class WeatherUpdateWorker(val appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    private val application = appContext.getApplicationContext()
    private val stateManager = (application as ElsewhereApp).stateManager
    private val placeDataSource = (application as ElsewhereApp).placeDataSource
    private val httpClient = okhttp3.OkHttpClient()

    override fun doWork(): WorkerResult {
        val isNewDay = (stateManager.isNewDay()
            && !MainActivity.maybeGettingNewPlace)
        val place = if (isNewDay) {
            MainActivity.maybeGettingNewPlace = true
            placeDataSource.getRandomPlace()
        }
        else
            stateManager.getPlace()

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
            stateManager.saveToday()
            MainActivity.maybeGettingNewPlace = false
        }
        return WorkerResult.success()
    }

    private fun refreshWeather(place: Place?): ResultEnum {
        Log.d("WeatherUpdateWorker", "Refreshing weather")

        if (place != null) {
            val url = okhttp3.HttpUrl.Builder()
                .scheme("https")
                .host((application as ElsewhereApp).owmHost)
                .addPathSegment("data")
                .addPathSegment("2.5")
                .addPathSegment("weather")
                .addQueryParameter("id", place.id.toString())
                .addQueryParameter("appid", BuildConfig.OWM_KEY)
                .build()
            val request = okhttp3.Request.Builder().url(url).build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful)
                    return ResultEnum.FAILURE

                val gson = Gson()
                val weather = gson.fromJson<Weather>(
                    response.body!!.string(),
                    Weather::class.java)
                weather.saveSharedPreferences(appContext)
                return ResultEnum.SUCCESS
            }
        }
        return ResultEnum.FAILURE
    }

    private fun loadImage(place: Place): ResultEnum {
        Log.d("WeatherUpdateWorker", "Loading place image")

        val url = okhttp3.HttpUrl.Builder()
            .scheme("https")
            .host((application as ElsewhereApp).wikidataHost)
            .addPathSegment("sparql")
            .addQueryParameter("format", "json")
            .build()
        val sparqlQuery = constructSparqlQuery(place.coord.lat, place.coord.lon)
        val params = okhttp3.FormBody.Builder()
            .add("query", sparqlQuery)
            .build()
        val request = okhttp3.Request.Builder()
            .url(url)
            .post(params)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful)
                return ResultEnum.FAILURE

            val gson = Gson()
            val result = gson.fromJson<WikidataQueryResult>(
                response.body!!.string(),
                WikidataQueryResult::class.java)
            val imageUrl = result?.results?.bindings?.getOrNull(0)?.image?.value
            return cacheImage(forceHttps(imageUrl!!))
        }
        return ResultEnum.FAILURE
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
        stateManager.saveImageUrl(imageUrl)
        return ResultEnum.SUCCESS
    }
}
