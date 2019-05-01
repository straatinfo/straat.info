package com.straatinfo.straatinfo.Models

import org.json.JSONObject

class Report {
    var id: String? = null
    var long: Double? = null
    var lat: Double? = null
    var createdAt: String? = null
    var updatedAt: String? = null
    var mainCategoryId: String? = null
    var mainCategoryName: String? = null
    var subCategoryId: String? = null
    var subCharCategoryName: String? = null
    var reportTypeId: String? = null
    var reportTypeName: String? = null
    var reportTypeCode: String? = null
    var reporterId: String? = null
    var reporterEmail: String? = null
    var reporterUsername: String? = null
    var description: String? = null
    var location: String? = null
    var title: String? = null
    var generatedReportId: String? = null

    var reportJson: JSONObject? = null

    constructor(reportJson: JSONObject) {
        this.reportJson = reportJson

        this.id = reportJson.getString("_id")
        this.generatedReportId = reportJson.getString("generatedReportId")
        this.location = reportJson.getString("location")
        this.title = reportJson.getString("title")
        this.long = reportJson.getDouble("long")
        this.lat = reportJson.getDouble("lat")
        this.createdAt = reportJson.getString("createdAt")
        this.updatedAt = reportJson.getString("updatedAt")

        val reportType = reportJson.getJSONObject("_reportType")

        this.reportTypeId = reportType.getString("_id")
        this.reportTypeCode = reportType.getString("code")
        this.reportTypeName = reportType.getString("name")


        val mainCategory = reportJson.getJSONObject("_mainCategory")
        this.mainCategoryId = mainCategory.getString("_id")
        this.mainCategoryName = mainCategory.getString("name")

        if (this.reportTypeCode != null && this.reportTypeCode!!.toLowerCase() == "a") {
            val subCategory = reportJson.getJSONObject("_subCategory")
            this.subCategoryId = subCategory.getString("_id")
            this.subCharCategoryName = subCategory.getString("name")
        }

//        val reporter = reportJson.getJSONObject("_reporter")
//        this.reporterId = reporter.getString("_id")
//        this.reporterEmail = reporter.getString("email")
//        this.reporterUsername = reporter.getString("username")
    }
}