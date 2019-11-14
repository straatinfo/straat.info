package com.straatinfo.straatinfo.Services

import android.graphics.Bitmap
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Utilities.*
import io.reactivex.Observable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import uk.me.hardill.volley.multipart.MultipartRequest
import java.io.ByteArrayOutputStream
import java.lang.NullPointerException

object TeamService {
    val TAG = "TEAM_SERVICE"
    var teamServiceError: String? = null

    fun createTeam (userId: String, teamName: String, teamEmail: String, hostId: String, isVolunteer: Boolean, img: Bitmap?): Observable<Boolean> {
        val url = TEAM_CREATE_NEW + userId
        val headers = HashMap<String, String>()
        if (App.prefs.token != "") {
            headers.put("Authorization", "Bearer ${App.prefs.token}")
        }

        return Observable.create {
            var createTeamRequest = MultipartRequest(Request.Method.POST, url, headers, { response ->
                it.onNext(true)
            }, { errorResponse ->
                it.onNext(false)
            })

            createTeamRequest.addPart(MultipartRequest.FormPart("teamName", teamName))
            createTeamRequest.addPart(MultipartRequest.FormPart("teamEmail", teamEmail))
            createTeamRequest.addPart(MultipartRequest.FormPart("isVolunteer", isVolunteer.toString()))
            createTeamRequest.addPart(MultipartRequest.FormPart("_host", hostId))

            if (img != null) {
                var byte = ByteArrayOutputStream()
                img.compress(Bitmap.CompressFormat.JPEG, 90, byte)
                createTeamRequest.addPart(MultipartRequest.FilePart("team-logo", "image/jpeg", "$teamName", byte.toByteArray()))
            }

            App.prefs.requestQueue.add(createTeamRequest)
        }

    }


    fun updateTeam (teamId: String, teamName: String, teamEmail: String, img: Bitmap?): Observable<Boolean> {
        val url = "$TEAM_API/$teamId"
        val headers = HashMap<String, String>()

        if (App.prefs.token != "") {
            headers.put("Authorization", "Bearer ${App.prefs.token}")
        }

        return Observable.create {
            var updateTeamRequest = MultipartRequest(Request.Method.PUT, url, headers, { response ->
                it.onNext(true)
            }, { errorResponse ->
                it.onNext(false)
            })

            updateTeamRequest.addPart(MultipartRequest.FormPart("teamName", teamName))
            updateTeamRequest.addPart(MultipartRequest.FormPart("teamEmail", teamEmail))

            if (img != null) {
                var byte = ByteArrayOutputStream()
                img.compress(Bitmap.CompressFormat.JPEG, 90, byte)
                updateTeamRequest.addPart(MultipartRequest.FilePart("team-logo", "image/jpeg", "$teamName", byte.toByteArray()))
            }

            App.prefs.requestQueue.add(updateTeamRequest)
        }
    }

