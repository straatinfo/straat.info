package com.straatinfo.straatinfo.Models

import org.json.JSONObject

class Design {
    var id: String? = null
    var hostId: String? = null
    var colorOne: String? = null
    var colorTwo: String? = null
    var colorThree: String? = null
    var logoUrl: String? = null
    var designName: String? = null

    constructor (id: String?, hostId: String?, colorOne: String?, colorTwo: String?, colorThree: String?, logoUrl: String?, designName: String?) {
        this.id = id
        this.hostId = hostId
        this.colorOne = colorOne
        this.colorTwo = colorTwo
        this.colorThree = colorThree
        if (logoUrl != null) {
            this.logoUrl = logoUrl
        }
        this.designName = designName
    }

    fun toJson (): JSONObject{
        val jsonObject: JSONObject = JSONObject()

        jsonObject.put("id", this.id)
        jsonObject.put("hostId", this.hostId)
        jsonObject.put("colorOne", this.colorOne)
        jsonObject.put("colorTwo", this.colorTwo)
        jsonObject.put("colorThree", this.colorThree)
        if (this.logoUrl != null) {
            jsonObject.put("logoUrl", this.logoUrl)
        }
        jsonObject.put("designName", this.designName)
        return jsonObject
    }
}