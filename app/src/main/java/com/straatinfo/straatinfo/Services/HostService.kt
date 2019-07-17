package com.straatinfo.straatinfo.Services

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Utilities.REQUEST_HOST_BY_NAME
import io.reactivex.Observable
import org.json.JSONObject

object HostService {

    var hostError: String? = null

    fun getHostByName (hostName: String) : Observable<JSONObject> {
        val url = "$REQUEST_HOST_BY_NAME$hostName"
        return Observable.create {
            val getHostByNameRequest = object : JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                if (response.has("_host")) {
                    it.onNext(response.getJSONObject("_host"))
                } else {
                    it.onNext(JSONObject())
                }
            }, Response.ErrorListener { error ->
                it.onNext(JSONObject())
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }


                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(getHostByNameRequest)
        }
    }
}