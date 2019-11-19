package com.straatinfo.straatinfo.Controllers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import com.straatinfo.straatinfo.Controllers.Fragments.ReportListPublic
import com.straatinfo.straatinfo.Controllers.Fragments.ReportListSuspicious
import com.straatinfo.straatinfo.Controllers.Fragments.TeamChat
import com.straatinfo.straatinfo.R
import io.socket.emitter.Emitter
import android.support.design.internal.BottomNavigationItemView
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import com.straatinfo.straatinfo.Controllers.Fragments.ReportListGovernment
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.Services.MessageService
import com.straatinfo.straatinfo.Utilities.BROADCAST_NEW_MESSAGE_RECEIVED
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject


class ReportsActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_map)

        Log.d("SELECTED_ACTIVITY", "Reports activity")
        val socket = App.socket

        if (socket != null) {
            socket!!
                .on("new-message", onSendMessage)
                .on("send-message-v2", onSendMessage)
        }

        setupBottomNavigationView()

        LocalBroadcastManager.getInstance(this).registerReceiver(this.newMessageDataReceiver, IntentFilter(
            BROADCAST_NEW_MESSAGE_RECEIVED))
    }

    override fun onResume() {
        super.onResume()
        var bottomNavigation: BottomNavigationView? = findViewById(R.id.reports_bottom_navigation)
        if (bottomNavigation != null) {
            this.getUnreadCount { a, b, c, team ->
                bottomNavigation.selectedItemId
                Log.d("SELECTED_ITEM", bottomNavigation.selectedItemId.toString())
                Log.d("SELECTED_ITEM_PUB", R.id.report_public.toString())
                this.showBadge(this, bottomNavigation!!, R.id.report_public, a)
                this.showBadge(this, bottomNavigation!!, R.id.report_suspicious, b)
                this.showBadge(this, bottomNavigation!!, R.id.report_chat, team)
            }
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
            this.showBadge(this, bottomNavigation!!, R.id.report_public, App.prefs.unreadPublicReportMessage)
            this.showBadge(this, bottomNavigation!!, R.id.report_suspicious, App.prefs.unreadSuspiciousReportMessage)
            this.showBadge(this, bottomNavigation!!, R.id.report_chat, 0)
        }
    }

    private fun showBadge (context: Context, bottomNavigationView: BottomNavigationView, itemId: Int, value: Int) {
        removeBadge(bottomNavigationView, itemId)
        this@ReportsActivity.runOnUiThread(java.lang.Runnable {
            if (value > 0) {
                val itemView = bottomNavigationView.findViewById<BottomNavigationItemView>(itemId)
                val badge = LayoutInflater.from(context).inflate(R.layout.action_layout_unread_message_counter, bottomNavigationView, false)
                val text = badge.findViewById<TextView>(R.id.badge_text_view)
                text.text = value.toString()
                itemView.addView(badge)
            }
        })


    }

    private fun removeBadge (bottomNavigationView: BottomNavigationView, itemId: Int) {
        this@ReportsActivity.runOnUiThread(java.lang.Runnable {
            val itemView = bottomNavigationView.findViewById<BottomNavigationItemView>(itemId)
            Log.d("CHILD_COUNT", itemView.childCount.toString())
            if (itemView.childCount == 3) {
                itemView.removeViewAt(2)
            }
        })
    }


    private fun selectFragment (menuItem: MenuItem) {
        menuItem.isChecked = true
        var fragment: Fragment? = null
        Log.d("MENU_SELECTED", menuItem.itemId.toString())
        when (menuItem.itemId) {
            R.id.report_public -> fragment = ReportListPublic()
            R.id.report_suspicious -> fragment = ReportListSuspicious()
            R.id.report_government -> fragment = ReportListGovernment()
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
        Log.d("RECEIVING_REPORT_LIST", args[0].toString())
//        ReportListPublic().loadPublicReports {  }
//        ReportListSuspicious().loadPublicReports {  }
        val json = JSONObject(args[0].toString())
        if (json.has("conversation")) {
            val conversation = json.getJSONObject("conversation")
            if (conversation.has("_report")) {
                val report = conversation.getJSONObject("_report")
                val reportType = if (report.has("_reportType")) report.getJSONObject("_reportType") else null
                if (reportType != null) {
                    val code = if (reportType!!.has("code")) reportType!!.getString("code") else null
                    var bottomNavigation: BottomNavigationView? = findViewById(R.id.reports_bottom_navigation)
                    if (code != null && bottomNavigation != null) {
                        this.getUnreadCount { a, b, c, team->
                            if (code == "A") this.showBadge(this, bottomNavigation!!, R.id.report_public, a)
                            if (code == "B") this.showBadge(this, bottomNavigation!!, R.id.report_suspicious, b)
                        }

                    }
                }
            } else if (conversation.has("_team")) {
                var bottomNavigation: BottomNavigationView? = findViewById(R.id.reports_bottom_navigation)
                this.getUnreadCount { a, b, c, team->
                    this.showBadge(this, bottomNavigation!!, R.id.report_chat, team)
                }
            }
        }
        // this.load()
    }

    private fun reloadBadges () {
        var bottomNavigation: BottomNavigationView? = findViewById(R.id.reports_bottom_navigation)
        this.getUnreadCount { a, b, c, team->
            this.showBadge(this, bottomNavigation!!, R.id.report_public, a)
            this.showBadge(this, bottomNavigation!!, R.id.report_suspicious, b)
            this.showBadge(this, bottomNavigation!!, R.id.report_chat, team)
        }

    }

    private val newMessageDataReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("NEW_MESSAGE", "NEW_MESSAGE_RECEIVED")
           reloadBadges()
        }
    }

    private fun getUnreadCount (cb: (Int, Int, Int, Int) -> Unit) { // a, b, c
        val userId = User().id
        if (userId != null) {
            MessageService.getUnreadMessagesGroupByReports(userId)
                .subscribeOn(Schedulers.io())
                .subscribe { response ->
                    val a = if (response.has("a")) response.getInt("a") else 0
                    val b = if (response.has("b")) response.getInt("b") else 0
                    val c = if (response.has("c")) response.getInt("c") else 0
                    val team = if (response.has("team")) response.getInt("team") else 0

                    cb(a, b, c, team)
                }
                .run {  }
        } else {
            cb(0, 0, 0, 0)
        }
    }
}
