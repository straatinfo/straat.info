package com.straatinfo.straatinfo.Controllers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.straatinfo.straatinfo.R
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.lang.Exception
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.support.v4.content.ContextCompat
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.model.BitmapDescriptor
import com.straatinfo.straatinfo.Adapters.CustomInfoWindowGoogleMap
import kotlinx.android.synthetic.main.app_bar_main.*
import android.view.View
import android.widget.*
import com.straatinfo.straatinfo.Models.*
import com.straatinfo.straatinfo.Services.*
import com.straatinfo.straatinfo.Utilities.*
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_home.drawer_layout
import kotlinx.android.synthetic.main.activity_home.nav_view
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.io.IOException
import java.util.jar.Manifest


class MainActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMarkerDragListener,
    GoogleMap.OnInfoWindowClickListener,
    GoogleApiClient.ConnectionCallbacks,
    NavigationView.OnNavigationItemSelectedListener {


    lateinit var locationManager: LocationManager
    // lateinit var progressBar: ProgressBar
    var mainCategories = mutableListOf<MainCategory>()
    var mainCatList = mutableListOf<String>()
    var subCategories = mutableListOf<SubCategory>()
    var subCatList = mutableListOf<String>()
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var userLocation: Location? = null
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var postion: MarkerOptions
    private lateinit var reportList: ArrayList<Report>
    var mainCatSelectedPos: Int = 0
    var subCatPos: Int = 0
    private var activityViewId = R.id.nav_home
    private val GALLERY = 1
    private val CAMERA = 2
    var btnClicked = 0

    // report inputs
    var _hasSubCatId = false
    var _mainCatId: String? = null
    var _subCatId: String? = null
    var _emergencyNotif: Boolean = false
    var _description: String? = null
    var _img1: Media? = null
    var _img2: Media? = null
    var _img3: Media? = null
    var _location: String? = null
    var _long: Double? = null
    var _lat: Double? = null
    var _isValid: Boolean = false
    var btnChose: Int? = null

    var _isPeopleInvolved: Boolean = false
    var _isVehicleInvolved: Boolean = false
    var _numPeopleInvolved: Int = 0
    var _numVehicleInvolved: Int = 0
    var _peopleInvolveDesc: String? = null
    var _vehicleInvolveDesc: String? = null


    var locationHost: Host? = null

    var  isCameraIsFocused = 0

    var myMarker: Marker? = null

    var markerHasDragged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        this.isCameraIsFocused = 0
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // mapFragment.getMapAsync(this)
        mainCatList = mutableListOf(getString(R.string.report_select_main_category))
        subCatList = mutableListOf(getString(R.string.report_select_sub_category))

        // progressBar = findViewById(R.id.mainActivityProgressBar)

        val user = User(JSONObject(App.prefs.userData))
        var socket = App.socket
        if (socket == null) {
            App.socket = App.connectToSocket(user)
        }
        this.locationHost = user.host
        mapFragment.getMapAsync(this)
        // progressBar.visibility = View.VISIBLE
        this.init {
            AuthService.userRefresh(user.email!!)
                .subscribeOn(Schedulers.io())
                .subscribe{
                    // progressBar.visibility = View.GONE
                    this.checkActiveTeam()
                }
                .run{}
        }


        LocalBroadcastManager.getInstance(this).registerReceiver(this.reportDataChangeReceiver, IntentFilter(BROADCAST_REPORT_DATA_CHANGE))

    }

    private val reportDataChangeReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            reload()
        }
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
        Log.d("MAP_GETTING_READY", "MAP_GETTING_READY")
        this.getLocationPoint(!markerHasDragged, googleMap) { success ->
            if (success) {
                Log.d("MAP_GETTING_READY", "SUCCESS")
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

    override fun onMarkerClick(marker: Marker?): Boolean {
        Log.d("CLICK", marker!!.isInfoWindowShown.toString())
        return marker!!.tag == null
    }

    override fun onInfoWindowClick(marker: Marker?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        Log.d("INFO_WINDOW", marker.toString())
        if (marker!!.tag != null) {
            val report = marker!!.tag as Report
            val intent = Intent(this, ReportInformationActivity::class.java)
            intent.putExtra("PREVIIOUS_LOCATION", "MAIN")
            intent.putExtra("REPORT_ID", report.id)
            startActivity(intent)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val user = User(JSONObject(App.prefs.userData))
        val refreshAlways = user.team_is_approved == null || !user.team_is_approved!!
        NavigationService.navigationHandler(this, item, activityViewId, drawer_layout, refreshAlways)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reports -> {
                val intent = Intent(this, ReportsActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("NUM_PERMISSIONS", grantResults.count().toString())
        Log.d("NUM_PERMISSIONS", grantResults.toString())
        when (requestCode) {
            LOCATION_RECORD_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // Log.d("PERMISSION", "Permission has been denied by user")
                    // UtilService.makeRequest(this, LOCATION_RECORD_CODE)

                } else if (grantResults.isEmpty() || (grantResults.count() > 1 && grantResults[1] != PackageManager.PERMISSION_GRANTED)) {
                    Log.d("PERMISSION", "Permission has been denied by user")
                    UtilService.makeRequest(this, LOCATION_RECORD_CODE)
                } else if (grantResults.isEmpty() || (grantResults.count() > 2 && grantResults[2] != PackageManager.PERMISSION_GRANTED)) {
                    Log.d("PERMISSION", "Permission has been denied by user")
                    UtilService.makeRequest(this, LOCATION_RECORD_CODE)
                } else {
                    Log.d("PERMISSION", "Permission has been granted by user")
                    this.map.isMyLocationEnabled = true
                    this.map.uiSettings.isMyLocationButtonEnabled = true
                    this.map.uiSettings.isMapToolbarEnabled = true
                    this.map.uiSettings.isCompassEnabled = false
                }
            }
        }
    }


    override fun onMarkerDragStart(p0: Marker?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMarkerDrag(p0: Marker?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMarkerDragEnd(p0: Marker?) {
        this.markerHasDragged = true
        Log.d("MARKER_MOVE", p0.toString())
        UtilService.geocode(p0!!.position.longitude, p0!!.position.latitude)
            .flatMap { result ->
                this.evaluateHostLocation(result)
                Observable.just(result)
            }
            .subscribeOn(Schedulers.io())
            .subscribe { result ->

                val countryCode = this.getCountryFromGeoCode(result)

                if (countryCode != "NL") {
                    val locationError = AlertDialog.Builder(this)
                        .setTitle(getString(R.string.action_not_allowed))
                        .setMessage(getString(R.string.error_reporting_outside_netherlands))
                        .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, i ->
                            val returnIntent = Intent(this, MainActivity::class.java)
                            startActivity(returnIntent)
                            finish()
                        })

                    locationError.show()
                } else {
                    reportCurrentLoc.text = this.parseGeocodeData(result)
                    sendReportTypeALocation.text = getString(R.string.location_col) + this.parseGeocodeData(result)
                    this.setLocation(this.parseGeocodeData(result))


                    this.setLongLat(p0!!.position.longitude, p0!!.position.latitude)
                }

            }
            .run {  }
    }



    // private functions
    private fun evaluateHostLocation (geoCode: JSONObject) {
        val hostName = this.getHostNameFromGeoCode(geoCode)

        Log.d("POST_CODE_HOST_NAME", this.getHostNameFromGeoCode(geoCode))

        this.getHostByName(hostName) { host ->

            this.getMainCategoryA(host?.id!!) {
                if (this.locationHost?.id != host?.id) {
                    this.locationHost = host
                    this.mainCatSelectedPos = 0
                    this.subCatPos = 0
                    subCatSpinner.visibility = View.GONE
                    this.loadRpAMainCatSpinner()
                }
            }
        }
    }

    private fun getHostByName (hostName: String, completion: (host: Host?) -> Unit) {
        HostService.getHostByName(hostName)
            .subscribeOn(Schedulers.io())
            .subscribe {
                if (it.has("_id")) {
                    val host = Host(it)
                    completion(host)
                } else {
                    val user = User()
                    completion(user.host)
                }
            }
            .run {  }
    }
    private fun reload() {
        val returnIntent = Intent(this, MainActivity::class.java)
        startActivity(returnIntent)
        finish()
    }
    private fun init(cb: () -> Unit) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Straat.Info"

        ViewCompat.setLayoutDirection(toolbar, ViewCompat.LAYOUT_DIRECTION_RTL)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        toolbar.setNavigationOnClickListener(navigationOnClickListener())
        nav_view.setNavigationItemSelectedListener(this)

        // progressBar.visibility = View.GONE
        cb()
    }

    private fun navigationOnClickListener() = View.OnClickListener {
        if (drawer_layout.isDrawerOpen(Gravity.END)) {
            drawer_layout.closeDrawer(Gravity.END)
        } else {
            drawer_layout.openDrawer(Gravity.END)
        }
        // Toast.makeText(this, "toggle", Toast.LENGTH_LONG).show()
    }

    private fun getLocationPoint (fromActivity: Boolean, googleMap: GoogleMap, onSuccess: (Boolean) -> Unit) {
        getUserCoordinates (fromActivity && !markerHasDragged) { point ->
            val user = User()
            val host = user.host
            Log.d("host", host!!.id)
            val long = host!!.long
            val lat = host!!.lat
            map = googleMap

            Log.d("LOCATION", "${long.toString()}, ${lat.toString()}")

            // Add a marker in Sydney and move the camera
            val hostPoint = LatLng(lat!!, long!!)
            // postion = MarkerOptions().position(point).title("Marker in Sydney")
            // m = map.addMarker(postion)
            Log.d("IS_CAM_FOCUSED", isCameraIsFocused.toString())
            val gpsPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)

            if (isCameraIsFocused < 2) {
                Log.d("FOCUSING_CAM", "YES")
                this.loadReports(10000.00)
                val circleOptions = CircleOptions()
                // Specifying the center of the circle
                circleOptions.center(hostPoint)
                // Radius of the circle
                circleOptions.radius(200.toDouble())

                // Border color of the circle
                circleOptions.strokeColor(0x30ff0000)

                // Fill color of the circle
                circleOptions.fillColor(0x30ff0000)

                // Border width of the circle
                circleOptions.strokeWidth(2F)

                map.addCircle(circleOptions)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, MAP_ZOOM))
                Log.d("CAMERA FOCUS", isCameraIsFocused.toString())
                isCameraIsFocused += 1
                Log.d("CAMERA FOCUS", isCameraIsFocused.toString())

                this.loadReportPointer(point)
            }

            map.getUiSettings().setZoomControlsEnabled(true)
            map.setOnMarkerClickListener(this)

            // this.loadReportPointer(point)

            if (gpsPermission == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true
                map.uiSettings.isMapToolbarEnabled = true
                map.uiSettings.isCompassEnabled = false
            } else {
                UtilService.makeRequest(this, LOCATION_RECORD_CODE)
            }
            onSuccess(true)
        }
    }

    private fun loadReports (radius: Double) {
        val host = User().host
        val long = host?.long
        val lat = host?.lat
        Log.d("USER_LOCATION", "$long, $lat")
        Log.d("USER_DATA", App.prefs.userData)
        val user = User(JSONObject(App.prefs.userData))
        val userId = user.id
        var reportId: String? = intent.getStringExtra("REPORT_ID")
        ReportService.getNearReport(userId!!, long!!, lat!!, radius, reportId)
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
                            .position(reportPos)
                            .title(report.mainCategoryName)
                            .snippet(report.title)


                        if (report.status != null && report.status!!.toLowerCase() == "expired") {
                            reportMarker.icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_pointer_e))
                        } else if (report.status != null && report.status!!.toLowerCase() == "done") {
                            reportMarker.icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_pointer_a))
                        } else if (report.status != null && report.status!!.toLowerCase() == "new") {
                            reportMarker.icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_pointer_c))
                        } else {
                            reportMarker.icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_pointer_b))
                        }


                        val customInfoWindow = CustomInfoWindowGoogleMap(this)


                        map.setInfoWindowAdapter(customInfoWindow)

                        val marker = map.addMarker(reportMarker)
                        marker.tag = report
                        map.setOnInfoWindowClickListener(this)

                        // @TODO temporary fix need to be more specific by using id
                        val reportId = intent.getStringExtra("REPORT_ID")
                        if (reportId != null && reportId != "" && report.id == reportId) {
                             marker.showInfoWindow()

                             map!!.moveCamera(CameraUpdateFactory.newLatLng(reportPos))
                        }
                    }
                    catch (e: Exception) {
                        Log.d("REPORT_POPULATION_ERROR", e.localizedMessage)
                    }
                }
            }
            .run {  }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap =
            Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun loadMapCircle (point: LatLng) {
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

        this.map.addCircle(circleOptions)
    }

    private fun loadMapCircleWithMap (mMap: GoogleMap, point: LatLng) {
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

        mMap.addCircle(circleOptions)
    }

    private fun checkActiveTeam () {
        val user = User(JSONObject(App.prefs.userData))
        if (user.team_is_approved == null || !user.team_is_approved!!) {
            val title = getString(R.string.send_report)
            val message = getString(R.string.non_volunteer_waiting_to_be_approved)
            val dialog = AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok)) { dialog, i ->
                    dialog.dismiss()
                }

            dialog.show()

            val sendReportBtn = findViewById<Button>(R.id.sendReportBtn)

            sendReportBtn.isEnabled = false
        }
    }

    private fun getHostCoordinates (): LatLng {
        var latLng: LatLng
        val host = User().host
        val long = host?.long
        val lat = host?.lat


        return LatLng(lat!!, long!!)
    }

