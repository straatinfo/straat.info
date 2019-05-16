package com.straatinfo.straatinfo.Controllers

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import com.straatinfo.straatinfo.Models.Host
import com.straatinfo.straatinfo.Models.Registration
import com.straatinfo.straatinfo.Models.Team
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.AuthService
import com.straatinfo.straatinfo.Services.RegexService
import com.straatinfo.straatinfo.Services.TeamService
import com.straatinfo.straatinfo.Services.UtilService
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.registration_step1.*
import kotlinx.android.synthetic.main.registration_step3.*
import org.json.JSONArray
import org.json.JSONObject


class RegistrationActivity : AppCompatActivity() {

    lateinit var torDialog: Dialog
    lateinit var userInput: Registration
    var isTorAccepted = false
    var isEmailValid = false
    var isPhoneNumberValid = false
    var teamList = mutableListOf<Team>()
    var teamLabels = mutableListOf<String>()
    var selectedTeamPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        this.userInput = Registration()
        userInput.loadInput()
        val hostData = App.prefs.hostData
        val host = Host(hostData)
        userInput.hostId = host.id!!
        userInput.saveInput()
        this.loadPage1()
        teamLabels = mutableListOf(getString(R.string.choose_team))
//        when (userInput.getPage()) {
//            1 -> this.loadPage1()
//            2 -> this.loadPage2()
//            3 -> this.loadPage3()
//            else -> this.loadPage1()
//        }
        this.registrationNavData.setOnClickListener(View.OnClickListener {
            this.loadPage1()

            // val termsAndCond : Button = this.registration_terms_and_condition
            // termsAndCond.setOnClickListener(onClicked())


        })

        this.registrationNavReporter.setOnClickListener(View.OnClickListener {
            this.loadPage2()
        })

        this.registrationNavTeam.setOnClickListener(View.OnClickListener {
            this.loadPage3()
        })
    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun layout(res: Int): View {
        val regStep1View : LayoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view : View = regStep1View.inflate(res, null)
        return view
    }

    private fun onClicked() = View.OnClickListener {
        this.termsAndConditionDialog()
        Toast.makeText(this, "hello", Toast.LENGTH_LONG).show()
    }

    private fun termsAndConditionDialog() {
        val dialog = Dialog(this)
        val inflater : LayoutInflater = this.layoutInflater
        val customView : View = inflater.inflate(R.layout.registration_terms_and_cond, null)
        dialog.setContentView(customView)

        Log.d("TAG", "TAG")

        val window : Window = dialog.window as Window
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.torDialog = dialog
        dialog.show()
    }

    private fun switchLayout (layout: Int) {
        val layout1 = layout(R.layout.registration_step1)

        val layout2 = layout(R.layout.registration_step2)

        val layout3 = layout(R.layout.registration_step3)
        when (layout) {
            2 -> {
                layout1.visibility = View.GONE
                layout2.visibility = View.VISIBLE
                layout3.visibility = View.GONE
            }

            3 -> {
                layout1.visibility = View.GONE
                layout2.visibility = View.GONE
                layout3.visibility = View.VISIBLE
            }

            else -> {
                layout1.visibility = View.VISIBLE
                layout2.visibility = View.GONE
                layout3.visibility = View.GONE
            }
        }
    }

