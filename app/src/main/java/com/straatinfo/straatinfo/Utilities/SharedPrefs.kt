package com.straatinfo.straatinfo.Utilities

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class SharedPrefs (context: Context) {
    val PREFS_FILENAME = "straatinfo-userprefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    val IS_LOGGED_IN = "isLoggedIn"
    val AUTH_TOKEN = "authToken"
    val USER_DATA = "userData"
    val REPORTER_ID = "reporterId"
    val USER_HOST = "userHost"
    val HOST_DATA = "HOST_DATA"

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var userData: String
        get() = prefs.getString(USER_DATA, "")
        set(value) = prefs.edit().putString(USER_DATA, value).apply()

    var hostData: String
        get() = prefs.getString(HOST_DATA, "")
        set(value) = prefs.edit().putString(HOST_DATA, value).apply()

    val requestQueue: RequestQueue = Volley.newRequestQueue(context)
}