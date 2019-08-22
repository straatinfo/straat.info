package com.straatinfo.straatinfo.Services

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Utilities.*
import io.reactivex.Observable
import org.json.JSONArray
import org.json.JSONObject

object MessageService {
    val TAG = "MESSAGE_SERVICE"
    val headers = HashMap<String, String>()
    var errorMessage: String? = null

    fun getMessages (reporterId: String, conversationId: String, keyword: String): Observable<JSONArray> {
        errorMessage = null
        return Observable.create {
            val url = "$MESSAGES?_conversation=$conversationId&_reporter=$reporterId&keyword=$keyword"
            // _conversation=5d37a9218e53e90015209bbe&_reporter=5c63e92035086200156f93e0&keyword=all
            val getMessagesRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                var data = JSONArray()
                Log.d(TAG, response.toString())
                if (response.has("payload")) {
                    data = response.getJSONArray("payload")
                }

                it.onNext(data)
            }, Response.ErrorListener { error ->
                // Log.d(TAG, error.localizedMessage)
                it.onNext(JSONArray())
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", App.prefs.token)
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(getMessagesRequest)
        }
    }

    fun createPrivateConversation (authorId: String, chatee: String): Observable<JSONObject> {
        return Observable.create {
            val jsonBody = JSONObject()
            jsonBody.put("_author", authorId)
            jsonBody.put("_chatee", chatee)
            val requestBody = jsonBody.toString()
            val url = CONVERSATION_V2 + "?type=PRIVATE"
            val createConvoRequest = object: JsonObjectRequest(Method.POST, url, null, Response.Listener { response ->
                Log.d("CONVERSATION", response.toString())
                if (response.has("payload")) {
                    val conversation = response.getJSONObject("payload")
                    it.onNext(conversation)
                } else {
                    it.onNext(JSONObject())
                }

            }, Response.ErrorListener { error ->
                it.onNext(JSONObject())
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(createConvoRequest)
        }
    }

    fun readUnreadMessages (conversationId: String, userId: String): Observable<Boolean> {
        val url = "$UNREAD_MESSAGE/$conversationId/$userId"

        return Observable.create {
            val readUnreadMessagesRequest = object: JsonObjectRequest(Method.DELETE, url, null, Response.Listener { response ->
                it.onNext(true)
            }, Response.ErrorListener { error ->
                it.onNext(false)
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(readUnreadMessagesRequest)
        }
    }

    fun getUnreadMessagesGroupByReports (userId: String): Observable<JSONObject> {
        val url = "$GET_ALL_UNREAD_MESSAGES_COUNT/$userId"

        return Observable.create {
            val getUnreadMessageCountReq = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                if (response.has("a") && response.has("b")) it.onNext(response) else it.onNext(JSONObject())
            }, Response.ErrorListener { error ->
                it.onNext(JSONObject())
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(getUnreadMessageCountReq)
        }
    }

}