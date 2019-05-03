package com.straatinfo.straatinfo.Adapters

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Models.Report
import com.straatinfo.straatinfo.R
import kotlinx.android.synthetic.main.layout_custom_map_marker.view.*
import org.json.JSONObject

class CustomInfoWindowGoogleMap(val context: Context) : GoogleMap.InfoWindowAdapter{
    override fun getInfoContents(p0: Marker?): View {

        var mInfoView = (context as Activity).layoutInflater.inflate(R.layout.layout_custom_map_marker, null)
        var mInfoWindow: Report? = p0?.tag as Report?

        mInfoView.reportDateTxt.text = mInfoWindow!!.createdAt
        mInfoView.mainCatTxt.text = mInfoWindow!!.mainCategoryName
        val preview: ImageView = mInfoView.reportPreviewImg
        preview.setImageResource(R.drawable.ic_logo)

        if (mInfoWindow!!.attachments!!.length() > 0) {
            val attachment: JSONObject = mInfoWindow!!.attachments!![0] as JSONObject
            val secureUrl = attachment.getString("secure_url")
            Glide.with(context).load(Uri.parse(secureUrl)).into(preview)
        } else {
            preview.setImageResource(R.drawable.ic_logo)
        }
        // mInfoView.reportPreviewImg.setImageBitmap()

        return mInfoView
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }
}