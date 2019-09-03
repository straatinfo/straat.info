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
    val REG_DATA = "REG_DATA"
    val REG_PAGE = "REG_PAGE"

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var token: String
        get() = prefs.getString(AUTH_TOKEN, "")
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()

    var userData: String
        get() = prefs.getString(USER_DATA, "")
        set(value) = prefs.edit().putString(USER_DATA, value).apply()

    var hostData: String
        get() = prefs.getString(HOST_DATA, "")
        set(value) = prefs.edit().putString(HOST_DATA, value).apply()

    var registrationData: String
        get() = prefs.getString(REG_DATA, "")
        set(value) = prefs.edit().putString(REG_DATA, value).apply()

    var registrationPage: Int
        get() = prefs.getInt(REG_PAGE, 1)
        set(value) = prefs.edit().putInt(REG_PAGE, value).apply()

    // temporary bug missing password in registration
    var registrationPassword: String
        get() = prefs.getString("PASS", "")
        set(value) = prefs.edit().putString("PASS", value).apply()

    var unreadPublicReportMessage: Int
        get() = prefs.getInt("UNREAD_PUBLIC_REPORT_MESSAGE", 0)
        set(value) = prefs.edit().putInt("UNREAD_PUBLIC_REPORT_MESSAGE", value).apply()

    var unreadSuspiciousReportMessage: Int
        get() = prefs.getInt("UNREAD_PUBLIC_SUSPICIOUS_MESSAGE", 0)
        set(value) = prefs.edit().putInt("UNREAD_PUBLIC_SUSPICIOUS_MESSAGE", value).apply()

    val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    val context: Context = context
}
