package com.straatinfo.straatinfo.Services

import android.provider.Settings
import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.Utilities.*
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.json.JSONException
import org.json.JSONObject
import java.lang.Error
import java.lang.Exception
import java.lang.NullPointerException


object AuthService {
    var authResponseError: String? = null

    private fun processUserData (it: ObservableEmitter<Boolean>, userData: JSONObject) {
        Log.d("LOGIN_RESULT", userData.toString())
        App.prefs.userData = userData.toString()
        Log.d("TOKEN", userData.getString("token"))
        App.prefs.token = userData.getString("token")
        App.prefs.isLoggedIn = true
        it.onNext(true)
    }

    fun loginUserRx (loginName: String, password: String): Observable<Boolean> {
        authResponseError = null
        return Observable.create {
            val jsonBody = JSONObject()
            jsonBody.put("loginName", loginName)
            jsonBody.put("password", password)

            val requestBody = jsonBody.toString()

            val loginRequest = object: JsonObjectRequest(Method.POST, LOGIN_URL, null, Response.Listener { response ->
                // do something here with the response
                Log.d("LOGIN_USER_RX", response.toString())
                val data = response.getJSONObject("data")
                this.processUserData(it, data)
            },  Response.ErrorListener { error ->
                var user: User
                try {
                    val err = JSONObject(String(error.networkResponse.data))

                    authResponseError = err.getString("message") as String
                    it.onNext(false)
                } catch (e: JSONException) {
                    Log.d("SIGNUP_ERROR", e.localizedMessage)
                    authResponseError = "Internal Server Error"
                    it.onNext(false)
                } catch (e: VolleyError) {
                    Log.d("SIGNUP_ERROR", e.localizedMessage)
                    authResponseError = "Slow Internet Connection"
                    it.onNext(false)
                } catch (e: NullPointerException) {
                    Log.d("SIGNUP_ERROR", e.localizedMessage)
                    authResponseError = "Internal Server Error"
                    it.onNext(false)
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

    fun login (loginName: String, password: String, completion: (error: String?, userData: JSONObject?) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("loginName", loginName)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object: JsonObjectRequest(Method.POST, LOGIN_URL, null, Response.Listener { response ->
            // do something here with the response
            completion(null, response)
        }, Response.ErrorListener { error ->
            try {
                val err = JSONObject(String(error.networkResponse.data))

                authResponseError = err.getString("message") as String
                completion(authResponseError, null)
            } catch (e: JSONException) {
                Log.d("SIGNUP_ERROR", e.localizedMessage)
                authResponseError = "Internal Server Error"
                completion(authResponseError, null)
            } catch (e: VolleyError) {
                Log.d("SIGNUP_ERROR", e.localizedMessage)
                authResponseError = "Slow Internet Connection"
                completion(authResponseError, null)
            } catch (e: NullPointerException) {
                Log.d("SIGNUP_ERROR", e.localizedMessage)
                authResponseError = "Internal Server Error"
                completion(authResponseError, null)
            }
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(loginRequest)
    }

    fun register (registrationInput: JSONObject, completion: (error: String?, userData: JSONObject?) -> Unit) {
        val requestBody = registrationInput.toString()

        val loginRequest = object: JsonObjectRequest(Method.POST, SIGNUP_V3, null, Response.Listener { response ->
            // do something here with the response
            completion(null, response)
        }, Response.ErrorListener { error ->
            try {
                val err = JSONObject(String(error.networkResponse.data))

                authResponseError = err.getString("message") as String
                completion(authResponseError, null)
            } catch (e: JSONException) {
                Log.d("REGISTRATION_ERROR", e.localizedMessage)
                authResponseError = "Internal Server Error"
                completion(authResponseError, null)
            } catch (e: VolleyError) {
                Log.d("REGISTRATION_ERROR", e.localizedMessage)
                authResponseError = "Slow Internet Connection"
                completion(authResponseError, null)
            } catch (e: NullPointerException) {
                Log.d("REGISTRATION_ERROR", e.localizedMessage)
                authResponseError = "Internal Server Error"
                completion(authResponseError, null)
            }
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(loginRequest)

    }

    fun registerRx (registrationInput: JSONObject): Observable<Boolean> {
        authResponseError = null
        val requestBody = registrationInput.toString()

        return Observable.create {
            val loginRequest = object: JsonObjectRequest(Method.POST, SIGNUP_V2, null, Response.Listener { response ->
                // do something here with the response
                if (response.has("data")) {
                    val data = response.getJSONObject("data")
                    this.processUserData(it, data)
                } else {
                    it.onNext(false)
                }
            }, Response.ErrorListener { error ->
                try {
                    val err = JSONObject(String(error.networkResponse.data))

                    authResponseError = err.getString("message") as String
                    it.onNext(false)
                } catch (e: JSONException) {
                    Log.d("REGISTRATION_ERROR", e.localizedMessage)
                    authResponseError = "Internal Server Error"
                    it.onNext(false)
                } catch (e: VolleyError) {
                    Log.d("REGISTRATION_ERROR", e.localizedMessage)
                    authResponseError = "Slow Internet Connection"
                    it.onNext(false)
                } catch (e: NullPointerException) {
                    Log.d("REGISTRATION_ERROR", e.localizedMessage)
                    authResponseError = "Internal Server Error"
                    it.onNext(false)
                }
            }){
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

    fun getHostCode (code: String, completion: (error: String?, userData: JSONObject?) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("code", code)
        val requestBody = jsonBody.toString()

        val hostRequest = object: JsonObjectRequest(Method.POST, REQUEST_HOST_WITH_CODE, null, Response.Listener { response ->
            // do something here with the response
            completion(null, response)
        }, Response.ErrorListener { error ->
            try {
                val err = JSONObject(String(error.networkResponse.data))

                authResponseError = err.getString("message") as String
                completion(authResponseError, null)
            } catch (e: JSONException) {
                Log.d("HOST_CODE", e.localizedMessage)
                authResponseError = "Internal Server Error"
                completion(authResponseError, null)
            } catch (e: VolleyError) {
                Log.d("HOST_CODE", e.localizedMessage)
                authResponseError = "Slow Internet Connection"
                completion(authResponseError, null)
            } catch (e: NullPointerException) {
                Log.d("HOST_CODE", e.localizedMessage)
                authResponseError = "Internal Server Error"
                completion(authResponseError, null)
            } catch (e: Error) {
                Log.d("HOST_CODE", e.localizedMessage)
                authResponseError = "Internal Server Error"
                completion(authResponseError, null)
            }
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(hostRequest)
    }

    fun getHostCodeRx (code: String) : Observable<JSONObject?> {
        authResponseError = null
        return Observable.create {
            val jsonBody = JSONObject()
            jsonBody.put("code", code)
            val requestBody = jsonBody.toString()

            val hostRequest = object: JsonObjectRequest(Method.POST, REQUEST_HOST_WITH_CODE, null, Response.Listener { response ->
                // do something here with the response
                Log.d("CODE RESPONSE", response.toString())
                it.onNext(response)
            }, Response.ErrorListener { error ->
                try {
                   // val err = JSONObject(String(error.networkResponse.data))

                   // authResponseError = err.getString("message") as String
                    if (error.networkResponse.statusCode == 400) {
                        authResponseError = "Incorrect Credentials"
                    } else {
                        authResponseError = "Internal Server Error"
                    }
                    Log.d("HOST_CODE", "Internal server error")
                } catch (e: JSONException) {
                    Log.d("HOST_CODE", e.localizedMessage)
                    authResponseError = "Internal Server Error"
                } catch (e: VolleyError) {
                    Log.d("HOST_CODE", e.localizedMessage)
                    authResponseError = "Slow Internet Connection"
                } catch (e: NullPointerException) {
                    Log.d("HOST_CODE", e.localizedMessage)
                    authResponseError = "Internal Server Error"
                } catch (e: Error) {
                    Log.d("HOST_CODE", e.localizedMessage)
                    authResponseError = "Internal Server Error"
                } catch (e: Exception) {
                    Log.d("HOST_CODE", e.localizedMessage)
                }
                val jsonError = JSONObject()
                // jsonError.put("error", authResponseError)
                it.onNext(jsonError)
            }){
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }
            }

            App.prefs.requestQueue.add(hostRequest)
        }
    }

    fun userRefresh (email: String) : Observable<Boolean> {
        var requestBody = JSONObject()
        requestBody.put("email", email)

        authResponseError = null
        return Observable.create {
            val refreshRequest = object: JsonObjectRequest(Method.POST, REFRESH_AUTH, null, Response.Listener { response ->
                // do something here with the response
                Log.d("REFRESH_TOKEN", response.toString())
                val data = response.getJSONObject("data")
                this.processUserData(it, data)
            }, Response.ErrorListener { error ->
                var user: User
                try {
                    val err = JSONObject(String(error.networkResponse.data))

                    authResponseError = err.getString("message") as String
                    it.onNext(false)
                } catch (e: JSONException) {
                    Log.d("SIGNUP_ERROR", e.localizedMessage)
                    authResponseError = "Internal Server Error"
                    it.onNext(false)
                } catch (e: VolleyError) {
                    Log.d("SIGNUP_ERROR", e.localizedMessage)
                    authResponseError = "Slow Internet Connection"
                    it.onNext(false)
                } catch (e: NullPointerException) {
                    Log.d("SIGNUP_ERROR", e.localizedMessage)
                    authResponseError = "Internal Server Error"
                    it.onNext(false)
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
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(refreshRequest)
        }
    }

    fun validateRegistrationInput (type: String, value: String) : Observable<Boolean> {
        val requestBody = JSONObject()
        requestBody.put(type, value)

        return Observable.create {
            val validationRequest = object: JsonObjectRequest(Method.POST, REGISTRATIN_VALIDATION, null, Response.Listener { response ->
                if (response.has("data")) {
                    val data = response.getJSONObject("data")
                    if (!data.has("data") && !data.has("message")) {
                        it.onNext(false)
                    } else {
                        val message = if (data.has("data"))  data.getString("data") else data.getString("message")

                        if (message == "Invalid Input" || message == "already taken" || message == "User Error") {
                            it.onNext(false)
                        } else {
                            it.onNext(true)
                        }
                    }

                } else {
                    it.onNext(false)
                }
            }, Response.ErrorListener { error ->
                it.onNext(false)
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
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(validationRequest)
        }
    }

    fun firebaseTokenUpdate (token: String, reporterId: String, email: String, deviceId: String): Observable<Boolean> {
        return Observable.create {
            val requestBody = JSONObject()
            requestBody.put("reporterId", reporterId)
            requestBody.put("token", token)
            requestBody.put("deviceId", deviceId)
            requestBody.put("email", email)
            requestBody.put("platform", "ANDROID")

            Log.d("FCM_REQUEST", requestBody.toString())

            val firebaseTokenUpdateRequest = object: JsonObjectRequest(Method.POST, FIREBASE_TOKEN_UPDATE, null, Response.Listener { response ->
                it.onNext(true)
            }, Response.ErrorListener { error ->
                try {
                    val err = JSONObject(String(error.networkResponse.data))
                    Log.d("FCM_ERROR", err.getString("message"))
                }
                 catch (e: Exception) {

                 }
                it.onNext(false)
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
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(firebaseTokenUpdateRequest)
        }
    }
}