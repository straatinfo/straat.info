package com.straatinfo.straatinfo.Services

import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Utilities.TEAM_API
import io.reactivex.Observable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.NullPointerException

object TeamService {
    val TAG = "TEAM_SERVICE"
    var teamServiceError: String? = null

    fun getListOfTeamPerHostWithFilter (hostId: String, isVolunteer: Boolean) : Observable<JSONArray> {
        var queryObject = JSONObject()
        queryObject.put("_host", hostId)
        queryObject.put("isVolunteer", isVolunteer)
        val requestBody = JSONObject()
        requestBody.put("queryObject", queryObject)

        return Observable.create {
            val getTeamListRequest = object: JsonObjectRequest(Method.POST, TEAM_API, null, Response.Listener { success ->
                Log.d(TAG, success.toString())
                val data = success.getJSONArray("data") as JSONArray
                it.onNext(data)
            }, Response.ErrorListener { error ->
                try {
                    val err = JSONObject(String(error.networkResponse.data))

                    teamServiceError = err.getString("message") as String
                    it.onNext(JSONArray())
                } catch (e: JSONException) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Internal Server Error"
                    it.onNext(JSONArray())
                } catch (e: VolleyError) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Slow Internet Connection"
                    it.onNext(JSONArray())
                } catch (e: NullPointerException) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Internal Server Error"
                    it.onNext(JSONArray())
                }
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toString().toByteArray()
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", App.prefs.token)
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(getTeamListRequest)
        }
    }
}