package com.straatinfo.straatinfo.Controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.straatinfo.straatinfo.Adapters.TeamListCheckBoxListAdapter
import com.straatinfo.straatinfo.Models.Team
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.TeamService
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_report_type_cadd_team.*
import org.json.JSONArray
import org.json.JSONObject

class ReportTypeCAddTeamActivity : AppCompatActivity() {

    lateinit var adapter: TeamListCheckBoxListAdapter
    var teamIdList: JSONArray = JSONArray()
    var teamNameList: JSONArray = JSONArray()
    var teamIds: MutableList<String> = mutableListOf()
    var teamNames: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_type_cadd_team)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        this.loadTeams { teams ->
            loadTeamAdapter(teams) {

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun getHostId (): String? {
        val reportDetails = intent.getStringExtra("REPORT_TYPE_C_DETAILS")
        val reportJson = JSONObject(reportDetails)

        val hostId = if (reportJson.has("_host")) reportJson.getString("_host") else null
        return hostId
    }

    private fun getTeams (hostId: String, completion: (MutableList<Team>) -> Unit) {
        TeamService.getHostTeamList(hostId)
            .subscribeOn(Schedulers.io())
            .subscribe { teamListData ->
                var teamList = mutableListOf<Team>()
                for (i in 0 until teamListData.length()) {
                    val teamData = teamListData[i] as JSONObject
                    val team = Team(teamData)
                    teamList.add(i, team)
                }

                completion(teamList)
            }
            .run {  }
    }

    private fun loadTeams (completion: (MutableList<Team>) -> Unit) {
        val hostId = this.getHostId()
        if (hostId == null) {
            completion(mutableListOf())
        } else {

            this.getTeams(hostId!!) { teams ->
                for (i in 0 until teams.count()) {
                    Log.d("TEAM_NAME: ", teams[i].name)
                }
                completion(teams)
            }
        }
    }

    private fun loadTeamAdapter (teams: MutableList<Team>, completion: () -> Unit) {
        adapter = TeamListCheckBoxListAdapter(this, teams, { team ->

        }, { teamId, teamName, hasSelected ->
            Log.d("SELECTED_TEAM", "$teamId, $hasSelected")
            addOrRemoveId(teamId, teamName, hasSelected)
        })

        report_type_c_team_list_recycler_view.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        report_type_c_team_list_recycler_view.layoutManager = layoutManager

        completion()
    }

    private fun addOrRemoveId (teamId: String, teamName: String, isChecked: Boolean) {
        if (isChecked) {
            if (!teamIds.contains(teamId)) {
                teamIds.add(teamId)
                teamNames.add(teamName)
            }
        } else {
            teamIds.remove(teamId)
            teamNames.remove(teamName)
        }

        this.teamIdList = convertTeamIdsToJsonArray()
        this.teamNameList = convertTeamNamesToJsonArray()
    }

    private fun convertTeamIdsToJsonArray (): JSONArray {
        var json = JSONArray()
        for (i in 0 until this.teamIds.count()) {
            json.put(this.teamIds[i])
        }
        this.report_type_c_add_team_next_btn.isEnabled = json.length() > 0
        return json
    }

    private fun convertTeamNamesToJsonArray (): JSONArray {
        var json = JSONArray()
        for (i in 0 until this.teamNames.count()) {
            json.put(this.teamNames[i])
        }

        return json
    }

    fun backButton (view: View) {
        this.teamIdList = JSONArray()
        finish()
    }

    fun nextButton (view: View) {
        val reportDetails = intent.getStringExtra("REPORT_TYPE_C_DETAILS")
        val reportJson = JSONObject(reportDetails)
        reportJson.put("teamList", this.teamIdList)
        val nextIntent = Intent(this, ReportTypeCViewInput::class.java)
        nextIntent.putExtra("REPORT_TYPE_C_DETAILS", reportJson.toString())
        nextIntent.putExtra("REPORT_TYPE_C_TEAM_LIST", this.teamNameList.toString())
        startActivity(nextIntent)
    }
}
