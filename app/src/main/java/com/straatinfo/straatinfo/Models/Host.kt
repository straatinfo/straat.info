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
        this.username = if (hostJsonObject.has("username")) hostJsonObject.getString("username") else ""
        this.streetName = if (hostJsonObject.has("streetName")) hostJsonObject.getString("streetName") else ""
        this.city = if (hostJsonObject.has("city")) hostJsonObject.getString("city") else ""
        this.country = if (hostJsonObject.has("country")) hostJsonObject.getString("country") else ""
        this.postalCode = if (hostJsonObject.has("postalCode")) hostJsonObject.getString("postalCode") else ""
        this.phoneNumber = if (hostJsonObject.has("phoneNumber")) hostJsonObject.getString("phoneNumber") else ""
        this.isVolunteer = if (hostJsonObject.has("isVolunteer")) hostJsonObject.getBoolean("isVolunteer") else null
        this.language = if (hostJsonObject.has("language")) hostJsonObject.getString("language") else ""
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