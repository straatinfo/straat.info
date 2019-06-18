package com.straatinfo.straatinfo.Services

import android.app.Activity
import android.content.Intent
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import com.straatinfo.straatinfo.Controllers.EnterCodeActivity
import com.straatinfo.straatinfo.Controllers.MainActivity
import com.straatinfo.straatinfo.Controllers.MyProfile
import com.straatinfo.straatinfo.Controllers.MyTeamActivity
import com.straatinfo.straatinfo.R

object NavigationService {
    fun navigationHandler (context: Activity, item: MenuItem, activityViewId: Int, drawer_layout: DrawerLayout, refreshAlways: Boolean) {
        // Handle navigation view item clicks here.
        Log.d("ITEM_ID", item.itemId.toString())
        if (refreshAlways || item.itemId != activityViewId) {
            when (item.itemId) {
                R.id.nav_home -> {
                    val navMain = Intent(context, MainActivity::class.java)
                    context.startActivity(navMain)

                    context.finish()
                }
                R.id.nav_my_team -> {
                    val myTeam = Intent(context, MyTeamActivity::class.java)
                    context.startActivity(myTeam)
                    context.finish()
                }
                R.id.nav_logout -> {
                    val enterCode = Intent(context, EnterCodeActivity::class.java)
                    context.startActivity(enterCode)
                    context.finish()
                }
                R.id.nav_profile -> {
                    val profile = Intent(context, MyProfile::class.java)
                    context.startActivity(profile)
                    context.finish()
                }
                else -> {
                    if (drawer_layout.isDrawerOpen(Gravity.END)) {
                        drawer_layout.closeDrawer(Gravity.END)
                    } else {
                        drawer_layout.isDrawerOpen(Gravity.END)
                    }
                }

            }
        } else {
            if (drawer_layout.isDrawerOpen(Gravity.END)) {
                drawer_layout.closeDrawer(Gravity.END)
            } else {
                drawer_layout.isDrawerOpen(Gravity.END)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.END)
    }
}