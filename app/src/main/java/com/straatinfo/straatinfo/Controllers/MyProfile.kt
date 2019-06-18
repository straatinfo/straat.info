package com.straatinfo.straatinfo.Controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.NavigationService
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject

class MyProfile : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private var activityViewId = R.id.nav_profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        this.init()
        this.loadUserDetails()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reports -> {
                val intent = Intent(this, ReportsActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        NavigationService.navigationHandler(this, item, activityViewId, drawer_layout, false)
        return true
    }


    private fun init() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Straat.Info"


        ViewCompat.setLayoutDirection(toolbar, ViewCompat.LAYOUT_DIRECTION_RTL)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        toolbar.setNavigationOnClickListener(navigationOnClickListener())
        nav_view.setNavigationItemSelectedListener(this)

    }

    private fun navigationOnClickListener() = View.OnClickListener {
        if (drawer_layout.isDrawerOpen(Gravity.END)) {
            drawer_layout.closeDrawer(Gravity.END)
        } else {
            drawer_layout.openDrawer(Gravity.END)
        }
        // Toast.makeText(this, "toggle", Toast.LENGTH_LONG).show()
    }


    fun loadUserDetails () {
        val fnameTxt = findViewById<EditText>(R.id.profileFnameTxt)
        val lnameTxt = findViewById<EditText>(R.id.profileLnameTxt)
        val houseNumberTxt = findViewById<EditText>(R.id.profileHouseNumberTxt)
        val postCodeTxt = findViewById<EditText>(R.id.profilePostCodeTxt)
        val streetNameTxt = findViewById<EditText>(R.id.profileStreetNameTxt)
        val cityTxt = findViewById<EditText>(R.id.profileCityTxt)
        val emailTxt = findViewById<EditText>(R.id.profileEmailTxt)
        val phoneNumber = findViewById<EditText>(R.id.profilePhoneNumberTxt)
        val usernameTxt = findViewById<EditText>(R.id.profileUsernameTxt)
        val profilePicImg = findViewById<ImageView>(R.id.profileProfilePicImg)

        val userData = App.prefs.userData
        val userJson = JSONObject(userData)
        Log.d("USER_DATA", userData)
        val user = User(userJson)

        fnameTxt.setText(user.firstname)
        lnameTxt.setText(user.lastname)
        houseNumberTxt.setText(user.houseNumber)
        postCodeTxt.setText(user.postalCode)
        streetNameTxt.setText(user.streetName)
        cityTxt.setText(user.city)
        emailTxt.setText(user.email)
        phoneNumber.setText(user.phoneNumber)
        usernameTxt.setText(user.username)

        Log.d("HASPROFILE", userJson.has("_profilePic").toString())

        if (userJson.has("user")) {
            val userD = userJson.getJSONObject("user")
            if (userD.has("_profilePic")) {
                val profilePic = userD.getJSONObject("_profilePic")
                if (profilePic.has("secure_url")) {
                    val secureUrl = profilePic.getString("secure_url")

                    Log.d("LOADING_IMAGE", secureUrl)

                    Glide.with(this).load(secureUrl).into(profilePicImg)
                }
            }

        }
    }
}
