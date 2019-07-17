package com.straatinfo.straatinfo.Services

import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Utilities.GET_PUBLIC_REPORT_LIST
import com.straatinfo.straatinfo.Utilities.REPORT_API
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

    fun getNearReport (userId: String, long: Double, lat: Double, radius: Double, reportId: String?) : Observable<JSONArray> {
        reportError = null
        return Observable.create { it ->
            var url = REPORT_NEAR + "/" + long.toString() + "/" + lat.toString() + "/" + radius.toString() + "/?" + "language=nl" + "&" + "_reporter=$userId"
            if (reportId != null && reportId != "") url += "&reportId=$reportId"

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

    fun sendReportV2 (report: JSONObject): Observable<JSONObject> {
        reportError = null
        return Observable.create {
            val requestBody = report.toString()
            val sendReportRequest = object: JsonObjectRequest(Method.POST, SEND_REPORT_V2, null, Response.Listener { response ->
                Log.d(TAG, response.toString())
                val json = JSONObject()
                json.put("success", true)
                val data = response.getJSONObject("data")
                if (data.has("_id")) json.put("reportId", data.getString("_id"))
                it.onNext(json)
            }, Response.ErrorListener { error ->
                val json = JSONObject()
                json.put("success", false)
                try {
                    val err = JSONObject(String(error.networkResponse.data))

                    reportError = err.getString("message") as String
                    it.onNext(json)
                } catch (e: JSONException) {
                    Log.d(TAG, e.localizedMessage)
                    reportError = "Internal Server Error"
                    it.onNext(json)
                } catch (e: VolleyError) {
                    Log.d(TAG, e.localizedMessage)
                    reportError = "Slow Internet Connection"
                    it.onNext(json)
                } catch (e: NullPointerException) {
                    Log.d(TAG, e.localizedMessage)
                    reportError = "Internal Server Error"
                    it.onNext(json)
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

    fun getReportList (reportType: String, reporterId: String, language: String): Observable<JSONArray> {
        reportError = null
        val reportTypeA = "5a7888bb04866e4742f74955"
        val reportTypeB = "5a7888bb04866e4742f74956"

        val type = if (reportType == "B") reportTypeB else reportTypeA

        val url = "$GET_PUBLIC_REPORT_LIST/?_reporter=$reporterId&_reportType=$type&language=$language"

        return Observable.create {
            val getPublicReport = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
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
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(getPublicReport)
        }

    }

    fun getReportDetails (reportId: String, language: String): Observable<JSONObject> {
        val url = "$REPORT_API/$reportId?language=$language"
        reportError = null
        return Observable.create {
            val reportDetailsRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                Log.d(TAG, response.toString())
                val data = response.getJSONObject("data")
                it.onNext(data)
            }, Response.ErrorListener { error ->
                try {
//                    val err = JSONObject(String(error.networkResponse.data))
//
//                    reportError = err.getString("message") as String
                    Log.d(TAG, error.toString())
                    it.onNext(JSONObject())
                } catch (e: JSONException) {
                    Log.d(TAG, e.localizedMessage)
                    reportError = "Internal Server Error"
                    it.onNext(JSONObject())
                } catch (e: VolleyError) {
                    Log.d(TAG, e.localizedMessage)
                    reportError = "Slow Internet Connection"
                    it.onNext(JSONObject())
                } catch (e: NullPointerException) {
                    Log.d(TAG, e.localizedMessage)
                    reportError = "Internal Server Error"
                    it.onNext(JSONObject())
                }
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

            App.prefs.requestQueue.add(reportDetailsRequest)
        }
    }
}