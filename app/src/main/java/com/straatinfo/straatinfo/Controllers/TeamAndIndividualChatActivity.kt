package com.straatinfo.straatinfo.Controllers

import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.straatinfo.straatinfo.Adapters.TeamChatCardAdapter
import com.straatinfo.straatinfo.Models.Conversation
import com.straatinfo.straatinfo.Models.Team
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.TeamService
import com.straatinfo.straatinfo.Utilities.BROADCAST_NEW_MESSAGE_RECEIVED
import io.reactivex.schedulers.Schedulers
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_team_and_individual_chat.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception


class TeamAndIndividualChatActivity : AppCompatActivity() {
    lateinit var adapter: TeamChatCardAdapter


    var team: Team? = null
    var conversations: MutableList<Conversation> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_and_individual_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = "Chat list"

        this.fetchConversations { conversations ->
            val teamId = intent.getStringExtra("TEAM_ID")
            this.loadAdapter(conversations, teamId)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(this.newMessageDataReceiver, IntentFilter(
            BROADCAST_NEW_MESSAGE_RECEIVED
        ))

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        this.reloadAdapter()
    }

    private val newMessageDataReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
           reloadAdapter()
        }
    }

    private fun reloadAdapter () {
        val teamId = intent.getStringExtra("TEAM_ID")
        fetchConversations { conversations ->
            if (teamId != null) loadAdapter(conversations, teamId)
        }
    }


    private fun getIndividualChats (completion: (MutableList<Conversation>, JSONObject) -> Unit) {
        val teamId = intent.getStringExtra("TEAM_ID")
        val user = User()
        val userId = user.id

        TeamService.getTeamIndividualChats(userId!!, teamId)
            .subscribeOn(Schedulers.io())
            .subscribe {
                if (it.has("conversations")) {
                    var convoArray = it.getJSONArray("conversations")
                    var conversations = mutableListOf<Conversation>()
                    var count = 0
                    for (i in 0 until convoArray.length()) {

                        var convo = Conversation(convoArray.getJSONObject(i))

                        if (convo.chatName != null) {
                            conversations.add(count, convo)
                            count = i
                        }
                    }
                    var teamMessagePreview = it.getJSONObject("teamMessagePreview")
                    completion(conversations, teamMessagePreview)
                } else {
                    completion(mutableListOf(), JSONObject())
                }

            }
            .run {  }
    }

    private fun getTeamData (teamId: String, teamArray: JSONArray): Team? {
        var team: Team? = null
        for (i in 0 until teamArray.length()) {
            val teamJson = teamArray[i] as JSONObject

            var teamData = Team(teamJson)


            if (teamData.id == teamId) {
                team = teamData
            }

        }
        return team
    }

    private fun getTeam (completion: (Team?) -> Unit) {
        val teamId = intent.getStringExtra("TEAM_ID")
        val user = User()
        val userId = user.id

        if (userId != null) {
            TeamService.getTeamList(userId)
                .subscribe { teamArray ->
                    try {
                        val team = this.getTeamData(teamId, teamArray)
                        Log.d("TEAM_LIST", "LOADED")
                        completion(team)
                    }
                    catch (e: Exception) {
                        Log.d("TEAM", e.localizedMessage)
                        completion(null)
                    }
                }
                .run {  }
        } else {
            completion(null)
        }
    }


    private fun fetchConversations (completion: (MutableList<Conversation>) -> Unit) {
        this.getTeam { team ->
            if (team != null) {
                this.getIndividualChats { convos, teamMessagePreview ->
                    var teamConvoObject = JSONObject()
                    teamConvoObject.put("type", "TEAM")
                    teamConvoObject.put("_id", team.conversationId)
                    teamConvoObject.put("chatName", team.name)
                    teamConvoObject.put("unreadMessageCount", team.unreadMessageCount)
                    if (team.profilePic != null && team.profilePic!!.has("secure_url")) {
                        var secureUrl = team.profilePic!!.getString("secure_url")
                        teamConvoObject.put("profilePicUrl", secureUrl)
                    }
                    teamConvoObject.put("messagePreview", teamMessagePreview)

                    var teamConversation = Conversation(teamConvoObject)
                    var conversations = mutableListOf(teamConversation)
                    for (i in 0 until convos.count()) {
                        conversations.add(i + 1, convos[i])
                    }
                    completion(conversations)
                }
            }
        }
    }

    private fun loadAdapter (conversations: MutableList<Conversation>, teamId: String) {
        try {
            adapter = TeamChatCardAdapter(this, conversations) { conversation ->
                val intent = Intent(this, ReportMessagesActivity::class.java)
                if (conversation.type == "TEAM") {
                    intent.putExtra("CHAT_TITLE", conversation.chatName + " - Team chat")
                    if (teamId != null && conversation.id != null) {
                        val conversationId = conversation.id
                        intent.putExtra("CONVERSATION_ID", conversationId)
                        intent.putExtra("TYPE", "TEAM")
                        intent.putExtra("TEAM_ID", teamId)
                        startActivity(intent)
                    }
                }

                if (conversation.type == "PRIVATE") {
                    val conversationId = conversation.id
                    intent.putExtra("CHAT_TITLE", conversation.chatName + " - Private chat")
                    intent.putExtra("CONVERSATION_ID", conversationId)
                    intent.putExtra("TYPE", "TEAM")
                    intent.putExtra("TEAM_ID", teamId)
                    startActivity(intent)
                }
            }

            team_chat_card_list_recycler_view.adapter = adapter
            val layoutManager = LinearLayoutManager(this)
            team_chat_card_list_recycler_view.layoutManager = layoutManager
        } catch (e: Exception) {

        }
    }


}
