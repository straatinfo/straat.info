package com.straatinfo.straatinfo.Models

import android.util.Log
import com.straatinfo.straatinfo.Controllers.App
import org.json.JSONObject
import java.lang.Exception

class User {
    var error: String? = null
    var id: String? = null
    var firstname: String? = null
    var lastname: String? = null
    var email: String? = null
    var username: String? = null
    var houseNumber: String? = null
    var postalCode: String? = null
    var phoneNumber: String? = null
    var streetName: String? = null
    var city: String? = null
    var gender: String? = null

    var profilePic: JSONObject? = null

    var isNotification: Boolean? = null
    var vibrate: Boolean? = null
    var sound: Boolean? = null
    var radius: Double? = null

    var team_id: String? = null
    var team_name: String? = null
    var team_email: String? = null
    var team_is_approved: Boolean? = null
    var team_is_volunteer: Boolean? = null

    var host_id: String? = null
    var isVolunteer: Boolean? = null
    var host_name: String? = null
    var host_long: Double? = null
    var host_lat: Double? = null
    var host: Host? = null

    // active design
//    var colorOne: String? = null
//    var colorTwo: String? = null
//    var colorThree: String? = null
//    var profileImageUrl: String? = null

    var activeDesign: Design? = null

    var userJsonData: JSONObject? = null

    constructor() {
        try {
            var userJsonData = JSONObject(App.prefs.userData)
            this.fromJson(userJsonData)
        }
         catch (e: Exception) {
             Log.d("USER_ERROR", e.localizedMessage)
         }
    }


    constructor(fetchingError: String?) {
        this.error = fetchingError
    }

    constructor(userJsonData: JSONObject) {
        this.fromJson(userJsonData)
    }

    fun toJson (): JSONObject {
        if (this.userJsonData != null) {
            return this.userJsonData!!
        } else {
            return JSONObject() // an empty user data
        }
    }

    fun fromJson (userJsonData: JSONObject) {

        val userJson = userJsonData.getJSONObject("user")
        this.userJsonData = userJsonData
        this.id = userJson.getString("_id")
        this.firstname = userJson.getString("fname")
        this.lastname = userJson.getString("lname")
        this.email = userJson.getString("email")
        this.houseNumber = userJson.getString("houseNumber")
        this.postalCode = userJson.getString("postalCode")
        this.username = userJson.getString("username")
        this.phoneNumber = userJson.getString("phoneNumber")
        this.streetName = userJson.getString("streetName")
        this.city = userJson.getString("city")
        this.gender = userJson.getString("gender")
        this.isVolunteer = userJson.getBoolean("isVolunteer")

        if (userJsonData.has("setting")) {
            val settingJson = userJsonData.getJSONObject("setting")
            this.isNotification = settingJson.getBoolean("isNotification")
            this.vibrate = settingJson.getBoolean("vibrate")
            this.sound = settingJson.getBoolean("sound")
            this.radius = settingJson.getDouble("radius")
        }

        if (userJson.has("_host")) {
            val hostJson = userJson.getJSONObject("_host")
            this.host_id = hostJson.getString("_id")
            this.host_name = hostJson.getString("hostName")
            this.host_long = hostJson.getDouble("long")
            this.host_lat = hostJson.getDouble("lat")
            try {
                this.host = Host(hostJson)
            }
            catch (e: Exception) {
                Log.d("UESR", e.localizedMessage)
            }
        }

        if (userJsonData.has("_activeDesign") && userJsonData.getString("_activeDesign") != "null") {
            Log.d("_activeDesign", userJsonData.getJSONObject("_activeDesign").toString())
            val designJson = userJsonData.getJSONObject("_activeDesign")
            val designId = designJson.getString("_id")
            val colorOne = designJson.getString("colorOne")
            val colorTwo = designJson.getString("colorTwo")
            val colorThree = designJson.getString("colorThree")
            val hostId = designJson.getString("_host")
            if (designJson.has("_profilePic")) {
                val profilePicJson = if (designJson.has("_profilePic")) designJson.getJSONObject("_profilePic") else null
                val logoUrl = profilePicJson?.getString("secure_url")
                val designName = designJson?.getString("designName")

                val design = Design(designId, hostId, colorOne, colorTwo, colorThree, logoUrl, designName!!)

                this.activeDesign = design
            }

        }

        if (userJson.has("_activeTeam")) {
            val activeTeam = userJson.getJSONObject("_activeTeam")
            this.team_email = activeTeam.getString("teamEmail")
            this.team_id = activeTeam.getString("_id")
            this.team_name = activeTeam.getString("teamName")
            this.team_is_approved = activeTeam.getBoolean("isApproved")
            this.team_is_volunteer = activeTeam.getBoolean("isVolunteer")
        }

        if (userJsonData.has("_profilePic")) {
            Log.d("USER_MODEL", "Has photo")
            this.profilePic = userJsonData.getJSONObject("_profilePic")
        }
    }
}