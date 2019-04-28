package com.straatinfo.straatinfo.Services

import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Utilities.POST_CODE_API
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.lang.NullPointerException

object UtilService {
    var utilResponseError: String? = null

    fun postcode (postCode: String, houseNumber: String, completion: (error: String?, userData: JSONObject?) -> Unit) {
        var url = "$POST_CODE_API/?postcode=${postCode}"
        if (houseNumber != null && houseNumber != "") {
            url += "&number=$houseNumber"
        }

        val getPostCodeData = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            completion(null, response)
        }, Response.ErrorListener { error ->
            try {
                val err = JSONObject(String(error.networkResponse.data))

                utilResponseError = err.getString("message") as String
                completion(utilResponseError, null)
            } catch (e: JSONException) {
                Log.d("POST_CODE", e.localizedMessage)
                utilResponseError = "Internal Server Error"
                completion(utilResponseError, null)
            } catch (e: VolleyError) {
                Log.d("POST_CODE", e.localizedMessage)
                utilResponseError = "Slow Internet Connection"
                completion(utilResponseError, null)
            } catch (e: NullPointerException) {
                Log.d("POST_CODE", e.localizedMessage)
                utilResponseError = "Internal Server Error"
                completion(utilResponseError, null)
            } catch (e: Exception) {
                Log.d("POST_CODE", e.localizedMessage)
            }
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                if (App.prefs.AUTH_TOKEN != null) {
                    headers.put("Authorization", "Bearer " + App.prefs.AUTH_TOKEN!!)
                }
                return headers
            }
        }
        App.prefs.requestQueue.add(getPostCodeData)
    }
}