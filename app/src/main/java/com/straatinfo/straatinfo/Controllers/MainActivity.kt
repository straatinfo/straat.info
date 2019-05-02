package com.straatinfo.straatinfo.Controllers

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.straatinfo.straatinfo.Models.Host
import com.straatinfo.straatinfo.Models.Report
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.ReportService
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.lang.Exception

class MainActivity : FragmentActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMapClickListener,
    GoogleApiClient.ConnectionCallbacks {


    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var postion: MarkerOptions
    private lateinit var m: Marker
    private lateinit var reportList: ArrayList<Report>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.getLocationPoint(googleMap) { success ->
            if (success) {
               // this.loadReports(100.00)
            }
        }



    }

    override fun onConnected(p0: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onMapClick(p0: LatLng?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // private functions
    private fun getLocationPoint (googleMap: GoogleMap, onSuccess: (Boolean) -> Unit) {
        val host = Host(App.prefs.hostData)
        val long = host.long
        val lat = host.lat
        map = googleMap

        Log.d("LOCATION", "${long.toString()}, ${lat.toString()}")

        // Add a marker in Sydney and move the camera
        val point = LatLng(lat!!, long!!)
        postion = MarkerOptions().position(point).title("Marker in Sydney")
        // m = map.addMarker(postion)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16.0f))

        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMarkerClickListener{
            true
        }

        val circleOptions = CircleOptions()
        // Specifying the center of the circle
        circleOptions.center(point)

        // Radius of the circle
        circleOptions.radius(200.toDouble())

        // Border color of the circle
        circleOptions.strokeColor(0x30ff0000)

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000)

        // Border width of the circle
        circleOptions.strokeWidth(2F)

        map.addCircle(circleOptions)

        this.loadReports(100.00)


        onSuccess(true)
    }

    private fun loadReports (radius: Double) {
        val host = Host(App.prefs.hostData)
        val long = host.long
        val lat = host.lat
        val userData = JSONObject(App.prefs.userData)
        Log.d("USER_DATA", App.prefs.userData)
        val user = User(JSONObject(App.prefs.userData))
        val userId = user.id
        ReportService.getNearReport(userId!!, long!!, lat!!, radius)
            .subscribeOn(Schedulers.io())
            .subscribe { reports ->
                Log.d("REPORTS_LIST", reports.toString())

                for (count in 1..reports.length()) {
                    try {
                        val i = count - 1

                        Log.d("REPORT", reports[i].toString())

                        val report = Report(reports[i] as JSONObject)

                        // reportList.add(report)

                        val reportPos = LatLng(report.lat!! + (i.toDouble() * 0.00002), report.long!! + (i.toDouble() * 0.00002))

                        var reportMarker = MarkerOptions()
                        reportMarker.position(reportPos)

                        map.addMarker(reportMarker)

                    }
                    catch (e: Exception) {
                        Log.d("REPORT_POPULATION_ERROR", e.localizedMessage)
                    }
                }
            }
            .run {  }
    }

    fun setMarker (pos: LatLng) {
        m.position = pos
    }

}
