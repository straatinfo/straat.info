package com.straatinfo.straatinfo.Controllers

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import com.straatinfo.straatinfo.Utilities.SharedPrefs

class App : Application() {

    companion object {
        lateinit var prefs: SharedPrefs
    }

    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        super.onCreate()
    }
}