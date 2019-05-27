package com.straatinfo.straatinfo.Controllers

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.straatinfo.straatinfo.Models.Media
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.*
import com.straatinfo.straatinfo.Utilities.LOCATION_RECORD_CODE
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class TeamFormActivity : AppCompatActivity() {

    var CAMERA = 1
    var GALLERY = 2
    var profileImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_form)

        loadNavigation()
        loadFormSelection()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_RECORD_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION", "Permission has been denied by user")
                    UtilService.makeRequest(this, LOCATION_RECORD_CODE)
                } else if (grantResults.isEmpty() || (grantResults.count() > 1 && grantResults[1] != PackageManager.PERMISSION_GRANTED)) {
                    Log.d("PERMISSION", "Permission has been denied by user")
                    UtilService.makeRequest(this, LOCATION_RECORD_CODE)
                } else if (grantResults.isEmpty() || (grantResults.count() > 2 && grantResults[2] != PackageManager.PERMISSION_GRANTED)) {
                    Log.d("PERMISSION", "Permission has been denied by user")
                    UtilService.makeRequest(this, LOCATION_RECORD_CODE)
                } else {
                    Log.d("PERMISSION", "Permission has been granted by user")
                }
            }
        }
    }

    fun loadNavigation () {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    fun loadFormSelection () {
        var formType = intent.getStringExtra("FORM_TYPE")

        if (formType == "UPDATE") {
            loadUpdateForm()
        } else {
            loadCreateForm()
        }
    }

    fun loadUpdateForm () {
        var title = findViewById<TextView>(R.id.teamFormTitleTxt)
        var teamName = intent.getStringExtra("TEAM_NAME")
        var teamEmail = intent.getStringExtra("TEAM_EMAIL")
        var profile = intent.getStringExtra("TEAM_PROFILE_PIC")
        var teamId = intent.getStringExtra("TEAM_ID")

        var teamNameTxt = findViewById<TextView>(R.id.teamFormTeamNameTxt)
        var teamEmailTxt = findViewById<TextView>(R.id.teamFormTeamEmailTxt)
        var profileImg = findViewById<ImageView>(R.id.teamFormTeamLogoImg)
        var button = findViewById<Button>(R.id.teamFormButtonTxt)

        button.setOnClickListener { view ->
            this.onUpdateTeam(view)
        }

        title.text = getString(R.string.team_update_profile)

        teamNameTxt.text = teamName
        teamEmailTxt.text = teamEmail
        button.text = getString(R.string.team_update_profile)
        button.isEnabled = teamName != "" && teamEmail != "" && RegexService.testEmail(teamEmail)

        if (profile != null) {
            Log.d("PROFILE_IMG", profile)
            var _profle = JSONObject(profile)
            var secureUrl = _profle.getString("secure_url")

            Picasso.get().load(secureUrl).into(profileImg)
        }


        teamNameTxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                button.isEnabled = teamNameTxt.text.toString() != "" && teamEmailTxt.text.toString() != "" && RegexService.testEmail(teamEmailTxt.text.toString())
            }
        })
        teamNameTxt.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && teamNameTxt.text.toString() == "") {
                var dialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.registration_cannot_use_team_name))

                dialog.show()
                button.isEnabled = false
            } else if (!hasFocus && teamNameTxt.text.toString() != teamName) {
                AuthService.validateRegistrationInput("teamName", teamNameTxt.text.toString())
                    .subscribeOn(Schedulers.io())
                    .subscribe { valid ->
                        button.isEnabled = valid && teamEmailTxt.text.toString() != ""
                        if (!valid) {
                            var dialog = AlertDialog.Builder(this)
                                .setTitle(getString(R.string.error))
                                .setMessage(getString(R.string.registration_cannot_use_team_name))

                            dialog.show()
                        }
                    }
                    .run {  }
            }
        }
        teamEmailTxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                button.isEnabled = teamNameTxt.text.toString() != "" && teamEmailTxt.text.toString() != "" && RegexService.testEmail(teamEmailTxt.text.toString())
            }
        })
        teamEmailTxt.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && (teamEmailTxt.text.toString() == "" || !RegexService.testEmail(teamEmailTxt.text.toString()))) {
                var dialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.error_invalid_email))

                dialog.show()
                button.isEnabled = false
            }
        }

    }

    fun loadCreateForm () {
        var title = findViewById<TextView>(R.id.teamFormTitleTxt)
        var teamNameTxt = findViewById<TextView>(R.id.teamFormTeamNameTxt)
        var teamEmailTxt = findViewById<TextView>(R.id.teamFormTeamEmailTxt)
        var profileImg = findViewById<ImageView>(R.id.teamFormTeamLogoImg)
        var button = findViewById<Button>(R.id.teamFormButtonTxt)

        button.setOnClickListener { view ->
            this.onCreateTeam(view)
        }

        title.text = getString(R.string.team_create_new)
        button.text = getString(R.string.team_create_new)

        button.isEnabled = false

        teamNameTxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                button.isEnabled = teamNameTxt.text.toString() != "" && teamEmailTxt.text.toString() != "" && RegexService.testEmail(teamEmailTxt.text.toString())
            }
        })
        teamNameTxt.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && teamNameTxt.text.toString() == "") {
                var dialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.registration_cannot_use_team_name))

                dialog.show()
                button.isEnabled = false
            } else if (!hasFocus) {
                AuthService.validateRegistrationInput("teamName", teamNameTxt.text.toString())
                    .subscribeOn(Schedulers.io())
                    .subscribe { valid ->
                        button.isEnabled = valid && teamEmailTxt.text.toString() != ""
                        if (!valid) {
                            var dialog = AlertDialog.Builder(this)
                                .setTitle(getString(R.string.error))
                                .setMessage(getString(R.string.registration_cannot_use_team_name))

                            dialog.show()
                        }
                    }
                    .run {  }
            }
        }
        teamEmailTxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                button.isEnabled = teamNameTxt.text.toString() != "" && teamEmailTxt.text.toString() != "" && RegexService.testEmail(teamEmailTxt.text.toString())
            }
        })
        teamEmailTxt.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && (teamEmailTxt.text.toString() == "" || !RegexService.testEmail(teamEmailTxt.text.toString()))) {
                var dialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.error_invalid_email))

                dialog.show()
                button.isEnabled = false
            }
        }
    }

    fun onButtonClick (view: View) {
        var formType = intent.getStringExtra("FORM_TYPE")

        if (formType == "UPDATE") {
            onUpdateTeam(view)
        } else {
            onCreateTeam(view)
        }
    }

    fun onUpdateTeam (view: View) {
        var teamId = intent.getStringExtra("TEAM_ID")

        var teamNameTxt = findViewById<TextView>(R.id.teamFormTeamNameTxt)
        var teamEmailTxt = findViewById<TextView>(R.id.teamFormTeamEmailTxt)

        TeamService.updateTeam(teamId, teamNameTxt.text.toString(), teamEmailTxt.text.toString(), profileImage)
            .subscribeOn(Schedulers.io())
            .subscribe { success ->
                var dialog = AlertDialog.Builder(this)
                when (success) {
                    true -> {
                        dialog.setTitle(getString(R.string.success))
                            .setMessage(getString(R.string.team_profile_update_success))
                            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                                dialog.dismiss()
                                val intent = Intent(this, MyTeamActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                    }
                    false -> {
                        dialog.setTitle(getString(R.string.error))
                            .setMessage(getString(R.string.team_profile_update_failed))
                            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                                dialog.dismiss()
                            }
                    }
                }
                dialog.show()
            }
            .run {  }
    }

    fun onCreateTeam (view: View) {
        val user = User(JSONObject(App.prefs.userData))
        val isVolunteer = user.isVolunteer
        Log.d("USER_DATA", App.prefs.userData)
        Log.d("IS_VOLUNTEER", user.isVolunteer!!.toString())
        val userId = user.id
        var teamNameTxt = findViewById<TextView>(R.id.teamFormTeamNameTxt)
        var teamEmailTxt = findViewById<TextView>(R.id.teamFormTeamEmailTxt)

        TeamService.createTeam(userId!!, teamNameTxt.text.toString(), teamEmailTxt.text.toString(), user.host_id!!, isVolunteer!!, profileImage)
            .subscribeOn(Schedulers.io())
            .subscribe { success ->
                val dialog = AlertDialog.Builder(this)
                when (success) {
                    true -> {
                        dialog
                            .setTitle(getString(R.string.success))
                            .setMessage(getString(R.string.create_new_team_success))
                            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                                dialog.dismiss()
                                val intent = Intent(this, MyTeamActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                    }
                    false -> {
                        dialog
                            .setTitle(getString(R.string.error))
                            .setMessage(getString(R.string.create_new_team_failed))
                            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                                dialog.dismiss()
                            }
                    }
                }
                dialog.show()
            }
            .run {  }
    }

    // send picture section
    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
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
                    setImage(bitmap)

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
            setImage(thumbnail)
            // saveImage(thumbnail)
            // Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    fun setImage (img: Bitmap) {
        try {
            profileImage = img
            val view = findViewById<ImageView>(R.id.teamFormTeamLogoImg)
            view.setImageBitmap(img)
            view.scaleType = ImageView.ScaleType.CENTER_CROP
        } catch (e: Exception) {
            Log.d("SET_IMAGE", e.localizedMessage)
            Toast.makeText(this, "Error on processing image", Toast.LENGTH_SHORT).show()
        }
    }

    fun showPictureDialog (view: View) {
        val pictureDialog = android.app.AlertDialog.Builder(this)
        pictureDialog.setTitle(getString(R.string.media_select_action))
        val pictureDialogItems = arrayOf(getString(R.string.media_select_photo_from_gallery))

        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                // 1 -> takePhotoFromCamera()
            }
        }

        pictureDialog.show()
    }
}
