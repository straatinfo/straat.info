package com.straatinfo.straatinfo.Services

import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.Utilities.LOGIN_URL
import io.reactivex.Observable
import org.json.JSONException
import org.json.JSONObject
import java.lang.NullPointerException


object AuthService {
    var authResponseError: String? = null

    fun loginUser (loginName: String, password: String): Observable<User?> {
        return Observable.create {
            val jsonBody = JSONObject()
            jsonBody.put("loginName", loginName)
            jsonBody.put("password", password)

            val requestBody = jsonBody.toString()

            val loginRequest = object: JsonObjectRequest(Method.POST, LOGIN_URL, null, Response.Listener { response ->
                // do something here with the response
            },  Response.ErrorListener { error ->
                var user: User
                try {
                    val err = JSONObject(String(error.networkResponse.data))

                    authResponseError = err.getString("message") as String
                    user = User(authResponseError)
                    it.onNext(user)
                } catch (e: JSONException) {
                    Log.d("SIGNUP_ERROR", e.localizedMessage)
                    authResponseError = "Internal Server Error"
                    user = User(authResponseError)
                    it.onNext(user)
                } catch (e: VolleyError) {
                    Log.d("SIGNUP_ERROR", e.localizedMessage)
                    authResponseError = "Slow Internet Connection"
                    user = User(authResponseError)
                    it.onNext(user)
                } catch (e: NullPointerException) {
                    Log.d("SIGNUP_ERROR", e.localizedMessage)
                    authResponseError = "Internal Server Error"
                    user = User(authResponseError)
                    it.onNext(user)
                }
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }
            }

            App.prefs.requestQueue.add(loginRequest)
        }
    }
}