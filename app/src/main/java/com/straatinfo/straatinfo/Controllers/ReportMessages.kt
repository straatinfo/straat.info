package com.straatinfo.straatinfo.Controllers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import com.straatinfo.straatinfo.Adapters.MessageAdapter
import com.straatinfo.straatinfo.Models.Message
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.MessageService
import io.reactivex.schedulers.Schedulers
import io.socket.client.Socket
import io.socket.emitter.Emitter

import kotlinx.android.synthetic.main.activity_report_messages.*
import org.json.JSONObject

class ReportMessages : AppCompatActivity() {

    lateinit var adapter: MessageAdapter
    var socket: Socket? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_report_messages)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        socket = App.socket

        supportActionBar?.title = "CHAT"

        val conversationId = intent.getStringExtra("CONVERSATION_ID")
        val chatTitle = intent.getStringExtra("CHAT_TITLE")
        if (chatTitle != null) {
            supportActionBar?.title = chatTitle
        }
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

        if (socket != null) {
            socket!!
                .on("new-message", onSendMessage)
                .on("send-message-v2", onGetMyMessage)
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
        finish()
        return super.onOptionsItemSelected(item)
    }


    private val onSendMessage = Emitter.Listener { args ->
        Log.d("RECEIVING_MESSAGE", args.toString())
        runOnUiThread {
            Log.d("MESSAGE", args.joinToString())

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
        }
    }

    private val onGetMyMessage = Emitter.Listener { args ->
        Log.d("RECEIVING_MESSAGE", args.toString())
        runOnUiThread {
            Log.d("MESSAGE", args.joinToString())
            addMessageTxtBox.text.clear()
            this.dismissKeyboard()

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


    private fun emitSendMessage (userId: String, conversationId: String, text: String) {
        if (socket != null) {
            val data = JSONObject()
            data.put("user", userId)
            data.put("_id", userId)
            data.put("_conversation", conversationId)
            data.put("text", text)

            socket!!.emit("send-message-v2", data)
        }
    }

    fun sendMessage (view: View) {
        val text = addMessageTxtBox.text.toString()
        val user = User()
        val conversationId = intent.getStringExtra("CONVERSATION_ID")

        if (user.id != null && conversationId != null && text != "") {
            this.emitSendMessage(user.id!!, conversationId, text)
        }
    }

    fun Activity.dismissKeyboard () {
        val inputMethodManager = getSystemService( Context.INPUT_METHOD_SERVICE ) as InputMethodManager
        if( inputMethodManager.isAcceptingText && this.currentFocus != null)
            inputMethodManager.hideSoftInputFromWindow( this.currentFocus.windowToken, /*flags:*/ 0)
    }
}
