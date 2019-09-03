package com.straatinfo.straatinfo.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.straatinfo.straatinfo.Models.TeamMember
import com.straatinfo.straatinfo.R

class TeamMemberListAdapter (val context: Context, val teamRequests: MutableList<TeamMember>, val teamMebershipClick: (TeamMember) -> Unit, val teamChatClick: (TeamMember) -> Unit): RecyclerView.Adapter<TeamMemberListAdapter.Holder> () {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        val view = LayoutInflater.from(parent?.context).inflate(R.layout.adapter_view_team_member_list, parent, false)

        return Holder(view, teamMebershipClick, teamChatClick)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return teamRequests.count()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bindCategory(teamRequests[position], context)
    }



    inner class Holder (itemView: View, val itemClick: (TeamMember) -> Unit, val chatClick: (TeamMember) -> Unit): RecyclerView.ViewHolder(itemView) {
        val fullnameTxt = itemView?.findViewById<TextView>(R.id.teamMemberListFullNameTxt)
        val addBtn = itemView?.findViewById<ImageButton>(R.id.teamMemberListAddBtn)
        val chatBtn = itemView?.findViewById<ImageButton>(R.id.teamMemberListChatBtn)
        val removeBtn = itemView?.findViewById<ImageButton>(R.id.teamMemberListRemoveBtn)

        fun bindCategory (teamRequest: TeamMember, context: Context) {
            chatBtn.visibility = View.VISIBLE
            addBtn.visibility = View.GONE
            removeBtn.visibility = View.GONE

            fullnameTxt.text = "${teamRequest.userFname} ${teamRequest.userLname}"
            chatBtn.setOnClickListener { view ->
                chatClick(teamRequest)
            }

            itemView.setOnClickListener {
                itemClick(teamRequest)
            }
        }
    }
}