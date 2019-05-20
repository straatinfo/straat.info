package com.straatinfo.straatinfo.Models

import org.json.JSONObject

class Team {
    var id: String? = null
    var email: String? = null
    var name: String? = null
    var profilePic: JSONObject? = null

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

    fun toJson () : JSONObject {
        val json = JSONObject()

        json.put("id", this.id)
        json.put("_id", this.id)
        json.put("email", this.email)
        json.put("name", this.name)
        json.put("profilePic", this.profilePic)
        json.put("_profilePic", this.profilePic)

        return json
    }
}