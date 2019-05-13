package com.straatinfo.straatinfo.Controllers

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.straatinfo.straatinfo.Models.Host
import com.straatinfo.straatinfo.Models.Media
import com.straatinfo.straatinfo.Models.Registration
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.AuthService
import com.straatinfo.straatinfo.Services.MediaService
import com.straatinfo.straatinfo.Services.UtilService
import com.straatinfo.straatinfo.Utilities.LOCATION_RECORD_CODE
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_registration_create_team.*
import java.io.IOException
import java.lang.Exception

class RegistrationCreateTeamActivity : AppCompatActivity() {

    var CAMERA = 1
    var GALLERY = 2
    lateinit var userInput: Registration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_create_team)

        this.userInput = Registration()
        userInput.teamEmail = null
        userInput.teamId = null
        userInput.teamName = null
        val host = Host(App.prefs.hostData)
        userInput.hostId = host.id!!

        Log.d("LOADING_INPUT_FROM_CT1", userInput.toRequestObject().toString())
        Log.d("LOADING_INPUT_FROM_CT2", userInput.toJson().toString())
        userInput.saveInput()
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

    fun onGoBack (view: View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
        finish()
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
            val view = findViewById<ImageView>(R.id.teamLogoImg)
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

                            userInput.teamPhotoJson = media.jsonData
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
        val pictureDialogItems = arrayOf("Select photo from gallery")

        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                // 1 -> takePhotoFromCamera()
            }
        }

        pictureDialog.show()
    }

    fun register (view: View) {
        val teamName = teamNameTxt.text.toString()
        val teamEmail = teamEmailAddressTxt.text.toString()

        if (teamEmail != "" && teamName != "") {
            userInput.teamName = teamName
            userInput.teamEmail = teamEmail
            userInput.password = App.prefs.registrationPassword
            val registrationInput = userInput.toRequestObject()
            AuthService.registerRx(registrationInput)
                .subscribeOn(Schedulers.io())
                .subscribe {
                    when (it) {
                        true -> {
                            val activity = Intent(this, LoginActivity::class.java)
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
        } else {
            val dialog = UtilService.showDefaultAlert(this, "Error", "Please complete the form")
            dialog.show()
        }
    }
}
