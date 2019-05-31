package com.straatinfo.straatinfo.Controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.straatinfo.straatinfo.Adapters.TeamListAdapter
import com.straatinfo.straatinfo.Models.Team
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.NavigationService
import com.straatinfo.straatinfo.Services.TeamService
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.drawer_layout
import kotlinx.android.synthetic.main.activity_home.nav_view
import kotlinx.android.synthetic.main.activity_my_team.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONArray
import org.json.JSONObject

class MyTeamActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private var activityViewId = R.id.nav_my_team
    var teamList = mutableListOf<Team>()
    lateinit var adapter: TeamListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_team)

        this.init()
        this.loadTeamList {
            adapter = TeamListAdapter(this, teamList) { team ->
                // do something here
                val intent = Intent(this, TeamDetailsActivity::class.java)
                intent.putExtra("TEAM_ID", team.id!!)
                intent.putExtra("TEAM_NAME", team.name!!)
                intent.putExtra("TEAM_EMAIL", team.email!!)
                if (team.profilePic != null) {
                    intent.putExtra("TEAM_PROFILE_PIC", team.profilePic!!.toString())
                }
                startActivity(intent)
            }

            teamListRecyclerView.adapter = adapter
            val layoutManager = LinearLayoutManager(this)
            teamListRecyclerView.layoutManager = layoutManager

            if (teamList.count() == 0) {
                // @TODO if no team seen show an alert dialog
            } else {
                // @TODO show add team button
                teamListAddNewTeamBtn.visibility = View.VISIBLE
            }
        }

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
        NavigationService.navigationHandler(this, item, activityViewId, drawer_layout, true)
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

    private fun teamLoader (teamArray: JSONArray, cb: () -> Unit) {
        teamList = mutableListOf()
        for (i in 0 until teamArray.length()) {
            val teamJson = teamArray[i] as JSONObject
            val id = teamJson.getString("_id")
            val name = teamJson.getString("teamName")
            val email = teamJson.getString("teamEmail")

            var profile: JSONObject ? = null

            if (teamJson.has("_profilePic")) {
                profile = teamJson.getJSONObject("_profilePic")
            }

            var team = Team(teamJson)

            Log.d("TEAM_LOAD", team.id + " " + team.name + " " + team.email)


            if (team.isApproved != null && team.isApproved!!) {
                teamList.add(teamList.count(), team)
            }

        }

        cb()

    }

    fun loadTeamList (cb: () -> Unit) {
        val userData = App.prefs.userData
        val userJson = JSONObject(userData)
        Log.d("USER_DATA_TEAM_LIST", userData)
        val user = User(userJson)

        teamList = mutableListOf()

       TeamService.getTeamList(user.id!!)
           .subscribeOn(Schedulers.io())
           .subscribe { teams ->
               this.teamLoader(teams, cb)
           }
           .run {}
    }

    fun onCreateTeamButtonPress (view: View) {
        val intent = Intent(this, TeamFormActivity::class.java)
        intent.putExtra("FORM_TYPE", "CREATE")
        startActivity(intent)
    }
}
