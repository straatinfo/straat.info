package com.straatinfo.straatinfo.Controllers

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.straatinfo.straatinfo.Adapters.MessageAdapter
import com.straatinfo.straatinfo.Models.Message
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.MessageService
import com.straatinfo.straatinfo.Utilities.BROADCAST_NEW_MESSAGE_RECEIVED
import io.reactivex.schedulers.Schedulers
import io.socket.client.Socket
import io.socket.emitter.Emitter

import kotlinx.android.synthetic.main.activity_report_messages.*
import org.json.JSONObject

class ReportMessagesActivity : AppCompatActivity() {

    lateinit var adapter: MessageAdapter
    var socket: Socket? = null
    var convoId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_report_messages)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        socket = App.socket

        supportActionBar?.title = "CHAT"

        val conversationId = intent.getStringExtra("CONVERSATION_ID")
        val chatTitle = intent.getStringExtra("CHAT_TITLE")
        this.convoId = conversationId
        if (chatTitle != null) {
            supportActionBar?.title = chatTitle
        }
        if (conversationId != null) {
            MessageService.readUnreadMessages(conversationId, User().id!!)
                .subscribeOn(Schedulers.io())
                .subscribe {  }
                .run {  }
            this.loadMessages(conversationId) { messages ->
                adapter = MessageAdapter(this, messages) { message ->

                }

                reportMessagesRecyclerView.adapter = adapter
                val layoutManager = LinearLayoutManager(this)
                    .apply {
                        stackFromEnd = true
                        reverseLayout = false
                    }
                reportMessagesRecyclerView.layoutManager = layoutManager
            }
        }

        if (socket != null) {
//            socket!!
//                .on("new-message", onSendMessage)
//                .on("send-message-v2", onGetMyMessage)
        } else {

            val user = User()
            App.socket = App.connectToSocket(user)
//            val dialog = AlertDialog.Builder(this)
//                .setTitle(getString(R.string.error))
//                .setMessage("Cannot connect to socket, please check network")
//
//            dialog.show()
            finish()
        }





        val sendMessageBtn = findViewById<Button>(R.id.send_message_btn)
        val textBox = findViewById<TextView>(R.id.addMessageTxtBox)
        sendMessageBtn.isEnabled = textBox.text.toString() != ""
        textBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                sendMessageBtn.isEnabled = textBox.text.toString() != ""
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                sendMessageBtn.isEnabled = textBox.text.toString() != ""
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                sendMessageBtn.isEnabled = textBox.text.toString() != ""
            }

        })

        LocalBroadcastManager.getInstance(this).registerReceiver(this.newMessageDataReceiver, IntentFilter(
            BROADCAST_NEW_MESSAGE_RECEIVED))

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when (item?.itemId) {
//            R.id.my_reports -> {
//                NavUtils.navigateUpFromSameTask(this)
//                return true
//            }
//        }
//        val mainActivity = Intent(this, MainActivity::class.java)
//        startActivity(mainActivity)
        this.convoId = null
        finish()
        return super.onOptionsItemSelected(item)
    }


    private val onSendMessage = Emitter.Listener { args ->
        Log.d("RECEIVING_MESSAGE", args.toString())
        runOnUiThread {
            Log.d("MESSAGE", args.joinToString())

            val conversationId = intent.getStringExtra("CONVERSATION_ID")
            this.convoId = conversationId
            if (conversationId != null) {
                this.loadMessages(conversationId) { messages ->
                    adapter = MessageAdapter(this, messages) { message ->

                    }

                    reportMessagesRecyclerView.adapter = adapter
                    val layoutManager = LinearLayoutManager(this)
                        .apply {
                            stackFromEnd = true
                            reverseLayout = false
                        }
                    reportMessagesRecyclerView.layoutManager = layoutManager
                }
            }
        }
    }

    private val onGetMyMessage = Emitter.Listener { args ->
        Log.d("RECEIVING_MESSAGE", args.toString())
        runOnUiThread {
            Log.d("MESSAGE", args.joinToString())
            addMessageTxtBox.text.clear()
            // this.dismissKeyboard()

            val conversationId = intent.getStringExtra("CONVERSATION_ID")
            if (conversationId != null) {
                this.loadMessages(conversationId) { messages ->
                    adapter = MessageAdapter(this, messages) { message ->

                    }

                    reportMessagesRecyclerView.adapter = adapter
                    val layoutManager = LinearLayoutManager(this)
                        .apply {
                            stackFromEnd = true
                            reverseLayout = false
                        }
                    reportMessagesRecyclerView.layoutManager = layoutManager
                }
            }

//            MessageService.readUnreadMessages(conversationId, User().id!!)
//                .subscribeOn(Schedulers.io())
//                .subscribe {  }
//                .run {  }
        }
    }

    private fun loadMessages (conversationId: String, completion: (MutableList<Message>) -> Unit) {

        var messages = mutableListOf<Message>()
        val user = User()

        MessageService.getMessages(user.id!!, conversationId, "all")
            .subscribeOn(Schedulers.io())
            .subscribe {
                for (i in 0 until it.length()) {
                    messages.add(i, Message(it[i] as JSONObject))
                }

                completion(messages)
            }
            .run {  }
    }


    private fun emitSendMessage (userId: String, conversationId: String, reportId: String?, teamId: String?, type: String, text: String) {

        if (socket != null) {
            val data = JSONObject()
            data.put("user", userId)
            data.put("_id", userId)
            data.put("_conversation", conversationId)
            data.put("text", text)
            if (reportId != null) data.put("_report", reportId)
            if (teamId != null) data.put("_team", teamId)
            data.put("type", type)

            Log.d("DATA_VALUE", data.toString())

            socket!!.emit("send-message-v2", data)
        }
    }

    private fun broadcastMessage (userId: String, conversationId: String, reportId: String?, teamId: String?, text: String, type: String, cb: (Boolean) -> Unit) {
        var messageDetails = JSONObject()
        messageDetails.put("user", userId)
        messageDetails.put("_conversation", conversationId)
        messageDetails.put("_report", reportId)
        messageDetails.put("text", text)
        messageDetails.put("type", type)
        messageDetails.put("_team", teamId)
        MessageService.sendMessage(messageDetails)
            .subscribeOn(Schedulers.io())
            .subscribe {
                cb(it)
            }
            .run {  }
    }

    private fun reloadMessages () {
        if (this.convoId != null) {
            MessageService.readUnreadMessages(this.convoId!!, User().id!!)
                .subscribeOn(Schedulers.io())
                .subscribe {  }
                .run {  }

            addMessageTxtBox.text.clear()
            this.loadMessages(this.convoId!!) { messages ->
                adapter = MessageAdapter(this, messages) { message ->

                }

                reportMessagesRecyclerView.adapter = adapter
                val layoutManager = LinearLayoutManager(this)
                    .apply {
                        stackFromEnd = true
                        reverseLayout = false
                    }
                reportMessagesRecyclerView.layoutManager = layoutManager
            }
        }

    }

    private val newMessageDataReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            reloadMessages()

        }
    }

    fun sendMessage (view: View) {
        val sendMessageBtn = findViewById<Button>(R.id.send_message_btn)
        val text = addMessageTxtBox.text.toString()
        val user = User()
        val conversationId = intent.getStringExtra("CONVERSATION_ID")
        val reportId = intent.getStringExtra("REPORT_ID")
        val teamId = intent.getStringExtra("TEAM_ID")
        val type = intent.getStringExtra("TYPE")
        Log.d("CONVERSATION_DETAILS", "CONVERSATION_ID: $conversationId, REPORT_ID: $reportId, TEAM_ID: $teamId, TYPE: $type")
        sendMessageBtn.isEnabled = false

        if (user.id != null && conversationId != null && text != "" && type != null) {
            this.broadcastMessage(user.id!!, conversationId, reportId, teamId, text, type) { success ->
                if (success) {
                    this.reloadMessages()
                    sendMessageBtn.isEnabled = true
                } else {
                    val dialog = AlertDialog.Builder(this)
                        .setTitle(getString(R.string.error))
                        .setMessage("Unable to send message, please check network")
                        .setOnDismissListener {
                            sendMessageBtn.isEnabled = true
                        }
                    dialog.show()
                }
            }
        }
    }

    fun Activity.dismissKeyboard () {
        val inputMethodManager = getSystemService( Context.INPUT_METHOD_SERVICE ) as InputMethodManager
        if( inputMethodManager.isAcceptingText && this.currentFocus != null)
            inputMethodManager.hideSoftInputFromWindow( this.currentFocus.windowToken, /*flags:*/ 0)
    }
}
