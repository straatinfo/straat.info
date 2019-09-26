package com.straatinfo.straatinfo.Services

import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Utilities.BROADCAST_NEW_MESSAGE_RECEIVED
import io.reactivex.schedulers.Schedulers

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (remoteMessage?.data != null) {
            Log.d("FIREBASE_TOKEN", App.prefs.firebaseToken)
            Log.d("FIREBASE_MESSAGING", "DATA: ${remoteMessage.data.toString()}")

//            var notification = NotificationCompat.Builder(applicationContext)
//                .setContentTitle("NOTIF")
//                .setContentText("HEY PLEASE CHECK THIS OUT")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .build()
//
//            var manager = NotificationManagerCompat.from(applicationContext)
//            manager.notify(123, notification)

            val newMessageReceived = Intent(BROADCAST_NEW_MESSAGE_RECEIVED)
            LocalBroadcastManager.getInstance(this).sendBroadcast(newMessageReceived)
        }

        if (remoteMessage?.notification != null) {
            Log.d("FIREBASE_TOKEN", App.prefs.firebaseToken)
            Log.d("FIREBASE_MESSAGING", "NOTIFICATION: ${remoteMessage.notification.toString()}")
        }
    }

    override fun onNewToken(token: String?) {
        Log.d("FIREBASE_MESSAGING", "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(token)
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val user = User()
        if (token != null && user.id != null && user.email != null) {
            AuthService.firebaseTokenUpdate(token, user.id!!, user.email!!, deviceId)
                .subscribeOn(Schedulers.io())
                .subscribe {

                }
                .run {}
        }

        if (token != null) {
            App.prefs.firebaseToken = token
        }
    }
}