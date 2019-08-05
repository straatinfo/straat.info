package com.straatinfo.straatinfo.Controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.MenuItemCompat
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.straatinfo.straatinfo.Controllers.Fragments.ReportListPublic
import com.straatinfo.straatinfo.Controllers.Fragments.ReportListSuspicious
import com.straatinfo.straatinfo.Controllers.Fragments.TeamChat
import com.straatinfo.straatinfo.R
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_initial.view.*

class ReportsActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_map)

        Log.d("SELECTED_ACTIVITY", "Reports activity")

        setupBottomNavigationView()

        val socket = App.socket

        if (socket != null) {
            socket!!
                .on("new-message", onSendMessage)
                .on("send-message-v2", onSendMessage)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when (item?.itemId) {
//            R.id.my_reports -> {
//                NavUtils.navigateUpFromSameTask(this)
//                return true
//            }
//        }
        val mainActivity = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        selectFragment(item)
        return true
    }


    private fun setupBottomNavigationView () {
        var bottomNavigation: BottomNavigationView? = findViewById(R.id.reports_bottom_navigation)

        if (bottomNavigation != null) {
            var menu = bottomNavigation.menu

            selectFragment(menu.getItem(0))


            bottomNavigation.setOnNavigationItemSelectedListener(this)
        }
    }

    private fun selectFragment (menuItem: MenuItem) {
        menuItem.isChecked = true
        var fragment: Fragment? = null
        Log.d("MENU_SELECTED", menuItem.itemId.toString())
        when (menuItem.itemId) {
            R.id.report_public -> fragment = ReportListPublic()
            R.id.report_suspicious -> fragment = ReportListSuspicious()
            R.id.report_chat -> fragment = TeamChat()
            else -> null // fragment = ReportListSuspicious()
        }

        if (fragment != null) {
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.replace(R.id.rootLayout, fragment)
            ft.commit()
        }

    }

    private val onSendMessage = Emitter.Listener { args ->
        Log.d("RECEIVING_REPORT_LIST", args.toString())
//        ReportListPublic().loadPublicReports {  }
//        ReportListSuspicious().loadPublicReports {  }
    }


}
