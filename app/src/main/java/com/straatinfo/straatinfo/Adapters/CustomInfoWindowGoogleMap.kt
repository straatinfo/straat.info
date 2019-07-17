package com.straatinfo.straatinfo.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Callback
import com.straatinfo.straatinfo.Models.Report
import com.straatinfo.straatinfo.Services.UtilService
import com.straatinfo.straatinfo.Utilities.WINDOW_INFO_REPORT_DATE_FORMAT
import kotlinx.android.synthetic.main.layout_custom_map_marker.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import com.squareup.picasso.Picasso
import com.straatinfo.straatinfo.Controllers.ReportInformationActivity
import com.straatinfo.straatinfo.Utilities.TZ_ZULU
import io.reactivex.Single
import java.lang.Exception


class CustomInfoWindowGoogleMap(val context: Context) : GoogleMap.InfoWindowAdapter{
    var myBitmap: Bitmap? = null
    var lastMarker: Marker? = null
    var mInfoView: View? = null
    override fun getInfoContents(p0: Marker?): View {
        Log.d("LOADING_INFO_CONTENTS", "LOADING_INFO_CONTENTS")

        if (mInfoView == null) {
            mInfoView = (context as Activity).layoutInflater.inflate(com.straatinfo.straatinfo.R.layout.layout_custom_map_marker, null)
        }

        if (lastMarker == null
            || lastMarker!!.id != p0!!.id) {
            lastMarker = p0

            var report: Report? = p0?.tag as Report?

            val createdAt = report!!.createdAt

            val format = SimpleDateFormat(TZ_ZULU)
            val dateFormat = SimpleDateFormat(WINDOW_INFO_REPORT_DATE_FORMAT)

            val date = format.parse(createdAt)


            mInfoView!!.reportDateTxt.text = dateFormat.format(date)
            mInfoView!!.view_report_main_cat_txt.text = UtilService.shortenedChar(report!!.mainCategoryName!!, 12)
            mInfoView!!.view_report_view_report_txt.setOnClickListener {
                val intent = Intent(context, ReportInformationActivity::class.java)
                intent.putExtra("REPORT_ID", report!!.id)
                context.startActivity(intent)
            }
            val preview: ImageView = mInfoView!!.reportPreviewImg
            preview.visibility = View.VISIBLE
            val noPhoto: TextView = mInfoView!!.windowInfoNoPhotoTxt
            noPhoto.visibility = View.VISIBLE
            preview.setImageResource(com.straatinfo.straatinfo.R.drawable.ic_logo)

            if (report!!.attachments != null && report!!.attachments!!.length() > 0) {
                // preview.setImageBitmap(myBitmap)
                val attachment: JSONObject = report!!.attachments!![0] as JSONObject
                val secureUrl = attachment.getString("secure_url")


                Picasso.get().load(secureUrl)
                    .resize(300, 300)
                    .centerCrop().noFade()
                    .placeholder(com.straatinfo.straatinfo.R.drawable.ic_logo)
                    .into(preview, object: Callback {
                    override fun onError(e: Exception?) {
                        //
                        if (p0 != null && p0.isInfoWindowShown) {
                            preview.visibility = View.VISIBLE
                            p0.showInfoWindow()
                        }
                    }

                    override fun onSuccess() {
                        //
                        if (p0 != null && p0.isInfoWindowShown) {
                            preview.visibility = View.VISIBLE
                            p0.showInfoWindow()
                        }
                    }
                })

                noPhoto.visibility = View.INVISIBLE



            } else {
                preview.setImageResource(com.straatinfo.straatinfo.R.drawable.ic_logo)
                preview.visibility = View.INVISIBLE
                noPhoto.visibility = View.VISIBLE
            }

            mInfoView!!.setOnClickListener {
                val intent = Intent(context, ReportInformationActivity::class.java)
                intent.putExtra("REPORT_ID", report.id)
                context.startActivity(intent)
            }
        }
        return mInfoView!!
    }

    override fun getInfoWindow(p0: Marker?): View? {

        return null
    }


    fun getBitmapSingle(picasso: Picasso, url: String): Single<Bitmap> = Single.create {
        try {
            if (!it.isDisposed) {
                val bitmap: Bitmap = picasso.load(url).get()
                it.onSuccess(bitmap)
            }
        } catch (e: Throwable) {
            it.onError(e)
        }
    }
}