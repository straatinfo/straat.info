package com.straatinfo.straatinfo.Models

import org.json.JSONObject

class Team {
    var id: String? = null
    var email: String? = null
    var name: String? = null
    var profilePic: JSONObject? = null
    var isVolunteer: Boolean? = null
    var isApproved: Boolean? = null
    var conversationJson: JSONObject? = null
    var conversationId: String? = null
    var unreadMessageCount: Int = 0


    constructor(id: String, email: String, name: String) {
        this.id = id
        this.email = email
        this.name = name
    }

    constructor(id: String, email: String, name: String, profilePic: JSONObject) {
        this.id = id
        this.email = email
        this.name = name
        this.profilePic = profilePic
    }

    constructor(teamJson: JSONObject) {
        if (teamJson.has("_id")) this.id = teamJson.getString("_id")
        if (teamJson.has("teamName")) this.name = teamJson.getString("teamName")
        if (teamJson.has("teamEmail")) this.email = teamJson.getString("teamEmail")
        if (teamJson.has("_profilePic")) this.profilePic = teamJson.getJSONObject("_profilePic")
        if (teamJson.has("isVolunteer")) this.isVolunteer = teamJson.getBoolean("isVolunteer")
        if (teamJson.has("isApproved")) this.isApproved = teamJson.getBoolean("isApproved")
        if (teamJson.has("_conversation")) this.conversationJson = teamJson.getJSONObject("_conversation")
        if (conversationJson != null && conversationJson!!.has("_id")) {
            this.conversationId = conversationJson!!.getString("_id")

            if (conversationJson!!.has("unreadMessageCount")) {
                this.unreadMessageCount = conversationJson!!.getInt("unreadMessageCount")
            }
        }
    }

    fun toJson () : JSONObject {
        val json = JSONObject()

        json.put("id", this.id)
        json.put("_id", this.id)
        json.put("email", this.email)
        json.put("teamName", this.name)
        json.put("teamEmail", this.email)
        json.put("name", this.name)
        json.put("profilePic", this.profilePic)
        json.put("_profilePic", this.profilePic)
        json.put("isVolunteer", this.isVolunteer)
        json.put("isApproved", this.isApproved)

        return json
    }
}