    private fun loadPage1 () {
        userInput.setRegPage(1)
        val switchableLayout = this.switchableLayout
        switchableLayout.removeAllViews()
        switchableLayout.addView((layout(R.layout.registration_step1)))

        val edtFirstName = findViewById<EditText>(R.id.edtFirstName)
        val edtLastName = findViewById<EditText>(R.id.edtLastName)
        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtUsernameRandom = findViewById<TextView>(R.id.usernameIdTxt)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPostalCode = findViewById<EditText>(R.id.edtPostalCode)
        val edtHouseNumber = findViewById<EditText>(R.id.edtPostalNumber)
        val edtStreetName = findViewById<EditText>(R.id.edtStreet)
        val edtCity = findViewById<EditText>(R.id.edtCity)
        val edtPhone = findViewById<EditText>(R.id.edtMobileNumber)
        val edtGender = findViewById<RadioGroup>(R.id.genderRb)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnNextStep = findViewById<Button>(R.id.btnNextStep)


        edtFirstName.setText(this.userInput.fname)
        edtLastName.setText(this.userInput.lname)
        edtUsername.setText(this.userInput.usernameInput)
        edtUsernameRandom.setText("_ID:${UtilService.randomStringGenerator(5)}")
        edtEmail.setText(this.userInput.email)
        edtPostalCode.setText(this.userInput.postalCode)
        edtHouseNumber.setText(this.userInput.houseNumber)
        edtStreetName.setText(this.userInput.streetName)
        edtCity.setText(this.userInput.city)
        edtPhone.setText(this.userInput.phoneNumber)
        edtPassword.setText(this.userInput.password)

        if (userInput.email != null && RegexService.testEmail(userInput.email!!)) {
            this.isEmailValid = true
        }

        if (userInput.phoneNumber != null && RegexService.testPhoneNumber(userInput.phoneNumber!!)) {
            this.isPhoneNumberValid = true
        }


        if (userInput.gender != null) {
            val maleRb = findViewById<RadioButton>(R.id.rbMale)
            val femaleRb = findViewById<RadioButton>(R.id.rbFemale)
            when (userInput.gender) {
                "M" -> {
                    maleRb.isChecked = true
                    femaleRb.isChecked = false
                }
                "F" -> {
                    maleRb.isChecked = false
                    femaleRb.isChecked = true
                }
            }
        }

        btnNextStep.isEnabled = this.checkPage1Validity()

        edtGender.setOnCheckedChangeListener { group, checkedId ->
            when (group.checkedRadioButtonId) {
                R.id.rbMale -> {
                    Log.d("RADIO_BUTTON", "MALE")
                    userInput.gender = "M"
                    userInput.saveInput()
                }
                else -> {
                    Log.d("RADIO_BUTTON", "FEMALE")
                    userInput.gender = "F"
                    userInput.saveInput()
                }
            }
            btnNextStep.isEnabled = this.checkPage1Validity()
        }

        edtFirstName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                userInput.fname = edtFirstName.text.toString()
                userInput.saveInput()
                btnNextStep.isEnabled = checkPage1Validity()
            }
        })

        edtLastName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                userInput.lname = edtLastName.text.toString()
                userInput.saveInput()
                btnNextStep.isEnabled = checkPage1Validity()
            }
        })

        edtUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                userInput.usernameInput = edtUsername.text.toString()
                userInput.username = edtUsername.text.toString() + edtUsernameRandom.text.toString()
                userInput.saveInput()
                btnNextStep.isEnabled = checkPage1Validity()
            }
        })

        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val email = edtEmail.text.toString()
                if (RegexService.testEmail(email)) {
                    userInput.email = email
                    userInput.saveInput()
                    isEmailValid = true

                } else {
                    isEmailValid = false
                }
                btnNextStep.isEnabled = checkPage1Validity()
            }
        })

        edtEmail.setOnFocusChangeListener { v, hasFocus ->
            val email = edtEmail.text.toString()
            if (!RegexService.testEmail(email) && !hasFocus) {
                isEmailValid = false
                Log.d("EMAIL_VALIDATION", "false")
                // Toast.makeText(this, "Invalid Email", Toast.LENGTH_LONG).show()
                val error = getString(R.string.error)
                val message = getString(R.string.error_invalid_email)
                val dialog = UtilService.showDefaultAlert(this, error, message)
                dialog.show()
            } else {
                userInput.email = email
                userInput.saveInput()
                isEmailValid = true
            }
            btnNextStep.isEnabled = checkPage1Validity()
        }

        edtPostalCode.setOnFocusChangeListener { v, hasFocus ->
            val postcode = edtPostalCode.text.toString()
            val houseNumber = edtHouseNumber.text.toString()
            if (postcode != "" &&  houseNumber != "") {
                this.getPostCode(postcode, houseNumber) { success, streetName, city ->
                    if (success && streetName != "" && city != "") {
                        edtStreetName.setText(streetName)
                        edtCity.setText(city)
                        userInput.postalCode = postcode
                        userInput.houseNumber = houseNumber
                        userInput.city = city
                        userInput.streetName = streetName
                        userInput.saveInput()
                    } else {
                        Log.d("POST_CODE_ERROR", "Invalid address")
                        val errorMessage = getString(R.string.error)
                        val invalidPostcode = getString(R.string.error_invalid_postal_code)
                        val dialog = UtilService.showDefaultAlert(this, errorMessage, invalidPostcode)

                        edtStreetName.setText("")
                        edtCity.setText("")
                        userInput.city = null
                        userInput.streetName = null
                        // Toast.makeText(this, "ongeldig adres", Toast.LENGTH_LONG).show()
                        dialog.show()
                    }
                }
            } else if (postcode == "") {
                val error = getString(R.string.error)
                val missingPostCodeError = getString(R.string.error_invalid_postal_code)
                val dialog = UtilService.showDefaultAlert(this, error, missingPostCodeError)
                dialog.show()
            }
            btnNextStep.isEnabled = checkPage1Validity()
        }

        edtHouseNumber.setOnFocusChangeListener { v, hasFocus ->
            val postcode = edtPostalCode.text.toString()
            val houseNumber = edtHouseNumber.text.toString()
            val error = getString(R.string.error)
            val message = getString(R.string.error_invalid_postal_code)
            val dialog = UtilService.showDefaultAlert(this, error, message)
            if (postcode != "" &&  houseNumber != "") {
                this.getPostCode(postcode, houseNumber) { success, streetName, city ->
                    if (success && streetName != "" && city != "") {
                        edtStreetName.setText(streetName)
                        edtCity.setText(city)
                        userInput.postalCode = postcode
                        userInput.houseNumber = houseNumber
                        userInput.city = city
                        userInput.streetName = streetName
                        userInput.saveInput()
                    } else {
                        Log.d("POST_CODE_ERROR", "Invalid address")
                        edtStreetName.setText("")
                        edtCity.setText("")
                        userInput.city = null
                        userInput.streetName = null
                        // Toast.makeText(this, "ongeldig adres", Toast.LENGTH_LONG).show()
                        dialog.show()
                    }
                }
            } else if (houseNumber == "") {
                dialog.show()
            }
            btnNextStep.isEnabled = checkPage1Validity()
        }


        edtPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val phoneNumber = edtPhone.text.toString()
                if (!RegexService.testPhoneNumber(phoneNumber)) {
                    userInput.phoneNumber = phoneNumber
                    userInput.saveInput()
                    isPhoneNumberValid = false
                } else {
                    userInput.phoneNumber = phoneNumber
                    userInput.saveInput()
                    isPhoneNumberValid = true
                }
                btnNextStep.isEnabled = checkPage1Validity()
            }
        })

        edtPhone.setOnFocusChangeListener { v, hasFocus ->
            val phoneNumber = edtPhone.text.toString()
            if (phoneNumber.length < 10) {
                val error = getString(R.string.error)
                val message = getString(R.string.error_invalid_mobile_number_length)
                val dialog = UtilService.showDefaultAlert(this, error, message)
                userInput.phoneNumber = phoneNumber
                userInput.saveInput()
                isPhoneNumberValid = false
                // Toast.makeText(this, "Invalid Phone number", Toast.LENGTH_LONG).show()
                dialog.show()
            } else if (!RegexService.testPhoneNumber(phoneNumber) && !hasFocus) {
                userInput.phoneNumber = phoneNumber
                userInput.saveInput()
                isPhoneNumberValid = false
                val error = getString(R.string.error)
                val message = getString(R.string.error_invalid_mobile_number)
                val dialog = UtilService.showDefaultAlert(this, error, message)

                // Toast.makeText(this, "Invalid Phone number", Toast.LENGTH_LONG).show()
                dialog.show()

            } else {
                userInput.phoneNumber = phoneNumber
                userInput.saveInput()
                isPhoneNumberValid = true
            }

            btnNextStep.isEnabled = checkPage1Validity()
        }

        edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val password = edtPassword.text.toString()
                userInput.password = password
                userInput.saveInput()
                App.prefs.registrationPassword = password
                btnNextStep.isEnabled = checkPage1Validity()
            }
        })

        edtPassword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && (edtPassword.text.toString() == "" || edtPassword.text.toString().length < 8)) {
                val error = getString(R.string.error)
                val message = getString(R.string.error_invalid_password)
                val dialog = UtilService.showDefaultAlert(this, error, message)
                dialog.show()
            } else {
                val password = edtPassword.text.toString()
                userInput.password = password
                userInput.saveInput()
                btnNextStep.isEnabled = checkPage1Validity()
            }
        }

    }

    private fun loadPage2() {
        userInput.setRegPage(2)
        if (this.checkPage1Validity()) {
            switchableLayout.removeAllViews()
            switchableLayout.addView((layout(R.layout.registration_step2)))
        }
    }

    private fun loadPage3() {
        userInput.setRegPage(3)
        if (userInput.isVolunteer != null) {
            switchableLayout.removeAllViews()
            switchableLayout.addView((layout(R.layout.registration_step3)))

            val teamSelection = findViewById<RadioGroup>(R.id.onTeamCreationSelect)

            teamSelection.setOnCheckedChangeListener{ group, checkedId ->
                when (group.checkedRadioButtonId) {
                    R.id.joinTeamRb -> {
                        this.loadTeamList {
                            this.loadTeamListSpinner()
                        }
                    }
                    else -> {
                        this.loadCreateTeam()
                    }
                }
            }

        }
    }

    private fun loadCreateTeam() {
        val intent = Intent(this, RegistrationCreateTeamActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun callDialog (title: String, message: String) {
        UtilService.showDefaultAlert(this, title, message)
    }

    private fun getPostCode(postalCode: String, houseNumber: String, completion: (success: Boolean, streetName: String, city: String) -> Unit) {
        UtilService.postcode(postalCode, houseNumber) { error, postCodeData ->
            if (error == null || error == "") {
                Log.d("POST_CODE", postCodeData.toString())
                val _embedded = if (postCodeData!!.has("_embedded")) postCodeData!!.getJSONObject("_embedded") else JSONObject()
                val addresses = if (_embedded!!.has("addresses")) _embedded!!.getJSONArray("addresses") else JSONArray()
                val address = if (addresses.length() > 0) addresses[0] as JSONObject else JSONObject()
                val cityData = if (address!!.has("city")) address!!.getJSONObject("city") else JSONObject()
                val city = if (cityData.has("label")) cityData!!.getString("label") else ""
                val street = if (address!!.has("street")) address!!.getString("street") else ""
                completion(true, street, city)
            } else {
                completion(false, "", "")
            }
        }
    }

    private fun checkPage1Validity (): Boolean {
        Log.d("USER_INPUT", userInput.toJson().toString())

        val value = (
            userInput.gender != null && userInput.gender != ""
            && userInput.tac
            && userInput.fname != null && userInput.fname != ""
            && userInput.lname != null && userInput.lname != ""
            && userInput.email != null && userInput.email != ""
            && userInput.username != null && userInput.username != ""
            && userInput.postalCode != null && userInput.postalCode != ""
            && userInput.houseNumber != null && userInput.houseNumber != ""
            && userInput.streetName != null && userInput.streetName != ""
            && userInput.city != null && userInput.city != ""
            && userInput.phoneNumber != null && userInput.phoneNumber != ""
            && userInput.password != null && userInput.password != "" && userInput.password!!.length >= 8
            && isEmailValid && isPhoneNumberValid && isTorAccepted
        )

        if (value) userInput.saveInput()

        return value
    }

    private fun loadTeamList (completion: () -> Unit) {
        val hostData = App.prefs.hostData
        val host = Host(hostData)
        val hostId = host.id
        val isVolunteer = userInput.isVolunteer

        TeamService.getListOfTeamPerHostWithFilter(hostId!!, isVolunteer!!)
            .subscribeOn(Schedulers.io())
            .subscribe {  teamListData ->
                for (i in 0..(teamListData.length() - 1)) {
                    val teamData = teamListData[i] as JSONObject
                    val name = teamData.getString("teamName")
                    val email = teamData.getString("teamEmail")
                    val id = teamData.getString("_id")
                    val team = Team(id, email, name)
                    teamList.add(i, team)
                    teamLabels.add(i + 1, team.name!!)
                }
                completion()
            }
            .run {  }
    }

    fun loadTeamListSpinner () {
        val uaa = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, this.teamLabels)
        val spinner = findViewById<Spinner>(R.id.teamListSpinner)
        spinner.adapter = uaa

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                Log.d("TEAMLISTSPIN", position.toString())
                Log.d("TEAMLISTSPIN", parent.selectedItemPosition.toString())
                if (position > 0) {
                    selectedTeamPos = position
                }

                if (position == 0 && selectedTeamPos > 0) {
                    teamListSpinner.setSelection(selectedTeamPos)
                    val team = teamList[selectedTeamPos - 1]
                    userInput.teamId = team.id
                    userInput.teamName = team.name
                    userInput.teamEmail = team.email
                }

                if (position > 0 && selectedTeamPos > 0) {
                    val team = teamList[selectedTeamPos - 1]
                    userInput.teamId = team.id
                    userInput.teamName = team.name
                    userInput.teamEmail = team.email
                }

                userInput.saveInput()

            } // to close the onItemSelected


            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }


    fun onTermsAndConditionBtnClick (view: View) {
        this.termsAndConditionDialog()
    }

    fun onCancelClick (view: View) {
        this.torDialog.hide()
        this.isTorAccepted = false
        this.userInput.tac = false
        this.userInput.saveInput()
        val btnNextStep = findViewById<Button>(R.id.btnNextStep)
        btnNextStep.isEnabled = checkPage1Validity()
        val error = getString(R.string.error)
        val message = getString(R.string.error_tac_not_accepted)
        val errorDialog = UtilService.showDefaultAlert(this, error, message)

        errorDialog.show()
    }

    fun onYesClick (view:View) {
        this.torDialog.hide()
        this.isTorAccepted = true
        this.userInput.tac = true
        this.userInput.saveInput()
        val btnNextStep = findViewById<Button>(R.id.btnNextStep)
        btnNextStep.isEnabled = checkPage1Validity()
    }

    fun goToStep2 (view: View) {
        this.loadPage2()
    }

    fun goToStep3 (view: View) {
        this.loadPage3()
    }

    fun setAsVolunteer (view: View) {
        this.userInput.isVolunteer = true
        this.userInput.saveInput()
        this.loadPage3()
    }

    fun setAsNonVolunteer (view: View) {
        this.userInput.isVolunteer = false
        this.userInput.saveInput()
        this.loadPage3()
    }

    fun register (view: View) {
        val hostData = App.prefs.hostData
        val host = Host(hostData)
        userInput.hostId = host.id
        val registrationInput = userInput.toRequestObject()
        Log.d("REGISTRATION_INPUTS", registrationInput.toString())
        AuthService.registerRx(registrationInput)
            .subscribeOn(Schedulers.io())
            .subscribe {
                when (it) {
                    true -> {
                        var activity: Intent
                        if (userInput.isVolunteer != null && userInput.isVolunteer!!) {
                            activity = Intent(this, MainActivity::class.java)
                        } else {
                            activity = Intent(this, LoginActivity::class.java)
                        }
                        startActivity(activity)
                        finish()
                    }
                    else -> {
                        val builder = android.app.AlertDialog.Builder(this)
                        builder
                            .setTitle("Create Team Error")
                            .setMessage("An error occured while processing data")

                        builder.create()
                        builder.show()
                    }
                }
            }
            .run {  }
    }
}

