package com.straatinfo.straatinfo.Services


import android.util.Log

import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.Utilities.SOCKET
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject


object SocketService {

    var socket: Socket? = null

    fun getInstance (): Socket {

        if (socket == null) {
            val ioOps = IO.Options()
            val user = User()
            ioOps.query = "_user=${user.id}&token=${App.prefs.token}"


            socket = IO.socket(SOCKET, ioOps)

            Log.d("SOCKET", socket!!.connected().toString())

            if (!socket!!.connected()) {
                socket!!.connect()

            }
        } else {
            if (!socket!!.connected()) {
                socket!!.connect()
            }
        }

        socket!!.emit("send-message-v2", JSONObject())
        socket!!.on("new-message") {
            Log.d("NEW_MESSAGE", "HAYST")
        }

        return socket!!
    }

}