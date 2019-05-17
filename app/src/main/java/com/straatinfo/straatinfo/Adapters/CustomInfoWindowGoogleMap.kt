package com.straatinfo.straatinfo.Adapters

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.transition.Transition
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Models.Report
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.UtilService
import com.straatinfo.straatinfo.Utilities.WINDOW_INFO_REPORT_DATE_FORMAT
import kotlinx.android.synthetic.main.layout_custom_map_marker.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class CustomInfoWindowGoogleMap(val context: Context) : GoogleMap.InfoWindowAdapter{
    override fun getInfoContents(p0: Marker?): View {

        var mInfoView = (context as Activity).layoutInflater.inflate(R.layout.layout_custom_map_marker, null)
        var mInfoWindow: Report? = p0?.tag as Report?

        val createdAt = mInfoWindow!!.createdAt

        val format = SimpleDateFormat("yyyy-DD-mm")
        val dateFormat = SimpleDateFormat(WINDOW_INFO_REPORT_DATE_FORMAT)

        val date = format.parse(createdAt)


        mInfoView.reportDateTxt.text = dateFormat.format(date)
        mInfoView.mainCatTxt.text = UtilService.shortenedChar(mInfoWindow!!.mainCategoryName!!, 12)
        val preview: ImageView = mInfoView.reportPreviewImg
        val noPhoto: TextView = mInfoView.windowInfoNoPhotoTxt
        preview.setImageResource(R.drawable.ic_logo)

        if (mInfoWindow!!.attachments!!.length() > 0) {
            val attachment: JSONObject = mInfoWindow!!.attachments!![0] as JSONObject
            val secureUrl = attachment.getString("secure_url")
            Glide.with(context).load(Uri.parse(secureUrl)).into(preview)
            preview.visibility = View.VISIBLE
            noPhoto.visibility = View.INVISIBLE
        } else {
            preview.setImageResource(R.drawable.ic_logo)
            preview.visibility = View.INVISIBLE
            noPhoto.visibility = View.VISIBLE
        }
        // mInfoView.reportPreviewImg.setImageBitmap()

        return mInfoView
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }
}