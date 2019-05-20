package com.straatinfo.straatinfo.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.straatinfo.straatinfo.Models.Team
import com.straatinfo.straatinfo.R

class TeamListAdapter (val context: Context, val teams: MutableList<Team>, val teamClick: (Team) -> Unit) : RecyclerView.Adapter<TeamListAdapter.Holder> () {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        val view = LayoutInflater.from(parent?.context).inflate(R.layout.team_list_adapter_view, parent, false)

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

        fun bindCategory (team: Team, context: Context) {
            nameTxt?.text = team.name
            emailTxt?.text = team.email

            if (team.profilePic != null) {
                val secureUrl = team.profilePic!!.getString("secure_url")
                Picasso.get().load(secureUrl).into(profileImg)
            }

            itemView.setOnClickListener {
                itemClick(team)
            }
        }
    }
}