    fun getListOfTeamPerHostWithFilter (hostId: String, isVolunteer: Boolean) : Observable<JSONArray> {
        var queryObject = JSONObject()
        queryObject.put("_host", hostId)
        queryObject.put("isVolunteer", isVolunteer)
        val requestBody = JSONObject()
        requestBody.put("queryObject", queryObject)

        return Observable.create {
            val getTeamListRequest = object: JsonObjectRequest(Method.POST, TEAM_API, null, Response.Listener { success ->
                Log.d(TAG, success.toString())
                val data = success.getJSONArray("data") as JSONArray
                it.onNext(data)
            }, Response.ErrorListener { error ->
                try {
                    val err = JSONObject(String(error.networkResponse.data))

                    teamServiceError = err.getString("message") as String
                    it.onNext(JSONArray())
                } catch (e: JSONException) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Internal Server Error"
                    it.onNext(JSONArray())
                } catch (e: VolleyError) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Slow Internet Connection"
                    it.onNext(JSONArray())
                } catch (e: NullPointerException) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Internal Server Error"
                    it.onNext(JSONArray())
                }
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toString().toByteArray()
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", App.prefs.token)
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(getTeamListRequest)
        }
    }

    fun getTeamList (userId: String) : Observable<JSONArray> {
        val url = TEAM_LIST + userId
        return Observable.create {
            val getTeamListRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { success ->
                Log.d(TAG, success.toString())
                val data = success.getJSONArray("data") as JSONArray
                it.onNext(data)
            }, Response.ErrorListener { error ->
                try {
                    val err = JSONObject(String(error.networkResponse.data))

                    teamServiceError = err.getString("message") as String
                    it.onNext(JSONArray())
                } catch (e: JSONException) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Internal Server Error"
                    it.onNext(JSONArray())
                } catch (e: VolleyError) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Slow Internet Connection"
                    it.onNext(JSONArray())
                } catch (e: NullPointerException) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Internal Server Error"
                    it.onNext(JSONArray())
                }
            }){
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }


                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(getTeamListRequest)
        }
    }

    fun getTeamRequests (teamId: String) : Observable<JSONArray> {
        val url = TEAM_REQUEST + teamId
        return Observable.create {
            val teamMembershipRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                Log.d(TAG, response.toString())
                val data = if (response.has("data")) response.getJSONArray("data") as JSONArray else JSONArray()
                it.onNext(data)
            }, Response.ErrorListener { error ->
                try {
                    val err = JSONObject(String(error.networkResponse.data))

                    teamServiceError = err.getString("message") as String
                    it.onNext(JSONArray())
                } catch (e: JSONException) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Internal Server Error"
                    it.onNext(JSONArray())
                } catch (e: VolleyError) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Slow Internet Connection"
                    it.onNext(JSONArray())
                } catch (e: NullPointerException) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Internal Server Error"
                    it.onNext(JSONArray())
                }
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }


                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(teamMembershipRequest)
        }
    }

    fun getTeamInfo (teamId: String): Observable<JSONObject> {
        val url = TEAM_INFO + teamId

        return Observable.create {
            val teamInfoRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                Log.d(TAG, response.toString())
                val data = if (response.has("data")) response.getJSONObject("data") as JSONObject else JSONObject()
                it.onNext(data)
            }, Response.ErrorListener { error ->
                try {
                    val err = JSONObject(String(error.networkResponse.data))

                    teamServiceError = err.getString("message") as String
                    it.onNext(JSONObject())
                } catch (e: JSONException) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Internal Server Error"
                    it.onNext(JSONObject())
                } catch (e: VolleyError) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Slow Internet Connection"
                    it.onNext(JSONObject())
                } catch (e: NullPointerException) {
                    Log.d(TAG, e.localizedMessage)
                    teamServiceError = "Internal Server Error"
                    it.onNext(JSONObject())
                }
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }


                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(teamInfoRequest)
        }
    }

    fun acceptTeamMember (userId: String, teamId: String): Observable<Boolean> {
        val url = "$TEAM_ACCEPT_MEMBER$userId/$teamId"

        return Observable.create {
            val acceptTeamMemberRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { success ->
                it.onNext(true)
            }, Response.ErrorListener { error ->
                it.onNext(false)
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }


                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(acceptTeamMemberRequest)
        }
    }

    fun declineTeamMember (userId: String, teamId: String): Observable<Boolean> {
        val url = "$TEAM_DECLINE_MEMBER$userId/$teamId"

        return Observable.create {
            val declineTeamMemberRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { success ->
                it.onNext(true)
            }, Response.ErrorListener { error ->
                it.onNext(false)
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }


                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(declineTeamMemberRequest)
        }
    }

    fun getTeamRequestCount (userId: String): Observable<Int> {
        val url = "$TEAM_REQUEST_COUNT/$userId"

        return Observable.create {
            val getTeamRequestCount = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { success ->
                val count = success.getInt("count")
                if (count != null) {
                    it.onNext(count)
                } else {
                    it.onNext(0)
                }
            }, Response.ErrorListener { error ->
                it.onNext(0)
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }


                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    if (App.prefs.token != "") {
                        headers.put("Authorization", "Bearer ${App.prefs.token}")
                    }
                    return headers
                }
            }

            App.prefs.requestQueue.add(getTeamRequestCount)
        }
    }
}