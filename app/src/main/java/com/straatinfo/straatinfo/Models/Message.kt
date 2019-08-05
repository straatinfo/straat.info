package com.straatinfo.straatinfo.Models

import com.straatinfo.straatinfo.Utilities.TZ_ZULU
import com.straatinfo.straatinfo.Utilities.WINDOW_INFO_REPORT_DATE_FORMAT
import org.json.JSONObject
import java.text.SimpleDateFormat

class Message {
    var id: String? = null
    var body: String? = null
    var userId: String? = null
    var username: String? = null
    var createdAt: String? = null

    constructor ()
    constructor(id: String, body: String, userId: String, username: String, createdAt: String) {
        this.id = id
        this.body = body
        this.userId = userId
        this.username = username
        this.createdAt = createdAt
    }
    constructor(json: JSONObject) {
        this.id = if (json.has("_id")) json.getString("_id") else null
        this.body = if (json.has("body")) json.getString("body") else null
        if (json.has("_author")) {
            val author = json.getJSONObject("_author")
            this.userId = author.getString("_id")
            this.username = author.getString("username")

        }
        this.createdAt = if (json.has("createdAt")) json.getString("createdAt") else null
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

    fun getDateTime (): String {
        return "${getDate()} ${getTime()}"
    }
}
