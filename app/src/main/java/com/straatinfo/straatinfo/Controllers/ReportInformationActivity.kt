package com.straatinfo.straatinfo.Controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.straatinfo.straatinfo.Models.Report
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.ReportService
import com.straatinfo.straatinfo.Utilities.BROADCAST_REPORT_DATA_CHANGE
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class ReportInformationActivity : AppCompatActivity() {

    var updated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        updated = false
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
        val source = intent.getStringExtra("PREVIIOUS_LOCATION")
        if (source != null && source!! == "MAIN" && this.updated) {
            val reportDataChange = Intent(BROADCAST_REPORT_DATA_CHANGE)
            LocalBroadcastManager.getInstance(this).sendBroadcast(reportDataChange)
        }
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
        val user = User()
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
        val statusIndicator = findViewById<ImageView>(R.id.status_indicator_img)
        changeStatusBtn.isEnabled = user.id != null &&  report.reporterId != null && report.status != null
                && (report.status!!.toLowerCase() == "new" || report.status!!.toLowerCase() == "inprogress")

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
            this.loadReportTypeBInfo(report)
        }

        if (report.status != null && report.status!!.toLowerCase() == "expired") {
            logo.setImageDrawable(getDrawable(R.drawable.ic_map_pointer_e))
            statusIndicator.setImageDrawable(getDrawable(R.drawable.ic_status_expired))
        } else if (report.status != null && report.status!!.toLowerCase() == "done") {
            logo.setImageDrawable(getDrawable(R.drawable.ic_map_pointer_a))
            statusIndicator.setImageDrawable(getDrawable(R.drawable.ic_status_done))
        } else if (report.status != null && report.status!!.toLowerCase() == "new") {
            logo.setImageDrawable(getDrawable(R.drawable.ic_map_pointer_c))
            statusIndicator.setImageDrawable(getDrawable(R.drawable.ic_status_new))
        } else {
            logo.setImageDrawable(getDrawable(R.drawable.ic_map_pointer_b))
            statusIndicator.setImageDrawable(getDrawable(R.drawable.ic_status_inprogress))
        }

        if (report.reportTypeCode != null && report.reportTypeCode!!.toLowerCase() == "b") {
            val isPublicLbl = findViewById<TextView>(R.id.report_info_is_public_lbl)
            val isPublicText = findViewById<TextView>(R.id.report_info_is_public_txt)
            val isPublicChangeBtn = findViewById<Button>(R.id.report_info_change_is_public_btn)
            isPublicChangeBtn.visibility = View.VISIBLE
            isPublicLbl.visibility = View.VISIBLE
            isPublicText.visibility = View.VISIBLE

            isPublicText.text = if (report.isPublic != null && report.isPublic!!) getString(R.string.yes) else getString(R.string.no)

            isPublicChangeBtn.isEnabled = report.reporterId != null && user.id != null && report.reporterId == user.id && (report.isPublic == null || !report.isPublic!!)
        }
    }

    private fun showUpdateReportResponseDialog (success: Boolean) {
        val alertDialog = AlertDialog.Builder(this)
        if (success) {
            alertDialog.setTitle(getString(R.string.success))
                .setMessage(getString(R.string.report_info_you_changed_report_status))
        } else {
            alertDialog.setTitle(getString(R.string.error))
                .setMessage(getString(R.string.error_error_occured))
        }
        alertDialog
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                dialog.dismiss()

            }
            .setOnDismissListener {
                val reportId = intent.getStringExtra("REPORT_ID")
                if (reportId != null) {
                    this.getReportInfo(reportId) { report ->
                        loadReportInfo(report)
                    }
                }
            }

        alertDialog.show()
    }

    private fun showUpdateReportPublicityResponseDialog (success: Boolean) {
        val alertDialog = AlertDialog.Builder(this)
        if (success) {
            alertDialog.setTitle(getString(R.string.success))
                .setMessage(getString(R.string.report_info_made_report_public))
        } else {
            alertDialog.setTitle(getString(R.string.error))
                .setMessage(getString(R.string.error_error_occured))
        }

        alertDialog
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                dialog.dismiss()

            }
            .setOnDismissListener {
                val reportId = intent.getStringExtra("REPORT_ID")
                if (reportId != null) {
                    this.getReportInfo(reportId) { report ->
                        loadReportInfo(report)
                    }
                }
            }

        alertDialog.show()
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

    private fun updateStatus () {
        val reportId = intent.getStringExtra("REPORT_ID")
        if (reportId != null) {
            ReportService.updateReportStatus(reportId, "DONE")
                .subscribeOn(Schedulers.io())
                .subscribe {
                    this.updated = it
                    this.showUpdateReportResponseDialog(it)
                }
                .run {  }
        }
    }

    private fun updateReportPublicity () {
        val reportId = intent.getStringExtra("REPORT_ID")
        if (reportId != null) {
            var data = JSONObject()
            data.put("isPublic", true)
            ReportService.updateReportInfo(reportId, data)
                .subscribeOn(Schedulers.io())
                .subscribe {
                    this.updated = it
                    this.showUpdateReportPublicityResponseDialog(it)
                }
                .run {  }
        }
    }

    fun updateStatusDialog (view: View) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage(getString(R.string.report_info_change_status_info))
        alertDialog.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            dialog.dismiss()
            this.updateStatus()
        }
        alertDialog.setNegativeButton(getString(R.string.no)) { dialog, which ->
            dialog.dismiss()
        }

        alertDialog.show()
    }

    fun updateReportPublicityDialog (view: View) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage(getString(R.string.report_info_make_report_public))
        alertDialog.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            dialog.dismiss()
            this.updateReportPublicity()
        }
        alertDialog.setNegativeButton(getString(R.string.no)) { dialog, which ->
            dialog.dismiss()
        }

        alertDialog.show()
    }
}
