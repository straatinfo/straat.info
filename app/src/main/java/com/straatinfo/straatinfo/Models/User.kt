package com.straatinfo.straatinfo.Models

import org.json.JSONObject

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

    var isNotification: Boolean? = null
    var vibrate: Boolean? = null
    var sound: Boolean? = null
    var radius: Double? = null

    var team_id: String? = null
    var team_name: String? = null
    var team_email: String? = null
    var team_is_approved: Boolean? = null

    var host_id: String? = null
    var isVolunteer: Boolean? = null
    var host_name: String? = null

    // active design
//    var colorOne: String? = null
//    var colorTwo: String? = null
//    var colorThree: String? = null
//    var profileImageUrl: String? = null

    var activeDesign: Design? = null

    var userJsonData: JSONObject? = null


    constructor(fetchingError: String?) {
        this.error = fetchingError
    }

    constructor(userJsonData: JSONObject) {
        val userJson = userJsonData.getJSONObject("user")
        val settingJson = userJsonData.getJSONObject("setting")
        val designJson = userJsonData.getJSONObject("_activeDesign")
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
        this.isNotification = settingJson.getBoolean("isNotification")
        this.vibrate = settingJson.getBoolean("vibrate")
        this.sound = settingJson.getBoolean("sound")
        this.radius = settingJson.getDouble("radius")

        val hostJson = userJson.getJSONObject("_host")
        this.host_id = hostJson.getString("_id")
        this.host_name = hostJson.getString("hostName")

        val designId = designJson.getString("_id")
        val colorOne = designJson.getString("colorOne")
        val colorTwo = designJson.getString("colorTwo")
        val colorThree = designJson.getString("colorThree")
        val hostId = designJson.getString("_host")
        val profilePicJson = designJson.getJSONObject("_profilePic")

        val logoUrl = profilePicJson.getString("secure_url")
        val designName = designJson.getString("designName")

        val design = Design(designId, hostId, colorOne, colorTwo, colorThree, logoUrl, designName)

        this.activeDesign = design
    }

    fun toJson (): JSONObject {
        if (this.userJsonData != null) {
            return this.userJsonData!!
        } else {
            return JSONObject() // an empty user data
        }
    }
}