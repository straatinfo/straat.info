package com.straatinfo.straatinfo.Models

import org.json.JSONArray
import org.json.JSONObject

class SubCategory {
    var id: String? = null
    var name: String? = null
    var description: String? = null
    var mainCategoryId: String? = null
    var translations: JSONArray? = null

    constructor(jsonData: JSONObject) {
        this.id = jsonData.getString("_id")
        this.name = jsonData.getString("name")
        this.description = jsonData.getString("description")
        this.mainCategoryId = jsonData.getString("_mainCategory")
        this.translations = jsonData.getJSONArray("translations")
    }
}