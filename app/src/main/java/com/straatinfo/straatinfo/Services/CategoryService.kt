package com.straatinfo.straatinfo.Services

import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Utilities.GET_MAIN_CAT
import io.reactivex.Observable
import org.json.JSONArray
import org.json.JSONException
import java.lang.NullPointerException

object CategoryService {
    val TAG = "CATEGORY_SERVICE"
    var categoryError: String? = null
    fun getMainCategories (hostId: String, language: String): Observable<JSONArray> {
        categoryError = null
        return Observable.create { it ->
            val url = GET_MAIN_CAT + hostId + "/" + "?language=" + language

            val getMainCatRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                Log.d(TAG, response.toString())
                val data = response.getJSONArray("data")
                it.onNext(data)
            }, Response.ErrorListener { error ->
                try {
//                    val err = JSONObject(String(error.networkResponse.data))
//
//                    reportError = err.getString("message") as String
                    Log.d(TAG, error.toString())
                    it.onNext(JSONArray())
                } catch (e: JSONException) {
                    Log.d(TAG, e.localizedMessage)
                    categoryError = "Internal Server Error"
                    it.onNext(JSONArray())
                } catch (e: VolleyError) {
                    Log.d(TAG, e.localizedMessage)
                    categoryError = "Slow Internet Connection"
                    it.onNext(JSONArray())
                } catch (e: NullPointerException) {
                    Log.d(TAG, e.localizedMessage)
                    categoryError = "Internal Server Error"
                    it.onNext(JSONArray())
                }
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", App.prefs.token)
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(getMainCatRequest)
        }
    }
}