//    private fun getHostByName (hostName: String, cb: (Boolean, Host) -> Unit) {
//        HostService.getHostByName(hostName)
//            .subscribeOn(Schedulers.io())
//            .subscribe {
//                when (it.has("_id")) {
//                    true -> {
//                        val host = Host(it)
//                        cb(true, host)
//                    }
//                    false -> cb(false, Host())
//                }
//            }
//            .run {  }
//    }

    // @SuppressLint("MissingPermission")
    private fun getUserCoordinates (fromActivity: Boolean, done: (point: LatLng) -> Unit) {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val gpsPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (gpsPermission != PackageManager.PERMISSION_GRANTED) {
            // UtilService.makeRequest(this, LOCATION_RECORD_CODE)
            val user = User()
            val host = user.host
            val point = LatLng(host?.lat!!, host.long!!)
            done(point)
        } else {
            if (hasGps || hasNetwork) {
                if (hasGps) {
                    Log.d("GPS_LOCATION", "hasGPS")
                    try {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object: LocationListener {
                            override fun onLocationChanged(location: Location?) {
                                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                Log.d("LOC", location.toString())
                                if (location != null && fromActivity && !markerHasDragged) {
                                    Log.d("NETWORK_LOCATION", "${location.latitude.toString()}, ${location.longitude}" )
                                    locationNetwork = location
                                    val point = LatLng(location.latitude!!, location.longitude!!)
                                    done(point)
                                }
                            }

                            override fun onProviderEnabled(provider: String?) {
                                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onProviderDisabled(provider: String?) {
                                val user = User()
                                val host = user.host
                                val point = LatLng(host?.lat!!, host.long!!)
                                done(point)
                                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                        })

                        val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (localGpsLocation != null) {
                            locationGps = localGpsLocation
                            val point = LatLng(locationGps!!.latitude, locationGps!!.longitude)
                            done(point)
                        } else {
                            val user = User()
                            val host = user.host
                            val point = LatLng(host?.lat!!, host.long!!)
                            done(point)
                        }
                    } catch (e: Exception) {
                        val user = User()
                        val host = user.host
                        val point = LatLng(host?.lat!!, host.long!!)
                        done(point)
                    }

                }
                if (hasNetwork) {
                    try {
                        Log.d("NETWOR_LOCATION", "hasNetwork")
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object: LocationListener {
                            override fun onLocationChanged(location: Location?) {
                                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

                                if (location != null && fromActivity && markerHasDragged) {
                                    Log.d("NETWORK_LOCATION", "${location.latitude.toString()}, ${location.longitude}" )
                                    locationNetwork = location
                                    val point = LatLng(location.latitude!!, location.longitude!!)
                                    done(point)
                                }
                            }

                            override fun onProviderEnabled(provider: String?) {
                                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onProviderDisabled(provider: String?) {
                                val user = User()
                                val host = user.host
                                val point = LatLng(host?.lat!!, host.long!!)
                                done(point)
                                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }
                        })

                        val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (localNetworkLocation != null) {
                            locationNetwork = localNetworkLocation
                            val point = LatLng(locationNetwork!!.latitude, locationNetwork!!.longitude)
                            done(point)
                        } else {
                            val user = User()
                            val host = user.host
                            val point = LatLng(host?.lat!!, host.long!!)
                            done(point)
                        }
                    } catch (e: Exception) {
                        val user = User()
                        val host = user.host
                        val point = LatLng(host?.lat!!, host.long!!)
                        done(point)
                    }
                }

                if (locationGps != null && locationNetwork != null) {
                    if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                        userLocation = locationGps
                    } else {
                        userLocation = locationNetwork
                    }

                    val point = LatLng(userLocation!!.latitude, userLocation!!.longitude)
                    done(point)
                }
            } else {
                // startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                Log.d("NO_GPS", "NO_GPS")
                val user = User()
                val host = user.host
                val point = LatLng(host?.lat!!, host?.long!!)
                Log.d("NO_GPS_LOCATION", "${host?.lat!!}, ${host?.long!!}")
                done(point)
            }
        }
    }

    private fun getMainCategoryA (hostId: String, completion: () -> Unit) {
        val language = getString(R.string.language)
        this.mainCategories = mutableListOf()
        this.mainCatList = mutableListOf(getString(R.string.report_select_main_category))
        CategoryService.getHostMainCategories(hostId, language)
            .subscribeOn(Schedulers.io())
            .subscribe { mainCategoryList ->
                Log.d("MAIN_CATEGORY_LIST", mainCategoryList.toString())

                var overige: MainCategory? = null
                var count = 0

                for (i in 0 until mainCategoryList.length()) {
                    val mc = MainCategory(mainCategoryList[i] as JSONObject)
                    if (mc.name!!.toLowerCase() == "others" || mc.name!!.toLowerCase() == "overige") {
                        overige = mc
                    } else {
                        Log.d("LOADIN_MC", mc.subCategories.toString())
                        this.mainCategories.add(count, mc)
                        this.mainCatList.add(count + 1, mc.name!!)
                        count++
                    }
                }

                if (overige != null) {
                    this.mainCategories.add(count, overige)
                    this.mainCatList.add(count + 1, overige.name!!)
                }


                Log.d("MAIN_CATEGORY_LIST_P", this.mainCategories.toString())
                Log.d("MAIN_CAT_LIST", this.mainCatList.toString())

                completion()
            }
            .run {  }
    }

    private fun getMainCategoryB (completion: () -> Unit) {
        val language = getString(R.string.language)
        val code = "B"
        CategoryService.getGeneralMainCategories(code, language)
            .subscribeOn(Schedulers.io())
            .subscribe { mainCategoryList ->
                Log.d("MAIN_CATEGORY_LIST", mainCategoryList.toString())

                var overige: MainCategory? = null
                var count = 0

                for (i in 0 until mainCategoryList.length()) {
                    val mc = MainCategory(mainCategoryList[i] as JSONObject)
                    if (mc.name!!.toLowerCase() == "others" || mc.name!!.toLowerCase() == "overige") {
                        overige = mc
                    } else {
                        Log.d("LOADIN_MC", mc.subCategories.toString())
                        this.mainCategories.add(count, mc)
                        this.mainCatList.add(count + 1, mc.name!!)
                        count++
                    }
                }

                if (overige != null) {
                    this.mainCategories.add(count, overige)
                    this.mainCatList.add(count + 1, overige.name!!)
                }


                Log.d("MAIN_CATEGORY_LIST_P", this.mainCategories.toString())
                Log.d("MAIN_CAT_LIST", this.mainCatList.toString())

                completion()
            }
            .run {  }
    }


    private fun parseGeocodeData (jsonObject: JSONObject): String {
        try {
            Log.d("LOCATION", jsonObject.toString())
            var location = ""
            val results = jsonObject.getJSONArray("results") as JSONArray
            val item1 = results[0] as JSONObject
            location = item1.getString("formatted_address")
            return location
        }
        catch (e: Exception) {
            Log.d("ERROR", e.localizedMessage)
            return "Unknown location"
        }
    }

    private fun getPostCodeFromGeoCode (jsonObject: JSONObject): String {
        try {
            var postCode = ""
            val results = jsonObject.getJSONArray("results") as JSONArray
            val result1 = results[0] as JSONObject
            val addressComponents = result1.getJSONArray("address_components")
            for (i in 0 until addressComponents.length()) {
                var item = addressComponents[i] as JSONObject
                Log.d("POST_CODE", item.toString())
                if (item.has("types")) {
                    var types = item.getJSONArray("types")
                    var stringify = types.toString()
                    if (stringify.contains("postal_code") && item.has("long_name")) {
                        postCode = item.getString("long_name")
                    }
                }
            }

            Log.d("POST_CODE", postCode)


            return postCode
        }
        catch (e: Exception) {
            Log.d("POST_CODE_ERROR", e.localizedMessage)
            return ""
        }
    }

    private fun getHostNameFromGeoCode (jsonObject: JSONObject): String{
        try {
            var hostName = ""
            val results = jsonObject.getJSONArray("results") as JSONArray
            val result1 = results[0] as JSONObject
            val addressComponents = result1.getJSONArray("address_components")
            for (i in 0 until addressComponents.length()) {
                var item = addressComponents[i] as JSONObject
                Log.d("POST_CODE", item.toString())
                if (item.has("types")) {
                    var types = item.getJSONArray("types")
                    var stringify = types.toString()
                    if (stringify.contains("locality") && item.has("long_name")) {
                        hostName = item.getString("long_name")
                    }
                }
            }

            Log.d("POST_CODE_HOST_NAME", hostName)


            return hostName
        } catch (e: Exception) {
            Log.d("POST_CODE_ERROR", e.localizedMessage)
            return ""
        }
    }

    private fun getCountryFromGeoCode (jsonObject: JSONObject): String {
        try {
            var countryCode = ""
            val results = jsonObject.getJSONArray("results") as JSONArray
            val result1 = results[0] as JSONObject
            val addressComponents = result1.getJSONArray("address_components")
            for (i in 0 until addressComponents.length()) {
                var item = addressComponents[i] as JSONObject
                Log.d("POST_CODE", item.toString())
                if (item.has("types")) {
                    var types = item.getJSONArray("types")
                    var stringify = types.toString()
                    if (stringify.contains("country") && item.has("short_name")) {
                        countryCode = item.getString("short_name")
                    }
                }
            }

            Log.d("POST_CODE_COUNTRY_NAME", countryCode)


            return countryCode
        } catch (e: Exception) {
            Log.d("POST_CODE_ERROR", e.localizedMessage)
            return ""
        }
    }

    fun loadReportPointer (point: LatLng) {
        // this.map.clear()

        val user = User()
        val host = user.host
        val hostPoint = LatLng(host?.lat!!, host.long!!)
        // this.loadMapCircleWithMap(this.map, hostPoint)

        if (this.myMarker == null) {
            var reportMarker = MarkerOptions()
                .position(point)
                .draggable(false)
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_pointer_d))

            this.myMarker = this.map.addMarker(reportMarker)
        } else {
            this.myMarker?.position = point
        }
        // this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, MAP_ZOOM))
        this.map.setOnMarkerDragListener(this)
    }

    // send report page 1 section
    fun showSendReportP1 () {
        sendReportP1Frame.visibility = View.VISIBLE
        sendReportBtn.visibility = View.GONE
        sendReportPage1TB.setNavigationOnClickListener {
            this.reload()
        }

        goToPage2Btn.setOnClickListener {
            this.showSendReportP2()
        }
    }


    // send report page 2 section
    fun showSendReportP2 () {
        sendReportP1Frame.visibility = View.GONE
        sendReportP2Frame.visibility = View.VISIBLE
        sendReportPage2TB.setNavigationOnClickListener {
            this.reload()
        }
        reportTypeBBtn.visibility = View.VISIBLE
        reportTypeABtn.setOnClickListener {
            this.showSendReportTypeA()
        }

        reportTypeBBtn.setOnClickListener {
            this.showSendReportTypeB()
        }
    }

    // send report type A section
    fun showSendReportTypeA () {
        var host: Host? = null
        if (locationHost != null) {
            host = locationHost
        } else {
            val user = User()
            host = user.host
        }
        this.getMainCategoryA (host?.id!!) {
            this.loadRpAMainCatSpinner()
        }
        sendReportP2Frame.visibility = View.GONE
        sendReportTypeAFrame.visibility = View.VISIBLE
        Log.d("NOTIF", notificationSwitch.isChecked.toString())
        notificationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            when(isChecked) {
                true -> {
                    setEmergencyNotif(true)
                    val dialog = AlertDialog.Builder(this)
                        .setTitle(R.string.send_report_is_urgent_title)
                        .setMessage(R.string.send_report_is_urgent_desc)
                        .setPositiveButton(getString(R.string.ok)) { dialog, i ->
                            dialog.dismiss()
                        }

                    dialog.show()
                }
                false -> setEmergencyNotif(false)
            }
        }
        sendReportTypeATB.setNavigationOnClickListener {
            this.reload()
        }
    }

    fun loadRpAMainCatSpinner () {
        _hasSubCatId = false
        val uaa = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, this.mainCatList)
        this.mainCatSpinner.adapter = uaa
        mainCatSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                Log.d("MAINCATSPIN", position.toString())
                Log.d("MAINCATSPIN", parent.selectedItemPosition.toString())
                if (position > 0) {
                    mainCatSelectedPos = position
                }

                if (position == 0 && mainCatSelectedPos > 0) {
                    unSetSubCatId()
                    val mc = mainCategories[mainCatSelectedPos - 1]
                    setMainCatId(mc.id!!)
                    Log.d("MAIN_C", mc.toString())
                    Log.d("SUB_CAT", mc.subCategories.toString())
                    if (mc.subCategories != null && mc.subCategories!!.length() > 0) {
                        subCatPos = 0
                        loadRpASubCatSpinner(mc)
                    } else {
                        subCatSpinner.visibility = View.GONE
                    }
                    mainCatSpinner.setSelection(mainCatSelectedPos)
                }

                if (position > 0 && mainCatSelectedPos > 0) {
                    unSetSubCatId()
                    val mc = mainCategories[mainCatSelectedPos - 1]
                    setMainCatId(mc.id!!)
                    Log.d("MAIN_C", mc.toString())
                    Log.d("SUB_CAT", mc.subCategories.toString())
                    if (mc.subCategories != null && mc.subCategories!!.length() > 0) {
                        _hasSubCatId = true
                        subCatPos = 0
                        loadRpASubCatSpinner(mc)
                    } else {
                        _hasSubCatId = false
                        subCatSpinner.visibility = View.GONE
                    }
                }

            } // to close the onItemSelected


            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    fun populateSubCat (scJSON: JSONArray, cb: () -> Unit) {
        var overige: SubCategory? = null
        var count = 0
        this.unSetSubCatId()
        for (i in 0..(scJSON.length()) - 1) {
            val scO = SubCategory(scJSON[i] as JSONObject)
            if (scO.name == "others" || scO.name == "overige") {
                overige = scO
            } else {
                subCategories.add(count, scO)
                subCatList.add(count + 1, scO.name!!)
                count++
            }
        }

        if (overige != null) {
            subCategories.add(count, overige)
            subCatList.add(count + 1, overige.name!!)
        }

        cb()
    }

    fun loadRpASubCatSpinner (mc: MainCategory) {
        subCategories = mutableListOf()
        subCatList = mutableListOf(getString(R.string.report_select_sub_category))
        populateSubCat(mc.subCategories as JSONArray) {
            subCatSpinner.visibility = View.VISIBLE
            val subAa = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, this.subCatList)
            this.subCatSpinner.adapter = subAa

            subCatSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if (position > 0) {
                        subCatPos = position
                        val sc = subCategories[subCatPos - 1]
                        setSubCatId(sc.id!!)
                    }

                    if (position == 0 && subCatPos > 0) {
                        subCatSpinner.setSelection(subCatPos)
                        val sc = subCategories[subCatPos - 1]
                        setSubCatId(sc.id!!)
                    }

                } // to close the onItemSelected


                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }
    }


    // send picture section
    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI) as Bitmap

                    // Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
                    setImage(bitmap, btnClicked)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to process photo", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            if (data != null) {
                if (data!!.extras != null) {
                    val thumbnail = data!!.extras!!.get("data") as Bitmap
                    // imageview!!.setImageBitmap(thumbnail)
                    setImage(thumbnail, btnClicked)
                }
            }
            // saveImage(thumbnail)
            // Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    fun setImage (img: Bitmap, viewId: Int) {
        try {
            val view = findViewById<ImageView>(viewId)
            MediaService.publicUpload(img, "title")
                .subscribeOn(Schedulers.io())
                .subscribe { data ->
                    try {
                        if (MediaService.errorMessage != null) {
                            Toast.makeText(this, MediaService.errorMessage, Toast.LENGTH_SHORT).show()
                        } else {
                            view.setImageBitmap(img)
                            view.scaleType = ImageView.ScaleType.CENTER_CROP
                            Log.d("UPLOAD_PHOTO_SUCCESS", data.toString())
                            val media = Media(data)
                            when (btnChose) {
                                R.id.photo1Btn -> this._img1 = media
                                R.id.photo2Btn -> this._img2 = media
                                R.id.photo3Btn -> this._img3 = media
                                R.id.send_report_type_b_photo_1_btn -> this._img1 = media
                                R.id.send_report_type_b_photo_2_btn -> this._img2 = media
                                R.id.send_report_type_b_photo_3_btn -> this._img3 = media
                                else -> this._img1 = media
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("SET_IMAGE", e.localizedMessage)
                        Toast.makeText(this, "Error on processing image", Toast.LENGTH_SHORT).show()
                    }
                }
                .run {  }
        } catch (e: Exception) {
            Log.d("SET_IMAGE", e.localizedMessage)
            Toast.makeText(this, "Error on processing image", Toast.LENGTH_SHORT).show()
        }
    }

    fun showPictureDialog (view: View) {
        val pictureDialog = AlertDialog.Builder(this)
        val title = getString(R.string.media_select_action)
        val fromGallery = getString(R.string.media_select_photo_from_gallery)
        val fromCamera = getString(R.string.medial_capture_photo_from_camera)
        pictureDialog.setTitle(title)
        val pictureDialogItems = arrayOf(fromGallery, fromCamera)

        btnChose = view.id
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }

        btnClicked = view.id

        pictureDialog.show()
    }

    // send report a info
    fun onCheckReportAInfo (view: View) {
        var title = getString(R.string.report_public_space)
        var message = getString(R.string.report_type_a_info)

        val dialog = UtilService.showDefaultAlert(this, title, message)

        dialog.show()
    }


    // on create report section

    fun onCreateReport (view: View) {
        this.resetForm()
        val reportDetailsTxt = findViewById(R.id.reportDetailsTxtBox) as TextView

        reportDetailsTxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                setDescription()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setDescription()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setDescription()
            }

        })


        UtilService.setLocationPermission(this, LOCATION_RECORD_CODE)

        getUserCoordinates (false) { point ->
            this.map.clear()
            val hostCoordinates = this.getHostCoordinates()
            this.loadMapCircle(hostCoordinates)
            val pointToUse = point
            // val pointToUse = hostCoordinates // for testing
            Log.d("POINT_LOADED", pointToUse.toString())
            UtilService.geocode(pointToUse.longitude, pointToUse.latitude)
                .flatMap { result ->
                    // add host here
                    this.evaluateHostLocation(result)
                    Observable.just(result)
                }
                .subscribeOn(Schedulers.io())
                .subscribe { result ->

                    val countryCode = this.getCountryFromGeoCode(result)

                    if (countryCode != "NL") {
                        val locationError = AlertDialog.Builder(this)
                            .setTitle(getString(R.string.action_not_allowed))
                            .setMessage(getString(R.string.error_reporting_outside_netherlands))
                            .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, i ->
                                val returnIntent = Intent(this, MainActivity::class.java)
                                startActivity(returnIntent)
                                finish()
                            })
                            .setOnDismissListener {
                                val returnIntent = Intent(this, MainActivity::class.java)
                                startActivity(returnIntent)
                                finish()
                            }

                        locationError.show()
                    } else {
                        Log.d("POINT", this.parseGeocodeData(result))
                        reportCurrentLoc.text = this.parseGeocodeData(result)
                        Log.d("POST_CODE", this.getPostCodeFromGeoCode(result))
                        sendReportTypeALocation.text = getString(R.string.location_col) + this.parseGeocodeData(result)
                        send_report_type_b_location.text = getString(R.string.location_col) + this.parseGeocodeData(result)
                        this.setLocation(this.parseGeocodeData(result))

                    }

                }
                .run {  }
            this.setLongLat(pointToUse.longitude, pointToUse.latitude)
            myMarker = null
            this.loadReportPointer(pointToUse)
            if (myMarker != null) myMarker?.isDraggable = true
            this.showSendReportP1()
        }

    }

    // input checking and update
    fun evaluateInput () {
        if (
            this._location != null &&
            this._mainCatId != null &&
            subCatValid() &&
            this._long != null &&
            this._lat != null
        ) {

            Log.d("SUB_CAT_ID", _hasSubCatId.toString() + " " + _subCatId)

            this._isValid = true
        }
    }
    fun setButtonEnablement () {
        Log.d("SUB_CAT_ID", _hasSubCatId.toString() + " " + _subCatId)
        sendReportABtn.isEnabled = this._isValid && subCatValid()
        send_report_type_b_send_report_btn.isEnabled = this._isValid
    }

    fun setMainCatId (id: String) {
        this._mainCatId = id
        this.evaluateInput()
        this.setButtonEnablement()
    }

    fun unsetMainCatId () {
        this._mainCatId = null
        this.evaluateInput()
        this.setButtonEnablement()
    }

    fun setSubCatId (id: String) {
        this._subCatId = id
        this.evaluateInput()
        this.setButtonEnablement()
    }

    fun unSetSubCatId () {
        this._subCatId = null
        this.evaluateInput()
        this.setButtonEnablement()
    }

    fun setLongLat (long: Double, lat: Double) {
        this._long = long
        this._lat = lat
        this.evaluateInput()
        this.setButtonEnablement()
    }

    fun setLocation (loc: String) {
        this._location = loc
        this.evaluateInput()
        this.setButtonEnablement()
    }

    fun unsetLocation () {
        this._location = null
        this.evaluateInput()
        this.setButtonEnablement()
    }

    fun setEmergencyNotif(isEmergency: Boolean) {
        this._emergencyNotif = isEmergency
        this.evaluateInput()
        this.setButtonEnablement()
    }

    fun setIsPeopleInvolve (isPeopleInvolved: Boolean) {
        this._isPeopleInvolved = isPeopleInvolved
        this.evaluateInput()
        this.setButtonEnablement()
    }

    fun setIsVehicleInvolve (isVehicleInvolve: Boolean) {
        this._isVehicleInvolved = isVehicleInvolve
        this.evaluateInput()
        this.setButtonEnablement()
    }

    fun setDescription() {
        if (reportDetailsTxtBox.text.toString() != "") {
            this._description = reportDetailsTxtBox.text.toString()
        } else {
            this._description = null
        }
        this.evaluateInput()
        this.setButtonEnablement()
    }

    fun unsetDesc() {
        this._description = null
        this.evaluateInput()
        this.setButtonEnablement()
    }

    fun setPeopleInvolveCount (count: Int) {
        _numPeopleInvolved = count
    }

    fun setVehicleInvolveCount (count: Int) {
        _numVehicleInvolved = count
    }

    fun loadNumberList (num: Int): MutableList<Int> {
        var numList = mutableListOf<Int>()
        for (i in 0 until num) {
            numList.add(i, i)
        }

        return numList
    }

    fun resetForm () {
        _mainCatId = null
        _subCatId = null
        _emergencyNotif = false
        _description = null
        _img1 = null
        _img2 = null
        _img3 = null
        _location = null
        _long = null
        _lat = null
        _isValid = false
        _isPeopleInvolved = false
        _isVehicleInvolved = false
        _peopleInvolveDesc = null
        _vehicleInvolveDesc = null
        _numPeopleInvolved = 0
        _numVehicleInvolved = 0
    }

    fun promptUser () {
        val title = getString(R.string.error)
        val message = getString(R.string.report_error_supply_missing_fields)
        // Toast.makeText(this, "Please supply missing fields", Toast.LENGTH_LONG).show()
        val dialog = UtilService.showDefaultAlert(this, title, message)
        dialog.show()
    }

    fun subCatValid () : Boolean {
        if (!_hasSubCatId) return true
        else {
            return _subCatId != null
        }
    }

    // actual sending
    fun onSendReportTypeA (view: View) {
        mainActivityProgressBar.visibility = View.VISIBLE
        sendReportTypeAFrame.visibility = View.GONE
        if (!_isValid && subCatValid()) promptUser()
        else {
            val user = User(JSONObject(App.prefs.userData))

            val jsonReport = JSONObject()
            jsonReport.put("title", "Public Space")
            jsonReport.put("description", this._description)
            jsonReport.put("location", this._location)
            jsonReport.put("lat", this._lat)
            jsonReport.put("long", this._long)
            jsonReport.put("_reporter", user.id)
            jsonReport.put("_host", user.host_id)
            jsonReport.put("_mainCategory", this._mainCatId)
            jsonReport.put("_reportType", REPORT_TYPE_A_ID)

            if (this._subCatId != null) jsonReport.put("_subCategory", this._subCatId)
            jsonReport.put("isUrgent", this._emergencyNotif)

            if (user.team_id != null) jsonReport.put("_team", user.team_id)

            var uploadedPhotos = JSONArray()

            if (this._img1 != null) {
                uploadedPhotos.put(uploadedPhotos.length(), this._img1!!.jsonData)
            }

            if (this._img2 != null) {
                uploadedPhotos.put(uploadedPhotos.length(), this._img2!!.jsonData)
            }

            if (this._img3 != null) {
                uploadedPhotos.put(uploadedPhotos.length(), this._img3!!.jsonData)
            }

            jsonReport.put("reportUploadedPhotos", uploadedPhotos)

            Log.d("REPORT_DETAILS", jsonReport.toString())

            ReportService.sendReportV2(jsonReport)
                .subscribeOn(Schedulers.io())
                .subscribe {
                    mainActivityProgressBar.visibility = View.GONE
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
                                    if (it.has("reportId")) returnIntent.putExtra("REPORT_ID", it.getString("reportId"))
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


    // REPORT TYPE B Section
    // send report a info
    fun onCheckReportBInfo (view: View) {
        var title = getString(R.string.report_suspicious_situation)
        var message = getString(R.string.report_type_b_info)

        val dialog = UtilService.showDefaultAlert(this, title, message)

        dialog.show()
    }

    // send report type B section
    fun showSendReportTypeB () {
        Log.d("GET_MAIN_CAT_B", "LOADING1")
        this.getMainCategoryB {
            Log.d("GET_MAIN_CAT_B", "LOADING")
            this.loadRpBMainCatSpinner()
        }
        sendReportP2Frame.visibility = View.GONE
        sendReportTypeAFrame.visibility = View.GONE
        send_report_type_b_frame.visibility = View.VISIBLE
        send_report_type_b_emergency_notif_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            when(isChecked) {
                true -> {
                    setEmergencyNotif(true)
                    val dialog = AlertDialog.Builder(this)
                        .setTitle(R.string.send_report_is_urgent_title)
                        .setMessage(R.string.send_report_is_urgent_desc)
                        .setPositiveButton(getString(R.string.ok)) { dialog, i ->
                            dialog.dismiss()
                        }

                    dialog.show()
                }
                false -> setEmergencyNotif(false)
            }
        }
        send_report_type_b_tb.setNavigationOnClickListener {
            this.reload()
        }
        loadPeopleInvolvedField()
        loadVehicleInvolvedField()
    }

    fun loadRpBMainCatSpinner () {
        val uaa = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, this.mainCatList)
        this.send_report_type_b_main_cat_spinner.adapter = uaa
        send_report_type_b_main_cat_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                Log.d("MAINCATSPIN", position.toString())
                Log.d("MAINCATSPIN", parent.selectedItemPosition.toString())
                if (position > 0) {
                    mainCatSelectedPos = position
                }

                if (position == 0 && mainCatSelectedPos > 0) {
                    val mc = mainCategories[mainCatSelectedPos - 1]
                    setMainCatId(mc.id!!)
                    Log.d("MAIN_C", mc.toString())
                    Log.d("SUB_CAT", mc.subCategories.toString())
                    if (mc.subCategories != null && mc.subCategories!!.length() > 0) {
                        subCatPos = 0
                        loadRpASubCatSpinner(mc)
                    } else {
                        subCatSpinner.visibility = View.GONE
                    }
                    send_report_type_b_main_cat_spinner.setSelection(mainCatSelectedPos)
                }

                if (position > 0 && mainCatSelectedPos > 0) {
                    val mc = mainCategories[mainCatSelectedPos - 1]
                    setMainCatId(mc.id!!)
                    Log.d("MAIN_C", mc.toString())
                    Log.d("SUB_CAT", mc.subCategories.toString())
                    if (mc.subCategories != null && mc.subCategories!!.length() > 0) {
                        subCatPos = 0
                        loadRpASubCatSpinner(mc)
                    } else {
                        subCatSpinner.visibility = View.GONE
                    }
                }

            } // to close the onItemSelected


            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    fun loadPeopleCountSpinner () {
        var peopleCountList = this.loadNumberList(101)
        val uaa = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, peopleCountList)
        this.send_report_type_b_number_of_people_involved_spinner.adapter = uaa
        this.send_report_type_b_number_of_people_involved_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val number = peopleCountList[position]
                setPeopleInvolveCount(number)

            } // to close the onItemSelected


            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    fun loadPeopleInvolvedField () {
        send_report_type_b_are_people_involved_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            setIsPeopleInvolve(isChecked)
            if (isChecked) {
                send_report_type_b_number_of_people_involved_spinner.visibility = View.VISIBLE
                send_report_type_b_people_involved_desc_txt_box.visibility = View.VISIBLE
                loadPeopleCountSpinner()
            } else {
                send_report_type_b_number_of_people_involved_spinner.visibility = View.GONE
                send_report_type_b_people_involved_desc_txt_box.visibility = View.GONE
                setPeopleInvolveCount(0)
                send_report_type_b_people_involved_desc_txt_box.setText("")
            }
        }
    }

    fun loadVehicleCountSpinner () {
        var vehicleCountList = this.loadNumberList(101)
        val uaa = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, vehicleCountList)
        this.send_report_type_b_number_of_vehicle_involved_spinner.adapter = uaa
        this.send_report_type_b_number_of_vehicle_involved_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val number = vehicleCountList[position]
                setVehicleInvolveCount(number)

            } // to close the onItemSelected


            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    fun loadVehicleInvolvedField () {
        send_report_type_b_are_vehicle_involved_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            setIsVehicleInvolve(isChecked)
            if (isChecked) {
                send_report_type_b_number_of_vehicle_involved_spinner.visibility = View.VISIBLE
                send_report_type_b_vehicle_involved_desc_txt_box.visibility = View.VISIBLE
                loadVehicleCountSpinner()
            } else {
                send_report_type_b_number_of_vehicle_involved_spinner.visibility = View.GONE
                send_report_type_b_vehicle_involved_desc_txt_box.visibility = View.GONE
                setVehicleInvolveCount(0)
                send_report_type_b_vehicle_involved_desc_txt_box.setText("")
            }
        }
    }

    fun onSendReportTypeB (view: View) {
        mainActivityProgressBar.visibility = View.VISIBLE
        sendReportTypeAFrame.visibility = View.GONE
        if (!_isValid) promptUser()
        else {
            val user = User(JSONObject(App.prefs.userData))

            val jsonReport = JSONObject()
            jsonReport.put("title", "Public Space")
            jsonReport.put("description", this.send_report_type_b_report_details_txt_box.text.toString())
            jsonReport.put("location", this._location)
            jsonReport.put("lat", this._lat)
            jsonReport.put("long", this._long)
            jsonReport.put("_reporter", user.id)
            jsonReport.put("_host", user.host_id)
            jsonReport.put("_mainCategory", this._mainCatId)
            jsonReport.put("_reportType", REPORT_TYPE_B_ID)

            jsonReport.put("isUrgent", this._emergencyNotif)

            jsonReport.put("isPeopleInvolved", this._isPeopleInvolved)
            jsonReport.put("isVehicleInvolved", this._isVehicleInvolved)
            jsonReport.put("peopleInvolvedCount", this._numPeopleInvolved)
            jsonReport.put("vehicleInvolvedCount", this._numVehicleInvolved)
            jsonReport.put("peopleInvolvedDescription", this.send_report_type_b_people_involved_desc_txt_box.text.toString())
            jsonReport.put("vehicleInvolvedDescription", this.send_report_type_b_vehicle_involved_desc_txt_box.text.toString())



            if (user.team_id != null) jsonReport.put("_team", user.team_id)

            var uploadedPhotos = JSONArray()

            if (this._img1 != null) {
                uploadedPhotos.put(uploadedPhotos.length(), this._img1!!.jsonData)
            }

            if (this._img2 != null) {
                uploadedPhotos.put(uploadedPhotos.length(), this._img2!!.jsonData)
            }

            if (this._img3 != null) {
                uploadedPhotos.put(uploadedPhotos.length(), this._img3!!.jsonData)
            }

            jsonReport.put("reportUploadedPhotos", uploadedPhotos)

            Log.d("REPORT_DETAILS", jsonReport.toString())

            ReportService.sendReportV2(jsonReport)
                .subscribeOn(Schedulers.io())
                .subscribe {
                    mainActivityProgressBar.visibility = View.GONE
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
                                    if (it.has("reportId")) returnIntent.putExtra("REPORT_ID", it.getString("reportId"))
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

}
