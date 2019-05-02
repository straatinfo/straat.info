package com.straatinfo.straatinfo.Models

import org.json.JSONObject

class Host {
    var error: String? = null
    var id: String? = null
    var lat : Double? = null
    var long : Double? = null
    var email : String? = null
    var hostName : String? = null
    var username: String? = null
    var streetName: String? = null
    var city: String? = null
    var country: String? = null
    var postalCode: String? = null
    var phoneNumber: String? = null
    var isVolunteer: Boolean? = null
    var language: String? = "en"
    var hostJsonObject: JSONObject? = null

    constructor()

    constructor(hostData: String) {
        val jsonObject = JSONObject(hostData)
        this.id = jsonObject.getString("id")
        this.lat = jsonObject.getDouble("lat")
        this.long = jsonObject.getDouble("long")
        this.email = jsonObject.getString("email")
        this.hostName = jsonObject.getString("hostName")
        this.username = jsonObject.getString("username")
        this.streetName = jsonObject.getString("streetName")
        this.city = jsonObject.getString("city")
        this.country = jsonObject.getString("country")
        this.postalCode = jsonObject.getString("postalCode")
        this.phoneNumber = jsonObject.getString("phoneNumber")
        this.isVolunteer = jsonObject.getBoolean("isVolunteer")
        this.language = jsonObject.getString("language")
    }
    constructor(hostJsonObject: JSONObject) {
        this.hostJsonObject = hostJsonObject
        this.id = hostJsonObject.getString("_id")
        this.lat = hostJsonObject.getDouble("lat")
        this.long = hostJsonObject.getDouble("long")
        this.email = hostJsonObject.getString("email")
        this.hostName = hostJsonObject.getString("hostName")
        this.username = hostJsonObject.getString("username")
        this.streetName = hostJsonObject.getString("streetName")
        this.city = hostJsonObject.getString("city")
        this.country = hostJsonObject.getString("country")
        this.postalCode = hostJsonObject.getString("postalCode")
        this.phoneNumber = hostJsonObject.getString("phoneNumber")
        this.isVolunteer = hostJsonObject.getBoolean("isVolunteer")
        this.language = hostJsonObject.getString("language")
    }


    fun getJsonObject (): JSONObject {
        var hostJson: JSONObject = JSONObject()
        hostJson.put("id", this.id)
        hostJson.put("lat", this.lat)
        hostJson.put("long", this.long)
        hostJson.put("email", this.email)
        hostJson.put("hostName", this.hostName)
        hostJson.put("username", this.username)
        hostJson.put("streetName", this.streetName)
        hostJson.put("city", this.city)
        hostJson.put("country", this.country)
        hostJson.put("postalCode", this.postalCode)
        hostJson.put("phoneNumber", this.phoneNumber)
        hostJson.put("isVolunteer", this.isVolunteer)
        hostJson.put("language", this.language)

        return hostJson
    }
}