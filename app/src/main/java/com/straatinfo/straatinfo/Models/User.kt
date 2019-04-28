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

    // active design
    var colorOne: String? = null
    var colorTwo: String? = null
    var colorThree: String? = null
    var profileImageUrl: String? = null


    constructor(fetchingError: String?) {
        this.error = fetchingError
    }

    constructor(userJsonData: JSONObject) {
        this.id = userJsonData.getString("_id")
        this.firstname = userJsonData.getString("fname")
        this.lastname = userJsonData.getString("lname")
        this.email = userJsonData.getString("email")
        this.houseNumber = userJsonData.getString("houseNumber")
        this.postalCode = userJsonData.getString("postalCode")
        this.username = userJsonData.getString("username")
        this.phoneNumber = userJsonData.getString("phoneNumber")
        this.streetName = userJsonData.getString("streetName")
        this.city = userJsonData.getString("city")
        this.gender = userJsonData.getString("gender")
        this.isNotification = userJsonData.getBoolean("isNotification")
        this.vibrate = userJsonData.getBoolean("vibrate")
        this.sound = userJsonData.getBoolean("sound")
        this.radius = userJsonData.getDouble("radius")
    }
}

class Setting {

}

class ActiveDesign {

}