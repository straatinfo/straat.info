package com.straatinfo.straatinfo.Models

import android.content.Context
import android.graphics.Bitmap
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Utilities.TZ_ZULU
import com.straatinfo.straatinfo.Utilities.WINDOW_INFO_REPORT_DATE_FORMAT
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat


class Report {
    var id: String? = null
    var long: Double? = null
    var lat: Double? = null
    var createdAt: String? = null
    var updatedAt: String? = null
    var status: String? = null
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
    var reporterFirstname: String? = null
    var reporterLastname: String? = null
    var description: String? = null
    var location: String? = null
    var title: String? = null
    var generatedReportId: String? = null
    var isUrgent: Boolean? = null

    var reportJson: JSONObject? = null
    var attachments: JSONArray? = null
    var conversation: JSONObject? = null
    var unreadMessageCount: Int? = 0

    var photoPreview: Bitmap? = null


    // repor type b specific fields
    var isPeopleInvolved: Boolean? = null
    var peopleInvolvedCount: Int? = null
    var peopleInvolvedDescription: String? = null
    var isVehicleInvolved: Boolean? = null
    var vehicleInvolvedCount: Int? = null
    var vehicleInvolvedDescription: String? = null

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
        this.status = if (reportJson.has("status")) reportJson.getString("status") else null
        this.isUrgent = if (reportJson.has("isUrgent")) reportJson.getBoolean("isUrgent") else null

        val reportType = reportJson.getJSONObject("_reportType")

        this.reportTypeId = reportType.getString("_id")
        this.reportTypeCode = reportType.getString("code")
        this.reportTypeName = reportType.getString("name")


        val mainCategory = reportJson.getJSONObject("_mainCategory")
        this.mainCategoryId = mainCategory.getString("_id")
        this.mainCategoryName = mainCategory.getString("name")

        this.description = if (reportJson.has("description")) reportJson.getString("description") else null

        if (this.reportTypeCode != null && this.reportTypeCode!!.toLowerCase() == "a") {
            val subCategory = if (reportJson.has("_subCategory")) reportJson.getJSONObject("_subCategory") else null
            this.subCategoryId = if (subCategory != null) subCategory.getString("_id") else null
            this.subCharCategoryName = if (subCategory != null) subCategory.getString("name") else null
        }

        this.attachments = if (reportJson.has("attachments")) reportJson.getJSONArray("attachments") else null
        if (reportJson.has("_conversation")) {
            val convoJson = reportJson.getJSONObject("_conversation")
            this.unreadMessageCount = if (convoJson.has("unreadMessageCount")) convoJson.getInt("unreadMessageCount") else 0
        }
        this.conversation = if (reportJson.has("_conversation")) reportJson.getJSONObject("_conversation") else null

        if (reportJson.has("_reporter")) {
            val reporter = reportJson.getJSONObject("_reporter")
            this.reporterId = if (reporter.has("_id")) reporter.getString("_id") else null
            this.reporterEmail = if (reporter.has("email")) reporter.getString("email") else null
            this.reporterUsername = if (reporter.has("username")) reporter.getString("username") else null
            this.reporterFirstname = if (reporter.has("fname")) reporter.getString("fname") else null
            this.reporterLastname = if (reporter.has("lname")) reporter.getString("lname") else null
        }

//        val reporter = reportJson.getJSONObject("_reporter")
//        this.reporterId = reporter.getString("_id")
//        this.reporterEmail = reporter.getString("email")
//        this.reporterUsername = reporter.getString("username")

        if (this.reportTypeCode != null && this.reportTypeCode!!.toLowerCase() == "b") {
            this.isPeopleInvolved = if (reportJson.has("isPeopleInvolved")) reportJson.getBoolean("isPeopleInvolved") else null
            this.peopleInvolvedCount = if (reportJson.has("peopleInvolvedCount")) reportJson.getInt("peopleInvolvedCount") else null
            this.peopleInvolvedDescription = if (reportJson.has("peopleInvolvedDescription")) reportJson.getString("peopleInvolvedDescription") else null

            this.isVehicleInvolved = if (reportJson.has("isVehicleInvolved")) reportJson.getBoolean("isVehicleInvolved") else null
            this.vehicleInvolvedCount = if (reportJson.has("vehicleInvolvedCount")) reportJson.getInt("vehicleInvolvedCount") else null
            this.vehicleInvolvedDescription = if (reportJson.has("vehicleInvolvedDescription")) reportJson.getString("vehicleInvolvedDescription") else null
        }
    }

    fun getDate (): String {
        val format = SimpleDateFormat(TZ_ZULU)
        val dateFormat = SimpleDateFormat(WINDOW_INFO_REPORT_DATE_FORMAT)

        return dateFormat.format(format.parse(this.createdAt)).toString()
    }

    fun getTime (): String {
        val format = SimpleDateFormat(TZ_ZULU)
        val timeFormat = SimpleDateFormat("HH:mm:ss")

        return timeFormat.format(format.parse(this.createdAt)).toString()
    }

    fun getStatus (context: Context): String {
        when (this.status) {
            "NEW" -> return context.getString(R.string.report_info_new)
            "COMPLETED" -> return context.getString(R.string.report_info_completed)
            else -> return context.getString(R.string.report_info_new)
        }
    }

    fun reporterFullname (): String {
        return "${this.reporterFirstname} ${this.reporterLastname}"
    }

    fun getMessagesCount (): Int {
        return if (this.conversation != null && this.conversation!!.has("messages")) {
            val messages = conversation!!.getJSONArray("messages")
            messages.length()
        } else {
            0
        }
    }

    fun getUnreadMessagesCount (): Int {
        return if (this.unreadMessageCount != null) this.unreadMessageCount!! else 0
    }
}

/*
*  Reporter object
*  {
*    "_id": "5c63e92035086200156f93e0",
*    "city": "'s-Gravenhage",
*    "email": "test4@test.com",
*    "fname": "Johnny",
*    "gender": "FEMALE",
*    "houseNumber": "1",
*    "lname": "Doe",
*    "phoneNumber": "0687878787",
*    "postalCode": "2521AA",
*    "streetName": "Calandkade",
*    "username": "Tester4",
*    "country": "Netherlands"
*  }
* */