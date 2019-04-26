package com.straatinfo.straatinfo.Controllers

import android.content.Context
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.straatinfo.straatinfo.R
import kotlinx.android.synthetic.main.activity_registration_step1.*

class RegistrationStep1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step1)

        val switchableLayout = this.switchableLayout
        switchableLayout.removeAllViews()
        switchableLayout.addView((layout(R.layout.registration_step1)))

        this.registrationNavData.setOnClickListener(View.OnClickListener {
            switchableLayout.removeAllViews()
            switchableLayout.addView((layout(R.layout.registration_step1)))

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

}

