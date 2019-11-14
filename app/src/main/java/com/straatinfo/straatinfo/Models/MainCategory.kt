package com.straatinfo.straatinfo.Models

import org.json.JSONArray
import org.json.JSONObject

class MainCategory {
    var id: String? = null
    var name: String? = null
    var description: String? = null
    var subCategories: JSONArray? = null
    var translations: JSONArray? = null
    var reportTypeCode: String? = "A"

    constructor(jsonData: JSONObject) {
        this.id = jsonData.getString("_id")
        this.name = jsonData.getString("name")
        this.description = jsonData.getString("description")
        this.subCategories = jsonData.getJSONArray("subCategories")
        this.translations = jsonData.getJSONArray("translations")
        if (jsonData.has("_reportType")) {
            val reportType = jsonData.getJSONObject("_reportType")
            if (reportType.has("code")) {
                this.reportTypeCode = reportType.getString("code")
            }
        }
    }
}