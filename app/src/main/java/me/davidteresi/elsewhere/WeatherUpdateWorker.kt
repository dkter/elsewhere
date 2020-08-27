package me.davidteresi.elsewhere

import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import org.json.JSONObject

class WeatherUpdateWorker(val appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val place = prefs.getPlace(appContext)

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
                return Result.success()
            } catch (e: Exception) {
                return Result.failure()
            }
        }
        else {
            return Result.failure()
        }
    }
}
