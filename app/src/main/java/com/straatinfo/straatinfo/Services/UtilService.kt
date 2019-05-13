package com.straatinfo.straatinfo.Services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Controllers.HomeActivity
import com.straatinfo.straatinfo.Controllers.MainActivity
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Utilities.GEOCODE
import com.straatinfo.straatinfo.Utilities.GOOGLE_API_KEY
import com.straatinfo.straatinfo.Utilities.POST_CODE_API
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.StringBuilder
import kotlin.random.Random

object UtilService {
    var utilResponseError: String? = null
    val TAG = "UTILS"
    var utilsError : String? = null

    fun postcode (postCode: String, houseNumber: String, completion: (error: String?, userData: JSONObject?) -> Unit) {
        utilResponseError = null
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
                completion(utilResponseError, JSONObject())
            } catch (e: JSONException) {
                Log.d("POST_CODE", e.localizedMessage)
                utilResponseError = "Internal Server Error"
                completion(utilResponseError, JSONObject())
            } catch (e: VolleyError) {
                Log.d("POST_CODE", e.localizedMessage)
                utilResponseError = "Slow Internet Connection"
                completion(utilResponseError, JSONObject())
            } catch (e: NullPointerException) {
                Log.d("POST_CODE", e.localizedMessage)
                utilResponseError = "Internal Server Error"
                completion(utilResponseError, JSONObject())
            } catch (e: Exception) {
                Log.d("POST_CODE", e.localizedMessage)
                utilResponseError = "Internal Server Error"
                completion(utilResponseError, JSONObject())
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

    fun setLocationPermission (activity: Activity, LOCATION_RECORD_CODE: Int) {
        val permission = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
        val internetPerm = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.INTERNET)

        if (internetPerm != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSION", "Permission denied")
        }

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION_PERMISSION", "Permission denied")
        }


        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("Permission to access Internet is required")
            builder.setTitle(("Internet Permission"))
            builder.setPositiveButton("OK") { dialog, which ->
                Log.d("PERMISSION", "clicked")
                this.makeRequest(activity, LOCATION_RECORD_CODE)
            }

            val dialog = builder.create()
            dialog.show()
        } else {
            this.makeRequest(activity, LOCATION_RECORD_CODE)
        }
    }

    fun makeRequest (activity: Activity, LOCATION_RECORD_CODE: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.CAMERA
            ), LOCATION_RECORD_CODE)
    }

    fun geocode (long: Double, lat: Double) : Observable<JSONObject> {
        utilsError = null
        val url = GEOCODE + "?latlng=${lat.toString()},${long.toString()}&key=" + GOOGLE_API_KEY

        return Observable.create {
            val geoCodeRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                Log.d("GEO_CODE_RESPONSE", response.toString())

                it.onNext(response)
            }, Response.ErrorListener { error ->
                try {
//                    val err = JSONObject(String(error.networkResponse.data))
//
//                    reportError = err.getString("message") as String
                    Log.d(TAG, error.toString())
                    it.onNext(JSONObject())
                } catch (e: JSONException) {
                    Log.d(TAG, e.localizedMessage)
                    utilsError = "Internal Server Error"
                    it.onNext(JSONObject())
                } catch (e: VolleyError) {
                    Log.d(TAG, e.localizedMessage)
                    utilsError = "Slow Internet Connection"
                    it.onNext(JSONObject())
                } catch (e: NullPointerException) {
                    Log.d(TAG, e.localizedMessage)
                    utilsError = "Internal Server Error"
                    it.onNext(JSONObject())
                }
            }) {

            }

            App.prefs.requestQueue.add(geoCodeRequest)
        }
    }

    fun showDefaultAlert (context: Context, title: String, message: String) : android.app.AlertDialog {
        val builder = android.app.AlertDialog.Builder(context)
        builder
            .setTitle(title)
            .setMessage(message)

        return builder.create()
    }

    fun randomStringGenerator (strLength: Int): String {
        val alphaNum: String = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val random = Random
        val sb = StringBuilder()
        for (i in 0..(strLength - 1)) {
            sb.append(alphaNum[random.nextInt(alphaNum.length - 1)])
        }

        return sb.toString()
    }
}