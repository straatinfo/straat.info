package com.straatinfo.straatinfo.Services

import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.Utilities.REPORT_NEAR
import com.straatinfo.straatinfo.Utilities.SEND_REPORT_V2
import io.reactivex.Observable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.NullPointerException

object ReportService {

    var reportError: String? = null
    var TAG = "REPORT_API"

    fun getNearReport (userId: String, long: Double, lat: Double, radius: Double) : Observable<JSONArray> {
        reportError = null
        return Observable.create { it ->
            val url = REPORT_NEAR + "/" + long.toString() + "/" + lat.toString() + "/" + radius.toString() + "/?" + "language=nl" + "&" + "_reporter=$userId"

            val getReportRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
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
                    reportError = "Internal Server Error"
                    it.onNext(JSONArray())
                } catch (e: VolleyError) {
                    Log.d(TAG, e.localizedMessage)
                    reportError = "Slow Internet Connection"
                    it.onNext(JSONArray())
                } catch (e: NullPointerException) {
                    Log.d(TAG, e.localizedMessage)
                    reportError = "Internal Server Error"
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

            App.prefs.requestQueue.add(getReportRequest)
        }
    }

    fun sendReportV2 (report: JSONObject): Observable<Boolean> {
        reportError = null
        return Observable.create {
            val requestBody = report.toString()
            val sendReportRequest = object: JsonObjectRequest(Method.POST, SEND_REPORT_V2, null, Response.Listener { response ->
                Log.d(TAG, response.toString())
                it.onNext(true)
            }, Response.ErrorListener { error ->
                try {
                    val err = JSONObject(String(error.networkResponse.data))

                    reportError = err.getString("message") as String
                    it.onNext(false)
                } catch (e: JSONException) {
                    Log.d(TAG, e.localizedMessage)
                    reportError = "Internal Server Error"
                    it.onNext(false)
                } catch (e: VolleyError) {
                    Log.d(TAG, e.localizedMessage)
                    reportError = "Slow Internet Connection"
                    it.onNext(false)
                } catch (e: NullPointerException) {
                    Log.d(TAG, e.localizedMessage)
                    reportError = "Internal Server Error"
                    it.onNext(false)
                }
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", App.prefs.token)
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(sendReportRequest)
        }
    }
}