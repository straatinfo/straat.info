package com.straatinfo.straatinfo.Controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toolbar
import com.straatinfo.straatinfo.R
import kotlinx.android.synthetic.main.app_bar_main.*

class ReportsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.d("SELECTED_ACTIVITY", "Reports activity")

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when (item?.itemId) {
//            R.id.my_reports -> {
//                NavUtils.navigateUpFromSameTask(this)
//                return true
//            }
//        }
        val mainActivity = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
        finish()
        return super.onOptionsItemSelected(item)
    }


}
