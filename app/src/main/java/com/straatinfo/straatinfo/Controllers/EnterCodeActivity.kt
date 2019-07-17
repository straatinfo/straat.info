package com.straatinfo.straatinfo.Controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.straatinfo.straatinfo.Models.Host
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.AuthService
import com.straatinfo.straatinfo.Services.UtilService
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_enter_code.*
import java.lang.Exception

class EnterCodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_code)

        App.prefs.registrationData = ""
        App.prefs.registrationPassword = ""
    }

    fun onSendCodeClick (view: View) {
        val code = enterCodeTxtBox.text.toString()

        Log.d("ENTERED_CODE", code)

//        if (code == "") {
//            // show a popup here
//        }
//
        AuthService.getHostCodeRx(code)
            .subscribeOn(Schedulers.io())
            .subscribe { result ->
                try {
//                    val error = result!!.getString("error")
////                    if (error != null && error != "") {
////                        Log.d("HOST_CODE_ERROR", error)
////                    }

                    if (AuthService.authResponseError != null) {
                        enterCodeTxtBox.text.clear()
                        val dialog = UtilService.showDefaultAlert(this, "Error", "Je hebt geen of niet goede code. Probeer opnieuw of maa een andere keuze.")
                        dialog.show()
                    } else {
                        val data = result!!.getJSONObject("data")
                        val host = Host(data)

                        App.prefs.hostData = host.getJsonObject().toString()
                        Log.d("HOST_ID", host.id)

                        Log.d("HOST_DATA_FETCH", data.toString())

                        val loginIntent = Intent(this, LoginActivity::class.java)
                        startActivity(loginIntent)
                        finish()
                    }

                }
                catch (e: Exception) {
                    Log.d("ON_SEND_CODE_ERROR", e.localizedMessage)
                }
            }
            .run {
               // Log.d("API RUNNING")
            }
    }
}
