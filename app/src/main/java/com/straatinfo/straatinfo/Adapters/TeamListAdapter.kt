package com.straatinfo.straatinfo.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.straatinfo.straatinfo.Controllers.TeamMemberRequestActivity
import com.straatinfo.straatinfo.Models.Team
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.TeamService
import io.reactivex.schedulers.Schedulers

class TeamListAdapter (val context: Context, val teams: MutableList<Team>, val teamClick: (Team) -> Unit) : RecyclerView.Adapter<TeamListAdapter.Holder> () {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        val view = LayoutInflater.from(parent?.context).inflate(R.layout.adapter_view_team_list, parent, false)

        return Holder(view, teamClick)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return teams.count()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bindCategory(teams[position], context)
    }

    inner class Holder(itemView: View, val itemClick: (Team) -> Unit): RecyclerView.ViewHolder(itemView) {

        // val idTxt = itemView?.findViewById<TextView>(R.id.teamListTeam)
        val nameTxt = itemView?.findViewById<TextView>(R.id.teamListTeamNameTxt)
        val emailTxt = itemView?.findViewById<TextView>(R.id.teamListTeamEmailTxt)
        val profileImg = itemView?.findViewById<ImageView>(R.id.teamListTeamProfileImg)

        var viewMemberRequest = itemView?.findViewById<TextView>(R.id.teamListViewMemberRequestTxt)
        val memberRequestCount = itemView?.findViewById<TextView>(R.id.teamListMemberRequestCountTxt)

        fun bindCategory (team: Team, context: Context) {
            nameTxt?.text = team.name
            emailTxt?.text = team.email

            TeamService.getTeamRequests(team.id!!)
                .subscribeOn(Schedulers.io())
                .subscribe { requests ->
                    if (requests.length() > 0) {
                        viewMemberRequest.visibility = View.VISIBLE
                        memberRequestCount.visibility = View.VISIBLE
                        memberRequestCount.text = requests.length().toString()


                        viewMemberRequest.setOnClickListener {
                            Log.d("ON_VIEW_REQUEST_CLICK", "CLICK")
                            val intent = Intent(context, TeamMemberRequestActivity::class.java)
                            intent.putExtra("TEAM_ID", team.id)
                            context.startActivity(intent)
                        }
                    } else {
                        viewMemberRequest.visibility = View.INVISIBLE
                        memberRequestCount.visibility = View.INVISIBLE
                    }
                }
                .run{}

            if (team.profilePic != null) {
                val secureUrl = team.profilePic!!.getString("secure_url")
                Picasso.get().load(secureUrl).into(profileImg)
            }

            itemView.setOnClickListener {
                itemClick(team)
            }
        }

        fun viewMemberRequest (teamId: String) {

        }
    }
}