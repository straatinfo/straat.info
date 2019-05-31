package com.straatinfo.straatinfo.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.straatinfo.straatinfo.Models.Report
import com.straatinfo.straatinfo.R
import org.json.JSONObject

class ReportListAdapter (val context: Context, val reports: MutableList<Report>, val reportClick: (Report) -> Unit): RecyclerView.Adapter<ReportListAdapter.Holder> () {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ReportListAdapter.Holder {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.adapter_view_report_list, parent, false)

        return Holder(view, reportClick)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return reports.count()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bindCategory(reports[position], context)
    }

    inner class Holder(itemView: View, val itemClick: (Report) -> Unit): RecyclerView.ViewHolder(itemView) {
        val mainCategoryTxt = itemView?.findViewById<TextView>(R.id.view_report_main_cat_txt)
        val dateTxt = itemView?.findViewById<TextView>(R.id.view_report_date_txt)
        val messageCount = itemView?.findViewById<TextView>(R.id.view_report_number_txt)
        val viewMessageTxt = itemView?.findViewById<TextView>(R.id.view_report_view_messages)
        val img = itemView?.findViewById<ImageView>(R.id.view_report_img)
        fun bindCategory (report: Report, context: Context) {
            mainCategoryTxt.text = report.mainCategoryName
            dateTxt.text = report.createdAt
            messageCount.text = "0"

            if (report.attachments != null && report.attachments!!.length() > 0) {
                val imageJson = report.attachments!![0] as JSONObject
                val secureUrl = imageJson.getString("secure_url")
                Picasso.get().load(secureUrl).into(img)
            } else {
                img.setImageResource(R.drawable.ic_logo)
            }

            itemView.setOnClickListener {
                itemClick(report)
            }
        }
    }
}