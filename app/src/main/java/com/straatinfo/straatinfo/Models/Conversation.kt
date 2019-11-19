package com.straatinfo.straatinfo.Models

import org.json.JSONObject

class Conversation {
    var id: String? = null
    var type: String? = null
    var messagePreview: JSONObject? = null
    var chatMate: JSONObject? = null
    var chatName: String? = null
    var unreadMessageCount: Int = 0
    var profilePicUrl: String? = null


    var conversationJson: JSONObject? = null
    constructor()
    constructor(jsonObject: JSONObject) {
        this.id = if (jsonObject.has("_id")) jsonObject.getString("_id") else null
        this.type = if (jsonObject.has("type")) jsonObject.getString("type") else null
        this.messagePreview = if (jsonObject.has("messagePreview")) jsonObject.getJSONObject("messagePreview") else null
        this.chatMate = if (jsonObject.has("chatMate")) jsonObject.getJSONObject("chatMate") else null

        this.unreadMessageCount = if (jsonObject.has("unreadMessageCount")) jsonObject.getInt("unreadMessageCount") else 0

        if (jsonObject.has("chatMate")) {
            val chatMate = if (jsonObject.has("chatMate")) jsonObject.getJSONObject("chatMate") else null
            if (chatMate != null) {
                this.chatName = if (chatMate.has("username")) chatMate.getString("username") else null
                val profilePic = if (chatMate.has("_profilePic")) chatMate.getJSONObject("_profilePic") as JSONObject else null
                if (profilePic != null) {
                    this.profilePicUrl = if (profilePic.has("secure_url")) profilePic.getString("secure_url") else null
                }
            }
        }

        if (jsonObject.has("type") && jsonObject.getString("type") == "TEAM") {
            this.profilePicUrl = if (jsonObject.has("profilePicUrl")) jsonObject.getString("profilePicUrl") else null
            this.chatName = if (jsonObject.has("chatName")) jsonObject.getString("chatName") else null
        }
    }
}