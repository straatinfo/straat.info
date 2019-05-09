package com.straatinfo.straatinfo.Models

import org.json.JSONArray
import org.json.JSONObject

class Media {
    var fieldname: String? = null
    var originalname: String? = null
    var encoding: String? = null
    var mimetype: String? = null
    var public_id: String? = null
    var version: String? = null
    var signature: String? = null
    var width: Double? = null
    var height: Double? = null
    var format: String? = null
    var resource_type: String? = null
    var created_at: String? = null
    var tags: JSONArray? = null
    var bytes: Double? = null
    var type: String? = null
    var etag: String? = null
    var placeholder: String? = null
    var url: String? = null
    var secure_url: String? = null
    var original_filename: String? = null
    var jsonData: JSONObject? = null

    constructor(jsonData: JSONObject) {
        this.jsonData = jsonData
        this.fieldname = jsonData.getString("fieldname")
        this.originalname = jsonData.getString("originalname")
        this.mimetype = jsonData.getString("mimetype")
        this.public_id = jsonData.getString("public_id")
        this.version = jsonData.getString("version")
        this.signature = jsonData.getString("signature")
        this.width = jsonData.getDouble("width")
        this.height = jsonData.getDouble("height")
        this.format = jsonData.getString("format")
        this.resource_type = jsonData.getString("resource_type")
        this.created_at = jsonData.getString("created_at")
        this.tags = jsonData.getJSONArray("tags")
        this.bytes = jsonData.getDouble("bytes")
        this.type = jsonData.getString("type")
        this.etag = jsonData.getString("etag")
        this.placeholder = jsonData.getString("placeholder")
        this.url = jsonData.getString("url")
        this.secure_url = jsonData.getString("secure_url")
        this.original_filename = jsonData.getString("original_filename")
    }
}

/*
   JSON response from upload photo api

   "data": {
        "fieldname": "photo",
        "originalname": "GraphQLAPI.jpg",
        "encoding": "7bit",
        "mimetype": "image/jpeg",
        "public_id": "reports/GraphQLAPI.jpg_Wed May 08 2019 07:54:43 GMT+0200 (Central European Summer Time)",
        "version": 1557294981,
        "signature": "96671cc03a4e894c31a727f57bcbb456a0354c9a",
        "width": 1600,
        "height": 1194,
        "format": "jpg",
        "resource_type": "image",
        "created_at": "2019-05-08T05:56:21Z",
        "tags": [],
        "bytes": 1146607,
        "type": "upload",
        "etag": "de5e56a3d833a787f85790f72ecb38f0",
        "placeholder": false,
        "url": "http://res.cloudinary.com/hvina6sjo/image/upload/v1557294981/reports/GraphQLAPI.jpg_Wed%20May%2008%202019%2007:54:43%20GMT%2B0200%20%28Central%20European%20Summer%20Time%29.jpg",
        "secure_url": "https://res.cloudinary.com/hvina6sjo/image/upload/v1557294981/reports/GraphQLAPI.jpg_Wed%20May%2008%202019%2007:54:43%20GMT%2B0200%20%28Central%20European%20Summer%20Time%29.jpg",
        "original_filename": "file"
    }
 */