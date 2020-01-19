package com.straatinfo.straatinfo.Models

import android.util.Log
import org.json.JSONObject

class TeamMember {
    var userId: String? = null
    var username: String? = null
    var userEmail: String? = null
    var userFname: String? = null
    var userLname: String? = null
    var userHouseNumber: String? = null
    var userStreetName: String? = null
    var userCity: String? = null
    var userPostCode: String? = null
    var userCountry: String? = null
    var userPhoneNumber: String? = null

    var userProfilePicUrl: String? = null

    var teamId: String? = null
    var teamName: String? = null
    var teamEmail: String? = null
    var teamIsApproved: Boolean? = null
    var teamIsVolunteer: Boolean? = null

    var teamMemberJson: JSONObject? = null

    constructor(teamMember: JSONObject) {
        this.teamMemberJson = teamMember
        Log.d("TEAM_MEMBER", teamMember.toString())
        if (teamMember.has("_user")) {
            val user = teamMember.getJSONObject("_user")
            this.userId = if (user.has("_id")) user.getString("_id") else null
            this.username = if (user.has("username")) user.getString("username") else null
            this.userEmail = if (user.has("email")) user.getString("email") else null
            this.userFname = if (user.has("fname")) user.getString("fname") else null
            this.userLname = if (user.has("lname")) user.getString("lname") else null
            this.userHouseNumber = if (user.has("houseNumber")) user.getString("houseNumber") else null
            this.userStreetName = if (user.has("streetName")) user.getString("streetName") else null
            this.userCity = if (user.has("city")) user.getString("city") else null
            this.userPostCode = if (user.has("postalCode")) user.getString("postalCode") else null
            this.userCountry = if (user.has("country")) user.getString("country") else null
            this.userPhoneNumber = if (user.has("phoneNumber")) user.getString("phoneNumber") else null
            if (user.has("_profilePic")) {
                val profilePicJson = user.getJSONObject("_profilePic")
                if (profilePicJson.has("secure_url")) this.userProfilePicUrl = profilePicJson.getString("secure_url")
            }
        }

        if (teamMember.has("_team")) {
            try {
                val team = teamMember.getJSONObject("_team")
                this.teamId = if (team.has("_id")) team.getString("_id") else null
                this.teamName = if (team.has("teamName")) team.getString("teamName") else null
                this.teamEmail = if (team.has("teamEmail")) team.getString("teamEmail") else null
                this.teamIsApproved = if (team.has("isApproved")) team.getBoolean("isApproved") else null
                this.teamIsVolunteer = if (team.has("isVolunteer")) team.getBoolean("isVolunteer") else null
            } catch (e: Exception) {
                // ignore errors
            }
        }
    }

}