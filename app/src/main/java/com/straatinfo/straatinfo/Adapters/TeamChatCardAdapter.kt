package com.straatinfo.straatinfo.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.straatinfo.straatinfo.Models.Conversation
import com.straatinfo.straatinfo.Models.Report
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Utilities.TZ_ZULU
import com.straatinfo.straatinfo.Utilities.WINDOW_INFO_REPORT_DATE_FORMAT
import java.text.SimpleDateFormat

class TeamChatCardAdapter (val context: Context, val conversations: MutableList<Conversation>, val conversationClick: (Conversation) -> Unit): RecyclerView.Adapter<TeamChatCardAdapter.Holder> () {

    override fun onCreateViewHolder (parent: ViewGroup, p1: Int): TeamChatCardAdapter.Holder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.adapter_team_chat_card, parent, false)

        return Holder(view, conversationClick)
    }

    override fun getItemCount(): Int {
        return conversations.count()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bindCategory(conversations[position], context)
    }

    inner class Holder (itemView: View, val itemClick: (Conversation) -> Unit): RecyclerView.ViewHolder(itemView) {
        var teamProfileImg = itemView?.findViewById<ImageView>(R.id.team_profile_img)
        var teamChatName = itemView?.findViewById<TextView>(R.id.team_chat_name)
        var chatPreview = itemView?.findViewById<TextView>(R.id.chat_preview_txt)
        var date = itemView?.findViewById<TextView>(R.id.date_txt)
        var messageCount = itemView?.findViewById<TextView>(R.id.new_message_count_txt)
        fun bindCategory (conversation: Conversation, context: Context) {

            teamChatName.text = conversation.chatName

            if (conversation.profilePicUrl != null) {
                Picasso.get().load(conversation.profilePicUrl).into(teamProfileImg)
            } else {
                teamProfileImg.setImageResource(R.drawable.ic_logo)
            }



            if (conversation.messagePreview != null) {
                var body = if (conversation.messagePreview!!.has("body")) conversation.messagePreview?.getString("body") else null
                var createdAt = if (conversation.messagePreview!!.has("createdAt")) conversation.messagePreview?.getString("createdAt") else null
                var author = if (conversation.messagePreview!!.has("author")) conversation.messagePreview?.getString("author") else null
                val user = User()

                if (body != null && author != null) {
                    if (conversation.type == "PRIVATE") {
                        if (author == user.username) {
                            chatPreview.text = "You: $body"
                        } else {
                            chatPreview.text = "$body"
                        }
                    } else { // TEAM
                        if (author == user.username) {
                            chatPreview.text = "You: $body"
                        } else {
                            chatPreview.text = "$author: $body"
                        }
                    }
                }

                if (createdAt != null) {
                    val format = SimpleDateFormat(TZ_ZULU)
                    val dateFormat = SimpleDateFormat(WINDOW_INFO_REPORT_DATE_FORMAT)
                    date.text = dateFormat.format(format.parse(createdAt!!))
                }
            }

            if (conversation.unreadMessageCount > 0) {
                messageCount.text = conversation.unreadMessageCount.toString()
                messageCount.visibility = View.VISIBLE
            } else {
                messageCount.visibility = View.INVISIBLE
            }

            itemView.setOnClickListener {
                itemClick(conversation)
            }
        }
    }

}