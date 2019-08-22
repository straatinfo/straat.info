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

class TeamChatListAdapter (val context: Context, val teams: MutableList<Team>, val teamClick: (Team) -> Unit) : RecyclerView.Adapter<TeamChatListAdapter.Holder> () {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): TeamChatListAdapter.Holder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.adapter_view_team_chat_list, parent, false)

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

    inner class Holder (itemView: View, val itemClick: (Team) -> Unit): RecyclerView.ViewHolder(itemView) {

        val teamName = itemView?.findViewById<TextView>(R.id.team_chat_name)
        val teamEmail = itemView?.findViewById<TextView>(R.id.team_chat_email)
        val teamImg = itemView?.findViewById<ImageView>(R.id.team_chat_img)
        val unreadMessage = itemView?.findViewById<TextView>(R.id.team_chat_report_txt)
        fun bindCategory(team: Team, context: Context) {
            teamName?.text = team.name
            teamEmail?.text = team.email
            unreadMessage?.text = team.unreadMessageCount.toString()
            if (team.profilePic != null) {
                val secureUrl = team.profilePic!!.getString("secure_url")
                Picasso.get().load(secureUrl).into(teamImg)
            }

            itemView.setOnClickListener {
                itemClick(team)
            }
        }
    }
}