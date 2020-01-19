package com.straatinfo.straatinfo.Controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.straatinfo.straatinfo.Adapters.TeamMemberListAdapter
import com.straatinfo.straatinfo.Models.TeamMember
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.MessageService
import com.straatinfo.straatinfo.Services.TeamService
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_team_details.*
import org.json.JSONArray
import org.json.JSONObject

class TeamDetailsActivity : AppCompatActivity() {

    lateinit var adapter: TeamMemberListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_details)

        this.loadNavigation()
        this.loadTeamDetails()
        this.loadAdapter()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        this.loadTeamDetails()
        this.loadAdapter()
    }

    private fun createConvo (chatee: String?, cb: (Boolean, String?) -> Unit) {
        val user = User()
        val userId = user.id

        if (userId != null && chatee != null) {
            MessageService.createPrivateConversation(userId, chatee)
                .subscribeOn(Schedulers.io())
                .subscribe { convo ->
                    if (convo.has("_id")) cb(true, convo.getString("_id"))
                    else cb(false, null)
                }
                .run {  }
        } else {
            cb(false, null)
        }
    }

    fun loadAdapter () {
        var teamId = intent.getStringExtra("TEAM_ID")
        this.loadTeamInfo(teamId) { teamMembers ->
            adapter = TeamMemberListAdapter(this, teamMembers, ({ teamMember: TeamMember ->
                val intent = Intent(this, TeamMemberProfileActivity::class.java)
                val teamMemberJsonString = teamMember.teamMemberJson.toString()
                Log.d("TEAM_MEMBER_DETAILS", teamMemberJsonString)
                intent.putExtra("TEAM_MEMBER", teamMemberJsonString)
                startActivity(intent)
            }), ({ teamMember ->
                Log.d("TEAM_CHAT", teamMember.userId)
                this.createConvo(teamMember.userId) { success, convoId ->
                    if (success && convoId != null) this.loadConversation(convoId!!, teamId!!, teamMember.username + " - Private chat")
                }
            }))

            teamDetailsListOfMembersRecyclerView.adapter = adapter
            val layoutManager = LinearLayoutManager(this)
            teamDetailsListOfMembersRecyclerView.layoutManager = layoutManager
        }
    }

    fun loadTeamInfo (teamId: String, cb: (MutableList<TeamMember>) -> Unit) {
        TeamService.getTeamInfo(teamId)
            .subscribeOn(Schedulers.io())
            .subscribe { teamInfo ->
                this.populateTeamMembers(teamInfo, cb)
            }
            .run {  }
    }

    fun loadNavigation () {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    fun loadTeamDetails () {
        val teamNameTxt = findViewById<TextView>(R.id.teamDetailsTeamName)
        val teamEmailTxt = findViewById<TextView>(R.id.teamDetailsTeamEmail)
        val teamLogo = findViewById<ImageView>(R.id.teamDetailsTeamLogo)
        val updateTeamTxt = findViewById<TextView>(R.id.teamDetailsUpdateTeamProfileTxt)

        var teamName = intent.getStringExtra("TEAM_NAME")
        var teamEmail = intent.getStringExtra("TEAM_EMAIL")
        var profile = intent.getStringExtra("TEAM_PROFILE_PIC")
        var teamId = intent.getStringExtra("TEAM_ID")

        teamNameTxt.text = teamName
        teamEmailTxt.text = teamEmail
        if (profile != null) {
            val _profile = JSONObject(profile)

            if (_profile.has("secure_url")) {
                val secureUrl = _profile.getString("secure_url")

                Picasso.get().load(secureUrl).into(teamLogo)
            }
        }

        updateTeamTxt.setOnClickListener { view ->
            val activity = Intent(this, TeamFormActivity::class.java)
            activity.putExtra("TEAM_NAME", teamName)
            activity.putExtra("TEAM_EMAIL", teamEmail)
            if (profile != null) activity.putExtra("TEAM_PROFILE_PIC", profile)
            activity.putExtra("TEAM_ID", teamId)
            activity.putExtra("FORM_TYPE", "UPDATE")

            startActivity(activity)
        }
    }

    fun populateTeamMembers (teamInfo: JSONObject, cb: (MutableList<TeamMember>) -> Unit) {
        var teamMembers = mutableListOf<TeamMember>()
        var teamMembersArray = if (teamInfo.has("teamMembers")) teamInfo.getJSONArray("teamMembers") else JSONArray()
        val userData = User(JSONObject(App.prefs.userData))

        for (i in 0 until teamMembersArray.length()) {

            var teamJson = teamMembersArray[i] as JSONObject
            var teamMember = TeamMember(teamJson)

            if (userData.id != teamMember.userId) {
                teamMembers.add(teamMembers.count(), teamMember)
            }
        }

        cb(teamMembers)
    }

    fun loadConversation (convoId: String, teamId: String, chatTitle: String) {
        val intent = Intent(this, ReportMessagesActivity::class.java)
        intent.putExtra("CHAT_TITLE", chatTitle)
        intent.putExtra("CONVERSATION_ID", convoId)
        intent.putExtra("TYPE", "TEAM")
        intent.putExtra("TEAM_ID", teamId)
        this.startActivity(intent)
    }
}
