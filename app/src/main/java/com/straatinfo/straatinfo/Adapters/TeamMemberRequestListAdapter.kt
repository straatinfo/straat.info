package com.straatinfo.straatinfo.Adapters

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.straatinfo.straatinfo.Models.TeamMember
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.TeamService
import io.reactivex.schedulers.Schedulers

class TeamMemberRequestListAdapter (val context: Context, val teamRequests: MutableList<TeamMember>, val teamMebershipClick: (TeamMember) -> Unit, val onAcceptCompletion: (Boolean) -> Unit): RecyclerView.Adapter<TeamMemberRequestListAdapter.Holder> () {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        val view = LayoutInflater.from(parent?.context).inflate(R.layout.adapter_view_team_member_list, parent, false)

        return Holder(view, teamMebershipClick)
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



    inner class Holder (itemView: View, val itemClick: (TeamMember) -> Unit): RecyclerView.ViewHolder(itemView) {
        val fullnameTxt = itemView?.findViewById<TextView>(R.id.teamMemberListFullNameTxt)
        val addBtn = itemView?.findViewById<ImageButton>(R.id.teamMemberListAddBtn)
        val chatBtn = itemView?.findViewById<ImageButton>(R.id.teamMemberListChatBtn)
        val removeBtn = itemView?.findViewById<ImageButton>(R.id.teamMemberListRemoveBtn)

        fun bindCategory (teamRequest: TeamMember, context: Context) {
            chatBtn.visibility = View.GONE
            addBtn.visibility = View.VISIBLE
            removeBtn.visibility = View.VISIBLE

            fullnameTxt.text = "${teamRequest.userFname} ${teamRequest.userLname}"
            addBtn.setOnClickListener { view ->
                val dialog = AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.team_member_request_confirmation_title))
                    .setMessage(context.getString(R.string.team_member_request_accept_user_confirmation))
                    .setPositiveButton(context.getString(R.string.yes)) { dialog, which ->
                        dialog.dismiss()
                        this.acceptMember(teamRequest.userId!!, teamRequest.teamId!!, context)
                    }
                    .setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                dialog.show()
            }

            removeBtn.setOnClickListener { view ->
                val dialog = AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.team_member_request_decline_confirmation_title))
                    .setMessage(context.getString(R.string.team_member_request_decline_body))
                    .setPositiveButton(context.getString(R.string.yes)) { dialog, which ->
                        dialog.dismiss()
                        this.declineMember(teamRequest.userId!!, teamRequest.teamId!!, context)
                    }
                    .setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                dialog.show()
            }

            itemView.setOnClickListener {
                itemClick(teamRequest)
            }
        }

        fun acceptMember (userId: String, teamId: String, context: Context) {
            TeamService.acceptTeamMember(userId, teamId)
                .subscribeOn(Schedulers.io())
                .subscribe { success ->
                    var dialog = AlertDialog.Builder(context)
                    when (success) {
                        true -> {
                            dialog.setTitle(context.getString(R.string.success))
                                .setMessage(context.getString(R.string.team_member_request_success))
                                .setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                                    dialog.dismiss()
                                    onAcceptCompletion(true)
                                }
                                .show()
                        }
                        false -> {
                            dialog.setTitle(context.getString(R.string.error))
                                .setMessage(context.getString(R.string.team_member_request_failed))
                                .setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                                    dialog.dismiss()
                                    onAcceptCompletion(false)
                                }
                                .show()
                        }
                    }
                }
                .run {  }
        }

        fun declineMember (userId: String, teamId: String, context: Context) {
            TeamService.declineTeamMember(userId, teamId)
                .subscribeOn(Schedulers.io())
                .subscribe { success ->
                    var dialog = AlertDialog.Builder(context)
                    when (success) {
                        true -> {
                            dialog.setTitle(context.getString(R.string.success))
                                .setMessage(context.getString(R.string.team_member_request_decline_success))
                                .setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                                    dialog.dismiss()
                                    onAcceptCompletion(true)
                                }
                                .show()
                        }
                        false -> {
                            dialog.setTitle(context.getString(R.string.error))
                                .setMessage(context.getString(R.string.team_member_request_decline_failed))
                                .setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                                    dialog.dismiss()
                                    onAcceptCompletion(false)
                                }
                                .show()
                        }
                    }
                }
                .run {  }
        }
    }
}