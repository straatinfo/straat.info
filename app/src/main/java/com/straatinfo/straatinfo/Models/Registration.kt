package com.straatinfo.straatinfo.Models

import android.util.Log
import com.straatinfo.straatinfo.Controllers.App
import org.json.JSONObject
import java.lang.Exception

class Registration {
    var gender: String? = null
    var fname: String? = null
    var lname: String? = null
    var usernameInput: String? = null
    var username: String? = null
    var postalCode: String? = null
    var houseNumber:String? = null
    var streetName: String? = null
    var city: String? = null
    var email: String? = null
    var phoneNumber: String? = null
    var tac: Boolean = false
    var isVolunteer: Boolean? = null
    var teamId: String? = null
    var teamName: String? = null
    var teamEmail: String? = null
    var password: String? = null
    var hostId: String? = null

    var teamPhotoJson: JSONObject? = null

    constructor() {
        this.loadInput()
    }

    fun toJson () : JSONObject {
        var json = JSONObject()
        json.put("gender", if(this.gender != null) this.gender!!.toUpperCase() else "")
        json.put("fname", if (this.fname != null) this.fname else "")
        json.put("lname", if (this.lname != null) this.lname else "")
        json.put("usernameInput", if (this.usernameInput != null) this.usernameInput else "")
        json.put("username", if (this.username != null) this.username else "")
        json.put("postalCode", if (this.postalCode != null) this.postalCode else "")
        json.put("houseNumber", if (this.houseNumber != null) this.houseNumber else "")
        json.put("streetName", if (this.streetName != null) this.streetName else "")
        json.put("city", if (this.city != null) this.city else "")
        json.put("email", if (this.email != null) this.email else "")
        json.put("phoneNumber", if (this.phoneNumber != null) this.phoneNumber else "")
        json.put("tac", this.tac)
        json.put("isVolunteer",if (this.isVolunteer != null) this.isVolunteer else "")
        json.put("teamId", if (this.teamId != null) this.teamId else "")
        json.put("teamName", if (this.teamName != null) this.teamName else "")
        json.put("teamEmail", if (this.teamEmail != null) this.teamEmail else "")
        json.put("teamPhotoJson", if (this.teamPhotoJson != null) this.teamPhotoJson else "")
        json.put("password", if (this.password != null) this.password else "")
        json.put("hostId", if (this.hostId != null) this.hostId else "")
        return json
    }

    fun toRequestObject() :JSONObject {
        var json = JSONObject()
        json.put("gender", if(this.gender != null) this.gender!!.toUpperCase() else "M")
        json.put("fname", if (this.fname != null) this.fname else "")
        json.put("lname", if (this.lname != null) this.lname else "")
        json.put("usernameInput", if (this.usernameInput != null) this.usernameInput else "")
        json.put("username", if (this.username != null) this.username else "")
        json.put("postalCode", if (this.postalCode != null) this.postalCode else "")
        json.put("houseNumber", if (this.houseNumber != null) this.houseNumber else "")
        json.put("streetName", if (this.streetName != null) this.streetName else "")
        json.put("city", if (this.city != null) this.city else "")
        json.put("email", if (this.email != null) this.email else "")
        json.put("phoneNumber", if (this.phoneNumber != null) this.phoneNumber else "")
        json.put("isVolunteer",if (this.isVolunteer != null) this.isVolunteer else "")
        if (this.teamId != null) {
            json.put("_team", if (this.teamId != null) this.teamId else "")
        }
        if (this.hostId != null) {
            json.put("_host", this.hostId!!)
        }
        json.put("teamName", if (this.teamName != null) this.teamName else "")
        json.put("teamEmail", if (this.teamEmail != null) this.teamEmail else "")
        json.put("password", this.password)
        if (this.teamPhotoJson != null && this.teamPhotoJson!!.has("secure_url")) {
            json.put("logoSecuredUrl", this.teamPhotoJson!!.getString("secure_url"))
            json.put("logoUrl", this.teamPhotoJson!!.getString("url"))
        }
        json.put("isReporter", true)
        json.put("register_option", "MOBILE_APP")

        return json
    }

    fun saveInput() {
        val jsonInput = toJson()
        Log.d("REG_INPUT_SAVE", jsonInput.toString())
        App.prefs.registrationData = jsonInput.toString()
    }

    fun loadInput() {
        try {
            val jsonInputString = App.prefs.registrationData
            val json = JSONObject(jsonInputString)

            this.gender = if (json.has("gender")) json.getString("gender") else null
            this.fname = if (json.has("fname")) json.getString("fname") else null
            this.lname = if (json.has("lname")) json.getString("lname") else null
            this.usernameInput = if (json.has("usernameInput")) json.getString("usernameInput") else null
            this.username = if (json.has("username")) json.getString("username") else null
            this.email = if (json.has("email")) json.getString("email") else null
            this.postalCode = if (json.has("postalCode")) json.getString("postalCode") else null
            this.houseNumber = if (json.has("houseNumber")) json.getString("houseNumber") else null
            this.streetName = if (json.has("streetName")) json.getString("streetName") else null
            this.city = if (json.has("city")) json.getString("city") else null
            this.isVolunteer = if (json.has("isVolunteer")) json.getBoolean("isVolunteer") else null
            this.tac = if (json.has("tac")) json.getBoolean("tac") else false
            this.phoneNumber = if (json.has("phoneNumber")) json.getString("phoneNumber") else null
            this.teamEmail = if (json.has("teamEmail")) json.getString("teamEmail") else null
            this.teamId = if (json.has("teamId")) json.getString("teamId") else null
            this.teamName = if (json.has("teamName")) json.getString("teamName") else null
            this.teamPhotoJson = if (json.has("teamPhotoJson")) json.getJSONObject("teamPhotoJson") else null
            this.password = if (json.has("password")) json.getString("password") else null
            this.hostId = if (json.has("hostId")) json.getString("hostId") else null
        }
        catch (e: Exception) {
            Log.d("REGISTRATION_CLASS", e.localizedMessage)
        }
    }

    fun clear() {
        this.gender = null
        this.fname = null
        this.lname = null
        this.username = null
        this.email = null
        this.postalCode = null
        this.houseNumber = null
        this.streetName = null
        this.city = null
        this.isVolunteer = null
        this.tac = false
        this.phoneNumber = null
        this.teamEmail = null
        this.teamId = null
        this.teamName = null
        this.teamPhotoJson = null
        this.password = null
        this.saveInput()
    }

    fun setRegPage (page: Int) {
        App.prefs.registrationPage = page
    }

    fun getPage (): Int {
        return App.prefs.registrationPage
    }

}