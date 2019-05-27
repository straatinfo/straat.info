package com.straatinfo.straatinfo.Models

import org.json.JSONObject

class TeamMember {
    var userId: String? = null
    var username: String? = null
    var userEmail: String? = null
    var userFname: String? = null
    var userLname: String? = null

    var teamId: String? = null
    var teamName: String? = null
    var teamEmail: String? = null
    var teamIsApproved: Boolean? = null
    var teamIsVolunteer: Boolean? = null

    constructor()
    constructor(teamMember: JSONObject) {
        if (teamMember.has("_user")) {
            val user = teamMember.getJSONObject("_user")
            this.userId = if (user.has("_id")) user.getString("_id") else null
            this.username = if (user.has("username")) user.getString("username") else null
            this.userEmail = if (user.has("email")) user.getString("email") else null
            this.userFname = if (user.has("fname")) user.getString("fname") else null
            this.userLname = if (user.has("lname")) user.getString("lname") else null

        }

        if (teamMember.has("_team")) {
            val team = teamMember.getJSONObject("_team")
            this.teamId = if (team.has("_id")) team.getString("_id") else null
            this.teamName = if (team.has("teamName")) team.getString("teamName") else null
            this.teamEmail = if (team.has("teamEmail")) team.getString("teamEmail") else null
            this.teamIsApproved = if (team.has("isApproved")) team.getBoolean("isApproved") else null
            this.teamIsVolunteer = if (team.has("isVolunteer")) team.getBoolean("isVolunteer") else null
        }
    }

}