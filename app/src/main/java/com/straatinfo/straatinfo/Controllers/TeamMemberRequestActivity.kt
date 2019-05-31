package com.straatinfo.straatinfo.Controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import com.straatinfo.straatinfo.Adapters.TeamMemberRequestListAdapter
import com.straatinfo.straatinfo.Models.TeamMember
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.TeamService
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_team_member_request.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONArray
import org.json.JSONObject

class TeamMemberRequestActivity : AppCompatActivity() {

    lateinit var adapter: TeamMemberRequestListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_member_request)



        loadTeamList { teamRequests ->
            Log.d("TEAM_REQUEST", teamRequests.count().toString())
            adapter = TeamMemberRequestListAdapter(this, teamRequests, { teamRequest ->

            }, { requestSuccess ->
                when (requestSuccess) {
                    true -> {
                        val intent = Intent(this, MyTeamActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            })

            teamMemberRequestRecyclerView.adapter = adapter
            val layoutManager = LinearLayoutManager(this)
            teamMemberRequestRecyclerView.layoutManager = layoutManager
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    fun loadTeamList (cb: (MutableList<TeamMember>) -> Unit) {
        val teamId = intent.getStringExtra("TEAM_ID")

        Log.d("TEAM_ID", teamId)

        if (teamId != null) {
            TeamService.getTeamRequests(teamId)
                .subscribeOn(Schedulers.io())
                .subscribe { teamRequests ->
                    this.populateRequests(teamRequests, cb)
                }
                .run {  }
        }
    }

    fun populateRequests (teamRequestArray: JSONArray, cb: (MutableList<TeamMember>) -> Unit) {
        var teamRequests = mutableListOf<TeamMember>()

        for (i in 0 until teamRequestArray.length()) {
            val teamRequestJson = teamRequestArray[i] as JSONObject

            val teamRequest = TeamMember(teamRequestJson)

            Log.d("TEAM_REQUEST_ID", teamRequest.teamId)

            teamRequests.add(teamRequests.count(), teamRequest)
        }

        cb(teamRequests)
    }

}
