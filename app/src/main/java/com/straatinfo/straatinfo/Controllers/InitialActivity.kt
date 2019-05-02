package com.straatinfo.straatinfo.Controllers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import com.straatinfo.straatinfo.R
import android.Manifest.permission
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import java.util.jar.Manifest


class InitialActivity : AppCompatActivity() {

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds
    private val INTERNET_RECORD_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)

        //Initializing the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            INTERNET_RECORD_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION", "Permission has been denied by user")
                    this.makeRequest()
                } else {
                    Log.d("PERMISSION", "Permission has been granted by user")
                    this.continueActivity()
                }
            }
        }
    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun setpermission () {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        val internetPerm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)

        if (internetPerm != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSION", "Permission denied")
        }

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION_PERMISSION", "Permission denied")
        }


        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Permission to access Internet is required")
            builder.setTitle(("Internet Permission"))
            builder.setPositiveButton("OK") { dialog, which ->
                Log.d("PERMISSION", "clicked")
                this.makeRequest()
            }

            val dialog = builder.create()
            dialog.show()
        } else {
            this.makeRequest()
        }
    }

    private fun makeRequest () {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), INTERNET_RECORD_CODE)
    }

    private val mRunnable: Runnable = Runnable {
        setpermission()
    }


     private fun continueActivity() {
         var intent: Intent? = null
        if (App.prefs.isLoggedIn) {
            // intent = Intent(applicationContext, MainActivity::class.java)
            intent = Intent(this, HomeActivity::class.java)
        } else {
            intent = Intent(applicationContext, EnterCodeActivity::class.java)
        }

        startActivity(intent)
        finish()
    }


}
