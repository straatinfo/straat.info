package com.straatinfo.straatinfo.Controllers

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import com.straatinfo.straatinfo.R
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.registration_step1.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val switchableLayout = this.switchableLayout
        switchableLayout.addView((layout(R.layout.registration_step2)))

        this.registrationNavData.setOnClickListener(View.OnClickListener {
            switchableLayout.removeAllViews()
            switchableLayout.addView((layout(R.layout.registration_step1)))

            val termsAndCond : Button = this.registration_terms_and_condition
            termsAndCond.setOnClickListener(onClicked())

        })

        this.registrationNavReporter.setOnClickListener(View.OnClickListener {
            switchableLayout.removeAllViews()
            switchableLayout.addView((layout(R.layout.registration_step2)))
        })

        this.registrationNavTeam.setOnClickListener(View.OnClickListener {
            switchableLayout.removeAllViews()
            switchableLayout.addView((layout(R.layout.registration_step3)))
        })
    }

    private fun layout(res: Int): View {
        val regStep1View : LayoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view : View = regStep1View.inflate(res, null)
        return view
    }

    private fun onClicked() = View.OnClickListener {
        this.termsAndConditionDialog()
        Toast.makeText(this, "hello", Toast.LENGTH_LONG).show()
    }

    private fun termsAndConditionDialog() {
        val dialog = Dialog(this)
        val inflater : LayoutInflater = this.layoutInflater
        val customView : View = inflater.inflate(R.layout.registration_terms_and_cond, null)
        dialog.setContentView(customView)

        val window : Window = dialog.window as Window
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
    }
}

