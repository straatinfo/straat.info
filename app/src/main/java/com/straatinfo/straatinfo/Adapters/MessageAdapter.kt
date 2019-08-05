package com.straatinfo.straatinfo.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.straatinfo.straatinfo.Models.Message
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R

private const val VIEW_TYPE_MY_MESSAGE = 1
private const val VIEW_TYPE_OTHER_MESSAGE = 2

class MessageAdapter (val context: Context, val messages: MutableList<Message>, val messageClick: (Message) -> Unit): RecyclerView.Adapter<MessageViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == VIEW_TYPE_MY_MESSAGE) {
            MyMessageHolder(LayoutInflater.from(context).inflate(R.layout.adapter_view_my_message_box, parent, false), messageClick)
        } else {
            OtherMessageHolder(LayoutInflater.from(context).inflate(R.layout.adapter_view_other_users_message_box, parent, false), messageClick)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        val user = User()
        if (message.userId != null && message.userId == user.id) {
            return VIEW_TYPE_MY_MESSAGE
        } else {
            return VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder?.bind(messages[position], context)
    }

    inner class MyMessageHolder(itemView: View, itemClick: (Message) -> Unit): MessageViewHolder (itemView, context) {
        val myTextBox = itemView?.findViewById<TextView>(R.id.myMessageTxtBox)
        val myDateTextBox = itemView?.findViewById<TextView>(R.id.myDateTimeTextBox)
        override fun bind (message: Message, context: Context) {
            myTextBox.text = message.body
            myDateTextBox.text = message.getDateTime()
        }
    }

    inner class OtherMessageHolder(itemView: View, itemClick: (Message) -> Unit): MessageViewHolder (itemView, context) {
        val userInitialAvatar = itemView?.findViewById<TextView>(R.id.userInitialAvatar)
        val messageTxtBox = itemView?.findViewById<TextView>(R.id.messageTxtBox)
        val dateTxtBox = itemView?.findViewById<TextView>(R.id.dateTimeTextBox)
        val usernameTxtBox = itemView?.findViewById<TextView>(R.id.usernameTxtBox)
        override fun bind (message: Message, context: Context) {
            userInitialAvatar.text = if (message.username != null) message.username!![0].toString().capitalize() else "U"
            messageTxtBox.text = message.body
            dateTxtBox.text = message.getDateTime()
            usernameTxtBox.text = if (message.username != null) message.username else "Anonymous User"
        }
    }
}

open class MessageViewHolder (view: View, context: Context) : RecyclerView.ViewHolder(view) {
    open fun bind(message:Message, context: Context) {}
}