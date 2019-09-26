package com.straatinfo.straatinfo.Controllers.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.straatinfo.straatinfo.Adapters.TeamChatListAdapter
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Controllers.ReportMessagesActivity
import com.straatinfo.straatinfo.Models.Team
import com.straatinfo.straatinfo.Models.User

import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.TeamService
import com.straatinfo.straatinfo.Utilities.BROADCAST_NEW_MESSAGE_RECEIVED
import io.reactivex.schedulers.Schedulers
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.fragment_team_chat.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
lateinit var adapter: TeamChatListAdapter

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TeamChat.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TeamChat.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TeamChat : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.getTeamList { teams ->
            Log.d("TEAM_LIST", teams.toString())
            this.loadAdapter(teams)
        }

        val socket = App.socket
        Log.d("SOCKET_DETAILS", socket.toString())
        if (socket != null) {
            socket!!
                .on("new-message", onSendMessage)
                .on("send-message-v2", onSendMessage)
        }

        LocalBroadcastManager.getInstance(context!!).registerReceiver(this.newMessageDataReceiver, IntentFilter(
            BROADCAST_NEW_MESSAGE_RECEIVED
        ))

        return inflater.inflate(R.layout.fragment_team_chat, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onResume() {
        super.onResume()
        this.getTeamList { teams ->
            Log.d("TEAM_LIST", teams.toString())
            this.loadAdapter(teams)
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TeamChat.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TeamChat().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getTeamList (cb: (teamList: MutableList<Team>) -> Unit) {
        val user = User()
        val userId = user.id
        if (userId != null) {
            TeamService.getTeamList(userId)
                .subscribeOn(Schedulers.io())
                .subscribe { teamArray ->
                    try {
                        val teams = this.processTeamList(teamArray)
                        Log.d("TEAM_LIST", "LOADED")
                        cb(teams)
                    }
                    catch (e: Exception) {
                        Log.d("TEAM_LIST", e.localizedMessage)
                        cb(mutableListOf())
                    }
                }
                .run {  }
        } else {
            cb(mutableListOf())
        }
    }

    private fun processTeamList (teamArray: JSONArray): MutableList<Team> {
        val teamList = mutableListOf<Team>()
        for (i in 0 until teamArray.length()) {
            val teamJson = teamArray[i] as JSONObject
            val id = teamJson.getString("_id")
            val name = teamJson.getString("teamName")
            val email = teamJson.getString("teamEmail")

            var profile: JSONObject? = null

            if (teamJson.has("_profilePic")) {
                profile = teamJson.getJSONObject("_profilePic")
            }

            var team = Team(teamJson)

            Log.d("TEAM_LOAD", team.id + " " + team.name + " " + team.email)


            if (team.isApproved != null && team.isApproved!!) {
                teamList.add(teamList.count(), team)
            }

        }
        return teamList
    }

    private val onSendMessage = Emitter.Listener { args ->
        Log.d("RECEIVING_REPORT_LIST", args.toString())
        this.getTeamList { teams ->
            Log.d("TEAM_LIST", teams.toString())
            this.loadAdapter(teams)
        }
    }

    private fun reloadAdapter () {
        this.getTeamList { teams ->
            Log.d("TEAM_LIST", teams.toString())
            this.loadAdapter(teams)
        }
    }

    private val newMessageDataReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            reloadAdapter()
        }
    }

    private fun loadAdapter (teams: MutableList<Team>) {
        try {
            adapter = TeamChatListAdapter(context!!, teams) { team ->
                val intent = Intent(context!!, ReportMessagesActivity::class.java)
                intent.putExtra("CHAT_TITLE", team.name + " - Team chat")
                if (team.id != null && team.conversationId != null) {
                    val conversationId = team.conversationId!!
                    intent.putExtra("CONVERSATION_ID", conversationId)
                    intent.putExtra("TYPE", "TEAM")
                    intent.putExtra("TEAM_ID", team.id!!)
                    context!!.startActivity(intent)
                }
            }

            team_chat_list_recyler_view.adapter = adapter
            val layoutManager = LinearLayoutManager(context)
            team_chat_list_recyler_view.layoutManager = layoutManager
        } catch (e: Exception) {

        }
    }
}
