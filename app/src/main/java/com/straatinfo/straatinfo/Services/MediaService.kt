package com.straatinfo.straatinfo.Services

import android.graphics.Bitmap
import android.util.Log
import com.android.volley.VolleyError
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Utilities.PUBLIC_UPLOAD
import io.reactivex.Observable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import uk.me.hardill.volley.multipart.MultipartRequest
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.lang.NullPointerException

object MediaService {
    val TAG = "MEDIA_SERVICE"
    var errorMessage: String? = null
    val headers = HashMap<String, String>()

    fun publicUpload (file: Bitmap, fileTitle: String): Observable<JSONObject> {
        errorMessage = null
        return Observable.create {
            val uploadRequest = MultipartRequest(PUBLIC_UPLOAD, headers, { response ->
                try {
                    val data = String(response.data)
                    Log.d(TAG, data)
                    val json = JSONObject(data)
                    it.onNext(json.getJSONObject("data"))
                } catch (e: Exception) {
                    errorMessage = "An Error Occured While uploading photo"
                    it.onNext(JSONObject())
                }

            }, { errorResponse ->
                Log.d("ERROR_22344", errorResponse.toString())
                try {
                    val err = String(errorResponse.networkResponse.data)
                    var errorBody = JSONObject(err)
                    errorMessage = errorBody.getString("message")
                    Log.d("POST_REPORT", err)
                } catch (e: Exception) {
                    errorMessage = "An Error Occured While uploading photo"
                    Log.d("POST_REPORT", "An Error occured")
                }
            })

            var byte = ByteArrayOutputStream()
            file.compress(Bitmap.CompressFormat.JPEG, 90, byte)
            uploadRequest.addPart(MultipartRequest.FilePart("photo", "image/jpeg", "${fileTitle}", byte.toByteArray()))

            App.prefs.requestQueue.add(uploadRequest)
        }
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