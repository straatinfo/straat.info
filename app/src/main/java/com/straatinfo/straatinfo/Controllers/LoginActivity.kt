package com.straatinfo.straatinfo.Controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.AuthService
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        loginBtn.setOnClickListener(View.OnClickListener {
//            Toast.makeText(this, "hello", Toast.LENGTH_LONG).show()
//        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun goToMainActivity () {
        val mainActivity = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)

        finish()
    }

    private fun login (email: String, password: String, completion: (success: Boolean, errorMessage: String?) -> Unit) {
        AuthService.loginUserRx(email, password)
            .subscribeOn(Schedulers.io())
            .subscribe { 
                if (it) {
                    completion(true, "")
                } else {
                    completion(false, "")
                }
            }
            .run {  }
    }

    fun onRegisterClick (view: View) {
        val regsitrationIntent = Intent(applicationContext, RegistrationActivity::class.java)

        startActivity(regsitrationIntent)

        finish()
    }

    fun onLoginInClick (view: View) {
        val email = loginEmailET.text.toString()
        val password = loginPasswordET.text.toString()

        if (email != null && email != "" && password != null && password != "") {
            this.login(email, password) { success, errorMessage ->
                if (success) {
                    this.goToMainActivity()
                } else {
                    // show error message pop up
                }
            }
        }
    }
}
