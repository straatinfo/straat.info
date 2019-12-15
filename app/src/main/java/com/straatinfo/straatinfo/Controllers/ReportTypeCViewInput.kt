package com.straatinfo.straatinfo.Controllers

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.ReportService
import com.straatinfo.straatinfo.Services.UtilService
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_report_type_cview_input.*
import kotlinx.android.synthetic.main.layout_custom_map_marker.*
import org.json.JSONArray
import org.json.JSONObject

class ReportTypeCViewInput : AppCompatActivity() {
    var reportJson: JSONObject = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_type_cview_input)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        this.loadInfo()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun loadInfo () {
        val teamNames = intent.getStringExtra("REPORT_TYPE_C_TEAM_LIST")
        val teamNamesArray = JSONArray(teamNames)
        val reportInfo = intent.getStringExtra("REPORT_TYPE_C_DETAILS")
        val teamNamesList = mutableListOf<String>()

        Log.d("REPORT_DETAILS_VIEW", reportInfo)

        this.reportJson = JSONObject(reportInfo)
        var info = this.findViewById<EditText>(R.id.report_type_c_general_info_input_txt)
        var description = this.findViewById<EditText>(R.id.report_type_c_view_input_description_txt)

        this.report_type_c_general_info_input_txt.hint =  if (this.reportJson.has("mainCategoryName")) this.reportJson.getString("mainCategoryName")!! else ""
        this.report_type_c_view_input_description_txt.hint = if (this.reportJson.has("description")) this.reportJson.getString("description")!! else ""

        for (i in 0 until teamNamesArray.length()) {
            teamNamesList.add(teamNamesArray[i] as String)
        }

        loadSpinner(teamNamesList)
    }

    private fun loadSpinner (teamNames: MutableList<String>) {
        val spinnerAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, teamNames)
        this.report_type_c_view_input_spinner.adapter = spinnerAdapter
    }

    fun back (view: View) {
        finish()
    }

    fun sendReport (view: View) {
        ReportService.sendReportV2(this.reportJson)
            .subscribeOn(Schedulers.io())
            .subscribe {
                when(it.getBoolean("success")) {
                    true -> {
                        val title = getString(R.string.success)
                        val message = getString(R.string.report_send_report_success)
                        val yes = getString(R.string.yes)
                        val dialog = AlertDialog.Builder(this)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton(yes, DialogInterface.OnClickListener { dialog, i ->
                                val returnIntent = Intent(this, MainActivity::class.java)
                                startActivity(returnIntent)
                                finish()
                            })
                        dialog.show()
                    }
                    else -> {
                        val title = getString(R.string.error)
                        val message = getString(R.string.report_error_internal_server)
                        val dialog = UtilService.showDefaultAlert(this, title, message)
                        dialog.show()
                        // Toast.makeText(this, "An error occured while sending the report", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .run {  }
    }
}
