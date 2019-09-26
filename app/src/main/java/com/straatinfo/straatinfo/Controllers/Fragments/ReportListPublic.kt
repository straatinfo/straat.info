package com.straatinfo.straatinfo.Controllers.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.straatinfo.straatinfo.Adapters.ReportListAdapter
import com.straatinfo.straatinfo.Controllers.App
import com.straatinfo.straatinfo.Controllers.ReportInformationActivity
import com.straatinfo.straatinfo.Controllers.ReportMessagesActivity
import com.straatinfo.straatinfo.Models.Report
import com.straatinfo.straatinfo.Models.User

import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.ReportService
import com.straatinfo.straatinfo.Utilities.BROADCAST_NEW_MESSAGE_RECEIVED
import io.reactivex.schedulers.Schedulers
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.fragment_report_list_public.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ReportListPublic.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ReportListPublic.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ReportListPublic : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var reportList = mutableListOf<Report>()
    lateinit var adapter: ReportListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val socket = App.socket
        if (socket != null) {
            socket!!
                .on("new-message", onSendMessage)
                .on("send-message-v2", onSendMessage)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        loadPublicReports {
            this.loadAdapter()
        }

        LocalBroadcastManager.getInstance(context!!).registerReceiver(this.newMessageDataReceiver, IntentFilter(
            BROADCAST_NEW_MESSAGE_RECEIVED
        ))

        return inflater.inflate(R.layout.fragment_report_list_public, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("REPORT_A", "attaching on detach")
        super.onSaveInstanceState(outState)
    }

    override fun setInitialSavedState(state: SavedState?) {
        Log.d("REPORT_A", "attaching on detach return")
        super.setInitialSavedState(state)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d("REPORT_A", "attaching on detach activity created")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        Log.d("REPORT_A", "attaching on detach resume")
        loadPublicReports {
            this.loadAdapter()
        }
        super.onResume()
    }


    override fun onAttach(context: Context) {
        Log.d("REPORT_A", "attaching on detach")
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        Log.d("REPORT_A", "detaching on detach")
//        val socket = App.socket
//        if (socket != null) {
//            socket!!
//                .on("new-message"){ Log.d("REPORT_A", "detaching")}
//                .on("send-message-v2"){ Log.d("REPORT_A", "detaching")}
//        }
        super.onDetach()
        listener = null
    }

    override fun onDestroy() {
        Log.d("REPORT_A", "detaching on destroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        Log.d("REPORT_A", "detaching on destroy view")
        super.onDestroyView()
    }

    private fun onReportClick (report: Report) {
        val intent = Intent(context!!, ReportInformationActivity::class.java)
        intent.putExtra("PREVIIOUS_LOCATION", "REPORT_A")
        intent.putExtra("REPORT_ID", report.id)
        context!!.startActivity(intent)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReportListPublic.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportListPublic().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun reportLoader (reportArray: JSONArray, cb: (reportList: MutableList<Report>) -> Unit) {
        reportList = mutableListOf()
        var unreadMessage = 0
        for (i in 0 until reportArray.length()) {
            val reportJson = reportArray[i] as JSONObject

            var report = Report(reportJson)

            unreadMessage += report.getUnreadMessagesCount()

            reportList.add(reportList.count(), report)
        }
        Log.d("UNREAD_MESSAGE_COUNT", unreadMessage.toString())
        App.prefs.unreadPublicReportMessage = unreadMessage
        cb(reportList)
    }

    fun loadPublicReports (cb: (reportList: MutableList<Report>) -> Unit) {
        if (context != null) {
            val user = User(JSONObject(App.prefs.userData))

            val reporterId = user.id!!
            val language = context!!.getString(R.string.language)
            val reportType = "A"

            ReportService.getReportList(reportType, reporterId, language)
                .subscribeOn(Schedulers.io())
                .subscribe { reportList ->
                    reportLoader(reportList, cb)
                }
                .run {  }
        }
    }


    private val onSendMessage = Emitter.Listener { args ->
        Log.d("RECEIVING_REPORT_LIST", args.toString())
        this.loadPublicReports {
            this.loadAdapter()
        }
    }

    private fun reloadAdapter () {
        this.loadPublicReports {
            this.loadAdapter()
        }
    }

    private val newMessageDataReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            reloadAdapter()
        }
    }

    private fun loadAdapter () {
        try {
            adapter = ReportListAdapter(context!!, reportList, ({ report -> onReportClick(report)})) { report ->
                val intent = Intent(context!!, ReportMessagesActivity::class.java)
                intent.putExtra("REPORT_ID", report.id)
                intent.putExtra("CHAT_TITLE", report.mainCategoryName)
                if (report.conversation != null && report.conversation!!.has("_id")) {
                    val conversationId = report.conversation!!.getString("_id")
                    intent.putExtra("CONVERSATION_ID", conversationId)
                    intent.putExtra("REPORT_ID", report.id)
                    intent.putExtra("TYPE", "REPORT")
                    context!!.startActivity(intent)
                }

            }

            report_list_public_recycler_view.adapter = adapter
            val layoutManager = LinearLayoutManager(context!!)
            report_list_public_recycler_view.layoutManager = layoutManager
        } catch (e: Exception) {

        }
    }
}
