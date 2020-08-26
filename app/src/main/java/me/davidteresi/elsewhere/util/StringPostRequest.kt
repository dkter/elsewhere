package me.davidteresi.elsewhere.util

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class StringPostRequest(
    url: String,
    val params_: Map<String, String>,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener?
) : StringRequest(Request.Method.POST, url, listener, errorListener) {

    override fun getParams(): Map<String, String> {
        return params_
    }
}