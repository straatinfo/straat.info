package com.straatinfo.straatinfo.Controllers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
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
import com.google.android.gms.maps.model.*
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.ReportService
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.lang.Exception
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.support.v4.content.ContextCompat
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.opengl.Visibility
import android.provider.MediaStore
import android.provider.Settings
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.NavigationView
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
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.view.View
import android.widget.*
import com.straatinfo.straatinfo.Models.*
import com.straatinfo.straatinfo.Services.CategoryService
import com.straatinfo.straatinfo.Services.MediaService
import com.straatinfo.straatinfo.Services.UtilService
import com.straatinfo.straatinfo.Utilities.LOCATION_RECORD_CODE
import kotlinx.android.synthetic.main.activity_home.drawer_layout
import kotlinx.android.synthetic.main.activity_home.nav_view
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.style
import org.json.JSONArray
import java.io.IOException


class MainActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMarkerDragListener,
    GoogleApiClient.ConnectionCallbacks,
    NavigationView.OnNavigationItemSelectedListener {


    lateinit var locationManager: LocationManager
    var mainCategories = mutableListOf<MainCategory>()
    var mainCatList = mutableListOf("Select Main Category")
    var subCategories = mutableListOf<SubCategory>()
    var subCatList = mutableListOf("Select Sub Category")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        this.init()
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

    override fun onMarkerClick(p0: Marker?): Boolean {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        // p0!!.showInfoWindow()
        Log.d("CLICK", p0.toString())
        if (p0!!.tag != null) return false
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        this.navigationHandler(item)
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
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_RECORD_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION", "Permission has been denied by user")
                    UtilService.makeRequest(this, LOCATION_RECORD_CODE)
                } else {
                    Log.d("PERMISSION", "Permission has been granted by user")

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
        Log.d("MARKER_MOVE", p0.toString())
        UtilService.geocode(p0!!.position.longitude, p0!!.position.latitude)
            .subscribeOn(Schedulers.io())
            .subscribe { result ->
                reportCurrentLoc.text = this.parseGeocodeData(result)
                this.setLocation(this.parseGeocodeData(result))
                this.setLongLat(p0!!.position.longitude, p0!!.position.latitude)
            }
            .run {  }
    }



    // private functions
    private fun reload() {
        val returnIntent = Intent(this, MainActivity::class.java)
        startActivity(returnIntent)
        finish()
    }
    private fun init() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        ViewCompat.setLayoutDirection(toolbar, ViewCompat.LAYOUT_DIRECTION_RTL)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        toolbar.setNavigationOnClickListener(navigationOnClickListener())
        nav_view.setNavigationItemSelectedListener(this)

    }

    private fun navigationOnClickListener() = View.OnClickListener {
        if (drawer_layout.isDrawerOpen(Gravity.END)) {
            drawer_layout.closeDrawer(Gravity.END)
        } else {
            drawer_layout.openDrawer(Gravity.END)
        }
        // Toast.makeText(this, "toggle", Toast.LENGTH_LONG).show()
    }

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
        map.setOnMarkerClickListener(this)

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

        this.loadReports(1000.00)


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
                            .position(reportPos)
                            .title(report.mainCategoryName)
                            .snippet(report.title)



                        if (report.reportTypeCode != null && report.reportTypeCode!!.toLowerCase() == "b") {
                            reportMarker.icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_pointer_b))
                        } else if (report.reportTypeCode != null && report.reportTypeCode!!.toLowerCase() == "c") {
                            reportMarker.icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_pointer_c))
                        } else {
                            reportMarker.icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_pointer_a))
                        }


                        val customInfoWindow = CustomInfoWindowGoogleMap(this)


                        map.setInfoWindowAdapter(customInfoWindow)

                        val marker = map.addMarker(reportMarker)
                        marker.tag = report


                        // marker.showInfoWindow()

                        // map!!.moveCamera(CameraUpdateFactory.newLatLng(reportPos))
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

    private fun navigationHandler (item: MenuItem) {
        // Handle navigation view item clicks here.
        Log.d("ITEM_ID", item.itemId.toString())
        if (item.itemId == this.activityViewId) {
            if (drawer_layout.isDrawerOpen(Gravity.END)) {
                drawer_layout.closeDrawer(Gravity.END)
            } else {
                drawer_layout.isDrawerOpen(Gravity.END)
            }
        } else {
            when (item.itemId) {
                R.id.nav_home -> {
                    val navMain = Intent(this, MainActivity::class.java)
                    startActivity(navMain)

                    finish()
                }
                R.id.nav_my_team -> {
                    val myTeam = Intent(this, MyTeamActivity::class.java)
                    startActivity(myTeam)
                    finish()
                }
                else -> {
                    if (drawer_layout.isDrawerOpen(Gravity.END)) {
                        drawer_layout.closeDrawer(Gravity.END)
                    } else {
                        drawer_layout.isDrawerOpen(Gravity.END)
                    }
                }

            }
        }

        drawer_layout.closeDrawer(GravityCompat.END)
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

    private fun checkPermissions () {

    }

    private fun getHostCoordinates (): LatLng {
        var latLng: LatLng
        val hostData = App.prefs.hostData
        val host = Host(hostData)


        return LatLng(host.lat!!, host.long!!)
    }

    @SuppressLint("MissingPermission")
    private fun getUserCoordinates (done: (point: LatLng) -> Unit) {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (hasGps || hasNetwork) {
            if (hasGps) {
                Log.d("GPS_LOCATION", "hasGPS")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object: LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        Log.d("LOC", location.toString())
                        if (location != null) {
                            Log.d("GPS_LOCATION", "${location.latitude.toString()}, ${location.longitude}" )
                            locationGps = location
                            val point = LatLng(locationGps!!.latitude, locationGps!!.longitude)
                            // this.loadReportPointer(point)
                            // done(point)
                        }
                    }

                    override fun onProviderEnabled(provider: String?) {
                        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderDisabled(provider: String?) {
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
                }
            }

            if (hasNetwork) {
                Log.d("NETWOR_LOCATION", "hasNetwork")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object: LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

                        if (location != null) {
                            Log.d("NETWORK_LOCATION", "${location.latitude.toString()}, ${location.longitude}" )
                            locationNetwork = location
                        }
                    }

                    override fun onProviderEnabled(provider: String?) {
                        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderDisabled(provider: String?) {
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
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun getMainCategoryA (completion: () -> Unit) {
        val hostData = App.prefs.hostData
        val host = Host(hostData)
        CategoryService.getMainCategories(host.id!!, "nl")
            .subscribeOn(Schedulers.io())
            .subscribe { mclist ->
                Log.d("MAIN_CATEGORY_LIST", mclist.toString())

                var overige: MainCategory? = null
                var count = 0

                for (i in 0..(mclist.length() - 1)) {
                    val mc = MainCategory(mclist[i] as JSONObject)
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

    fun loadReportPointer (point: LatLng) {
        var reportMarker = MarkerOptions()
            .position(point)
            .draggable(true)
            .icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_pointer_d))


        this.map.addMarker(reportMarker)
        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16.0f))
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
        reportTypeABtn.setOnClickListener {
            this.showSendReportTypeA()
        }
    }

    // send report type A section
    fun showSendReportTypeA () {
        this.getMainCategoryA {
            this.loadRpAMainCatSpinner()
        }
        sendReportP2Frame.visibility = View.GONE
        sendReportTypeAFrame.visibility = View.VISIBLE
        Log.d("NOTIF", notificationSwitch.isChecked.toString())
        sendReportTypeATB.setNavigationOnClickListener {
            this.reload()
        }
    }

    fun loadRpAMainCatSpinner () {
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
        subCatList = mutableListOf("Select Sub Category")
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
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            // imageview!!.setImageBitmap(thumbnail)
            setImage(thumbnail, btnClicked)
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
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")

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

        getUserCoordinates { point ->
            this.map.clear()
            val hostCoordinates = this.getHostCoordinates()
            this.loadMapCircle(hostCoordinates)
            // val pointToUse = point
            val pointToUse = hostCoordinates // for testing
            Log.d("POINT_LOADED", pointToUse.toString())
            UtilService.geocode(pointToUse.longitude, pointToUse.latitude)
                .subscribeOn(Schedulers.io())
                .subscribe { result ->
                    Log.d("POINT", this.parseGeocodeData(result))
                    reportCurrentLoc.text = this.parseGeocodeData(result)
                    this.setLocation(this.parseGeocodeData(result))
                    this.setLongLat(pointToUse.longitude, pointToUse.latitude)
                }
                .run {  }
            this.loadReportPointer(pointToUse)
            showSendReportP1()
        }

    }

    // input checking and update
    fun evaluateInput () {
        if (
            this._location != null &&
            this._mainCatId != null &&
            this._description != null &&
            // reportDetailsTxtBox.text.toString() != "" &&
            this._long != null &&
            this._lat != null
        ) {
            this._isValid = true
        }
    }
    fun setButtonEnablement () {
        if (this._isValid) {
            sendReportABtn.setBackgroundColor(getResources().getColor(R.color.color_green))
        } else {
            sendReportABtn.setBackgroundColor(getResources().getColor(R.color.color_blue_dark))
        }
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
    }

    fun promptUser () {
        Toast.makeText(this, "Please supply missing fields", Toast.LENGTH_LONG).show()
    }

    // actual sending
    fun onSendReportTypeA (view: View) {
        if (!_isValid) promptUser()
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
            jsonReport.put("_reportType", "5a7888bb04866e4742f74955")

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
                    when(it) {
                        true -> {
                            reload()
                        }
                        else -> {
                            Toast.makeText(this, "An error occured while sending the report", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .run {  }
        }
    }

}
