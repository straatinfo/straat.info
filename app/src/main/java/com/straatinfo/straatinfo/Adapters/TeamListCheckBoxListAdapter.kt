package com.straatinfo.straatinfo.Adapters

import android.content.Context
import android.support.design.internal.BottomNavigationItemView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.straatinfo.straatinfo.Models.Team
import com.straatinfo.straatinfo.R

class TeamListCheckBoxListAdapter (val context: Context, val teams: MutableList<Team>, val teamClick: (Team) -> Unit, val onCheckBoxClick: (String, String, Boolean) -> Unit) : RecyclerView.Adapter<TeamListCheckBoxListAdapter.Holder> () {
    var items = teams
    var itemStateArray = SparseBooleanArray()
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.adapter_team_list_check_boxes, parent, false)
        val team = teams[p1]
        return Holder(view, team.id, teamClick, onCheckBoxClick)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }


    override fun getItemCount(): Int {
        return teams.count()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bindCategory(teams[position], context, position)
    }

    inner class Holder(itemView: View, teamId: String?, val itemClick: (Team) -> Unit, val onCheckBoxClick: (String, String, Boolean) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val teamName = itemView?.findViewById<TextView>(R.id.team_list_check_box_team_name)
        val checkBox = itemView?.findViewById<CheckBox>(R.id.team_list_check_box_cb)
        val teamEmail = itemView?.findViewById<TextView>(R.id.team_list_check_box_team_email)
        val teamImg = itemView?.findViewById<ImageView>(R.id.team_list_check_box_img)
        val _team = teamId

        fun bindCategory (team: Team, context: Context, position: Int) {
            teamName?.text = team.name
            teamEmail?.text = team.email
            Log.d("PROFILE_PIC", team.profilePic.toString())
            if (team.profilePic != null && team.profilePic!!.has("secure_url")) {
                val secureUrl = team.profilePic!!.getString("secure_url")
                Picasso.get()
                    .load(secureUrl)
                    .resize(60, 60) // resizes the image to these dimensions (in pixel)
                    .centerCrop()
                    .into(teamImg)
            } else {
                teamImg.setImageResource(R.drawable.ic_logo)
            }

            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                updateCB(itemView, team?.id!!, team?.name!!, isChecked)
            }

            checkBox.isChecked = itemStateArray.get(position)


            itemView.setOnClickListener {

                val checkBox = itemView?.findViewById<CheckBox>(R.id.team_list_check_box_cb)

                val adapterPosition = adapterPosition
                if (!itemStateArray.get(adapterPosition, false)) {
                    checkBox.isChecked = true
                    itemStateArray.put(adapterPosition, true)
                } else {
                    checkBox.isChecked = false
                    itemStateArray.put(adapterPosition, false)
                }
            }

        }
    }

    fun updateCB (itemView: View, teamId: String, teamName: String, isCheked: Boolean) {
        // val checkBox = itemView?.findViewById<CheckBox>(R.id.team_list_check_box_cb)
        Log.d("CLICKING_CB", "$teamId , $isCheked")
        // checkBox.isChecked = true
        onCheckBoxClick(teamId, teamName, isCheked)
    }
}