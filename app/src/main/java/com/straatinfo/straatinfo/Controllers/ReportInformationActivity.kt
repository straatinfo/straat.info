package com.straatinfo.straatinfo.Controllers

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.straatinfo.straatinfo.Models.Report
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.ReportService
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class ReportInformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_information)

        loadNavigation()


        val reportId = intent.getStringExtra("REPORT_ID")
        if (reportId != null) {
            this.getReportInfo(reportId) { report ->
                loadReportInfo(report)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    fun loadNavigation () {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun getReportInfo (reportId: String, cb: (Report) -> Unit) {
        val language = getString(R.string.language)
        ReportService.getReportDetails(reportId, language)
            .subscribeOn(Schedulers.io())
            .subscribe { reportInfo ->
                val report = Report(reportInfo)
                cb(report)
            }
            .run {  }
    }

    private fun loadReportInfo (report: Report) {
        val locationTxt = findViewById<TextView>(R.id.report_info_location_txt)
        val dateTxt = findViewById<TextView>(R.id.report_info_date_value)
        val timeTxt = findViewById<TextView>(R.id.report_info_time_value)
        val statusTxt = findViewById<TextView>(R.id.report_info_status_value)
        val mainCatTxt = findViewById<TextView>(R.id.report_info_main_cat_value)
        val subCatTxt = findViewById<TextView>(R.id.report_info_sub_cat_value)
        val descriptionTxt = findViewById<TextView>(R.id.report_info_description_value)
        val img1 = findViewById<ImageView>(R.id.report_info_photo_1_img)
        val img2 = findViewById<ImageView>(R.id.report_info_photo_2_img)
        val img3 = findViewById<ImageView>(R.id.report_info_photo_3_img)
        val reporterNameTxt = findViewById<TextView>(R.id.report_info_reported_by_value)
        val photoFrame = findViewById<FrameLayout>(R.id.report_info_photo_frame)
        val changeStatusBtn = findViewById<Button>(R.id.report_info_change_status_btn)
        val logo = findViewById<ImageView>(R.id.report_info_logo)
        changeStatusBtn.isEnabled = false

        locationTxt.text = report.location
        dateTxt.text = report.getDate()
        timeTxt.text = report.getTime()
        statusTxt.text = report.getStatus(this)
        mainCatTxt.text = report.mainCategoryName

        if (report.subCharCategoryName == null || report.subCharCategoryName == "") {
            subCatTxt.visibility = View.GONE
        } else {
            subCatTxt.visibility = View.VISIBLE
            subCatTxt.text = report.subCharCategoryName
        }

        descriptionTxt.text = if (report.description != null) report.description else ""

        if (report.attachments == null || report.attachments!!.length() < 1) {
            photoFrame.visibility = View.GONE
        } else {
            photoFrame.visibility = View.VISIBLE
        }

        if (report.attachments != null && report.attachments!!.length() > 0) {
            val imgJson = report.attachments!![0] as JSONObject
            if (imgJson.has("secure_url")) {
                Picasso.get().load(imgJson.getString("secure_url")).resize(612, 250).onlyScaleDown().into(img1)
                img1.visibility = View.VISIBLE
            }
        }

        if (report.attachments != null && report.attachments!!.length() > 1) {
            val imgJson = report.attachments!![1] as JSONObject
            if (imgJson.has("secure_url")) {
                Picasso.get().load(imgJson.getString("secure_url")).resize(612, 250).onlyScaleDown().into(img2)
                img2.visibility = View.VISIBLE
            }
        }

        if (report.attachments != null && report.attachments!!.length() > 2) {
            val imgJson = report.attachments!![2] as JSONObject
            if (imgJson.has("secure_url")) {
                Picasso.get().load(imgJson.getString("secure_url")).resize(612, 250).onlyScaleDown().into(img3)
                img3.visibility = View.VISIBLE
            }
        }

        reporterNameTxt.text = report.reporterUsername

        if (report.reportTypeCode != null && report.reportTypeCode!!.toLowerCase() == "b") {
            logo.setImageDrawable(getDrawable(R.drawable.ic_map_pointer_b))
            this.loadReportTypeBInfo(report)
        } else {
            logo.setImageDrawable(getDrawable(R.drawable.ic_map_pointer_a))
        }
    }

    fun loadReportTypeBInfo (report: Report) {
        val peopleInvolvedFrame = findViewById<FrameLayout>(R.id.report_info_people_involved_frame)
        val isPeopleInvolvedValue = findViewById<TextView>(R.id.report_info_are_people_involved_value)
        val peopleInvolvedCount = findViewById<TextView>(R.id.report_info_num_people_involved)
        val peopleInvolvedDesc = findViewById<TextView>(R.id.report_info_people_involved_description)
        val vehicleInvolvedFrame = findViewById<FrameLayout>(R.id.report_info_vehicle_involved_frame)
        val isVehicleInvolvedValue = findViewById<TextView>(R.id.report_info_are_vehicle_involved_value)
        val vehicleInvolvedCount = findViewById<TextView>(R.id.report_info_number_vehicle_involved_value)
        val vehicleInvovledDesc = findViewById<TextView>(R.id.report_info_vehicle_involved_description_value)
        if (report.isPeopleInvolved != null && report.isPeopleInvolved!!) {
            peopleInvolvedFrame.visibility = View.VISIBLE
            isPeopleInvolvedValue.text = if (report.isPeopleInvolved!!) getString(R.string.yes) else getString(R.string.no)
            peopleInvolvedCount.text = if (report.peopleInvolvedCount != null) report.peopleInvolvedCount!!.toString() else 0.toString()
            peopleInvolvedDesc.text = if (report.peopleInvolvedDescription != null) report.peopleInvolvedDescription!! else ""
        } else {
            peopleInvolvedFrame.visibility = View.GONE
        }

        if (report.isVehicleInvolved != null && report.isVehicleInvolved!!) {
            vehicleInvolvedFrame.visibility = View.VISIBLE
            isVehicleInvolvedValue.text = if (report.isVehicleInvolved!!) getString(R.string.yes) else getString(R.string.no)
            vehicleInvolvedCount.text = if (report.vehicleInvolvedCount != null) report.vehicleInvolvedCount!!.toString() else 0.toString()
            vehicleInvovledDesc.text = if (report.vehicleInvolvedDescription != null) report.vehicleInvolvedDescription!! else ""
        } else {
            vehicleInvolvedFrame.visibility = View.GONE
        }
    }
}