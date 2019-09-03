package com.straatinfo.straatinfo.Controllers

import android.app.AlertDialog
import android.app.Application
import android.util.Log
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.Utilities.BASE_URL
import com.straatinfo.straatinfo.Utilities.SharedPrefs
import io.socket.client.IO
import io.socket.client.Socket

class App : Application() {

    companion object {
        lateinit var prefs: SharedPrefs
        var socket: Socket? = null
        fun connectToSocket (user: User): Socket {
            val ioOps = IO.Options()
            ioOps.query = "_user=${user.id}&token=${App.prefs.token}"
            Log.d("CONNECTING_SOCKET", ioOps.query)
            val mySocket =  IO.socket(BASE_URL, ioOps)
                .connect()
            mySocket
                .on(Socket.EVENT_CONNECT) {
                    Log.d("SOCKET", "connected")

                }
                .on(Socket.EVENT_DISCONNECT) {
                    socket = null
                    Log.d("SOCKET", "disconnected")
                }
                .on("register"){
                    Log.d("SOCKET", "Socket is registered")
                }
            return mySocket
        }
    }

    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        val user = User()
        if (user.id != null && prefs.token != "" && socket == null) {
            socket = connectToSocket(user)
        }

        super.onCreate()
    }

    private fun connectToSocket (user: User): Socket {
        val ioOps = IO.Options()
        ioOps.query = "_user=${user.id}&token=${App.prefs.token}"
        Log.d("CONNECTING_SOCKET", ioOps.query)
        val mySocket =  IO.socket(BASE_URL, ioOps)
            .connect()
        mySocket
            .on(Socket.EVENT_CONNECT) {
                Log.d("SOCKET", "connected")

            }
            .on(Socket.EVENT_DISCONNECT) {
                Log.d("SOCKET", "disconnected")
                socket = null
            }
            .on("register"){
                Log.d("SOCKET", "Socket is registered")
            }
        return mySocket
    }
}