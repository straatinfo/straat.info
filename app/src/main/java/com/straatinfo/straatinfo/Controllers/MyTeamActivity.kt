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
import com.straatinfo.straatinfo.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MyTeamActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private var activityViewId = R.id.nav_my_team

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_team)

        this.init()
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

        this.navigationHandler(item)
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

    fun navigationHandler (item: MenuItem) {
        // Handle navigation view item clicks here.
        Log.d("ITEM_ID", item.itemId.toString())
        if (item.itemId == this.activityViewId) {
            if (drawer_layout.isDrawerOpen(Gravity.END)) {
                drawer_layout.closeDrawer(Gravity.END)
            } else {
                drawer_layout.isDrawerOpen(Gravity.END)
            }
        } else {
            when (item.itemId) {
                R.id.nav_home -> {
                    val navMain = Intent(this, MainActivity::class.java)
                    startActivity(navMain)

                    finish()
                }
                R.id.nav_my_team -> {
                    val myTeam = Intent(this, MyTeamActivity::class.java)
                    startActivity(myTeam)
                    finish()
                }
                R.id.nav_logout -> {
                    val login = Intent(this, LoginActivity::class.java)
                    startActivity(login)
                    finish()
                }
                else -> {
                    if (drawer_layout.isDrawerOpen(Gravity.END)) {
                        drawer_layout.closeDrawer(Gravity.END)
                    } else {
                        drawer_layout.isDrawerOpen(Gravity.END)
                    }
                }

            }
        }

        drawer_layout.closeDrawer(GravityCompat.END)
    }
}
