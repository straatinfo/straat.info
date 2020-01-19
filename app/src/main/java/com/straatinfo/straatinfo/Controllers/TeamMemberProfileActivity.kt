package com.straatinfo.straatinfo.Controllers

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.straatinfo.straatinfo.Models.TeamMember
import com.straatinfo.straatinfo.Models.User
import com.straatinfo.straatinfo.R
import com.straatinfo.straatinfo.Services.TeamService
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class TeamMemberProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_member_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = getString(R.string.team_member)// "USER PROFILE"

        this.populatePage()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun loadTeamMember (cb: (TeamMember?) -> Unit) {
        // intent.putExtra("TEAM_MEMBER", teamMemberJsonString)
        // intent.putExtra("FROM_ACTIVITY", "TEAM_MEMBER_REQUEST_ACTIVITY")

        val teamMemberString = intent.getStringExtra("TEAM_MEMBER")

        if (teamMemberString != null) {
            cb(TeamMember(JSONObject(teamMemberString)))
        } else {
            cb(null)
        }
    }

    private fun checkTeamLeader (teamId: String, userId: String, cb: (Boolean) -> Unit) {
        TeamService.getTeamLeader(teamId, userId)
            .subscribeOn(Schedulers.io())
            .subscribe {
                cb(it.has("_id"))
            }
            .run {  }
    }

    private fun populatePage () {
        val user = User()
        val userImg = findViewById<ImageView>(R.id.user_profile_img)
        val fnameTxt = findViewById<TextView>(R.id.user_profile_fname_txt)
        val lnameTxt = findViewById<TextView>(R.id.user_profile_lname_txt)
        val houseNumberTxt = findViewById<TextView>(R.id.user_profile_house_number_txt)
        val postCodeTxt = findViewById<TextView>(R.id.user_profile_post_code_txt)
        val streetNameTxt = findViewById<TextView>(R.id.user_profile_street_name_txt)
        val cityTxt = findViewById<TextView>(R.id.user_profile_city_txt)
        val emailTxt = findViewById<TextView>(R.id.user_profile_email_txt)
        val phoneNumberTxt = findViewById<TextView>(R.id.user_profile_phone_number_txt)
        val usernameTxt = findViewById<TextView>(R.id.user_profile_username_txt)

        val declineBtn = findViewById<Button>(R.id.user_profile_decline_team_request_btn)
        val acceptBtn = findViewById<Button>(R.id.user_profile_accept_team_request_btn)
        val removeUserBtn = findViewById<Button>(R.id.user_profile_remove_user_btn)

        val fromActivity = intent.getStringExtra("FROM_ACTIVITY")

        this.loadTeamMember { teamMember ->
            fnameTxt.text = teamMember?.userFname
            lnameTxt.text = teamMember?.userLname
            houseNumberTxt.text = teamMember?.userHouseNumber
            postCodeTxt.text = teamMember?.userPostCode
            streetNameTxt.text = teamMember?.userStreetName
            cityTxt.text = teamMember?.userCity
            emailTxt.text = teamMember?.userEmail
            phoneNumberTxt.text = teamMember?.userPhoneNumber
            usernameTxt.text = teamMember?.username

            if (teamMember?.userProfilePicUrl != null) {
                Picasso.get()
                    .load(teamMember?.userProfilePicUrl)
                    .resize(100, 100)
                    .centerCrop()
                    .into(userImg)
            }

            this.checkTeamLeader(teamMember?.teamId!!, user.id!!) { isLeader ->
                if (isLeader) {
                    if (fromActivity == "TEAM_MEMBER_REQUEST_ACTIVITY") {
                        declineBtn.visibility = View.VISIBLE
                        acceptBtn.visibility = View.VISIBLE
                        removeUserBtn.visibility = View.GONE
                    } else {
                        declineBtn.visibility = View.GONE
                        acceptBtn.visibility = View.GONE
                        removeUserBtn.visibility = View.VISIBLE
                    }
                } else {
                    declineBtn.visibility = View.GONE
                    acceptBtn.visibility = View.GONE
                    removeUserBtn.visibility = View.GONE
                }
            }

        }
    }

    fun acceptMember (userId: String, teamId: String, cb: () -> Unit) {
        TeamService.acceptTeamMember(userId, teamId)
            .subscribeOn(Schedulers.io())
            .subscribe { success ->
                var dialog = AlertDialog.Builder(this)
                when (success) {
                    true -> {
                        dialog.setTitle(getString(R.string.success))
                            .setMessage(getString(R.string.team_member_request_success))
                            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                                dialog.dismiss()
                                // onAcceptCompletion(true)
                                cb()
                            }
                            .show()
                    }
                    false -> {
                        dialog.setTitle(getString(R.string.error))
                            .setMessage(getString(R.string.team_member_request_failed))
                            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                                dialog.dismiss()
                                // onAcceptCompletion(false)
                                cb()
                            }
                            .show()
                    }
                }
            }
            .run {  }
    }

    fun declineMember (userId: String, teamId: String, cb: () -> Unit) {
        TeamService.declineTeamMember(userId, teamId)
            .subscribeOn(Schedulers.io())
            .subscribe { success ->
                var dialog = AlertDialog.Builder(this)
                when (success) {
                    true -> {
                        dialog.setTitle(getString(R.string.success))
                            .setMessage(getString(R.string.team_member_request_decline_success))
                            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                                dialog.dismiss()
                                // onAcceptCompletion(true)
                            }
                            .show()
                    }
                    false -> {
                        dialog.setTitle(getString(R.string.error))
                            .setMessage(getString(R.string.team_member_request_decline_failed))
                            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                                dialog.dismiss()
                                // onAcceptCompletion(false)
                            }
                            .show()
                    }
                }
            }
            .run {  }
    }

    fun removeMember (teamId: String, memeberId: String, cb: (Boolean) -> Unit) {
        TeamService.removeTeamMember(teamId, memeberId)
            .subscribeOn(Schedulers.io())
            .subscribe { success ->
                val dialog = AlertDialog.Builder(this)

                when (success) {
                    true -> {
                        dialog.setTitle(getString(R.string.success))
                            .setMessage(getString(R.string.user_profile_member_has_been_removed))
                            .setPositiveButton(R.string.ok) { dialog, which ->
                                dialog.dismiss()
                            }
                            .setOnDismissListener {
                                cb(true)
                            }
                            .show()
                    }
                    false -> {
                        dialog.setTitle(getString(R.string.error_failed))
                            .setMessage(getString(R.string.user_proflie_member_has_not_remove))
                            .setPositiveButton(R.string.ok) { dialog, which ->
                                dialog.dismiss()
                            }
                            .setOnDismissListener {
                                cb(false)
                            }
                            .show()
                    }
                }
            }
            .run {  }
    }

    fun declineRequest (view: View) {
        val teamMemberString = intent.getStringExtra("TEAM_MEMBER")
        if (teamMemberString != null) {
            val teamMember = TeamMember(JSONObject(teamMemberString))

            this.declineMember(teamMember?.userId!!, teamMember?.teamId!!) {
//                val intent = Intent(this, TeamMemberRequestActivity::class.java)
//                intent.putExtra("TEAM_ID", teamMember.teamId)
//                startActivity(intent)
                finish()
            }
        }
    }

    fun acceptRequest (view: View) {
        val teamMemberString = intent.getStringExtra("TEAM_MEMBER")
        if (teamMemberString != null) {
            val teamMember = TeamMember(JSONObject(teamMemberString))

            this.acceptMember(teamMember?.userId!!, teamMember?.teamId!!) {
//                val intent = Intent(this, TeamMemberRequestActivity::class.java)
//                intent.putExtra("TEAM_ID", teamMember.teamId)
//                startActivity(intent)
                finish()
            }
        }
    }

    fun removeMemberRequest (view: View) {
        val teamMemberString = intent.getStringExtra("TEAM_MEMBER")

        if (teamMemberString != null) {
            val teamMember = TeamMember(JSONObject(teamMemberString))

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.user_profile_remove_member_info))
                .setMessage(getString(R.string.user_profile_remove_member_confirmation))
                .setPositiveButton(R.string.ok) { dialog, which ->
                    this.removeMember(teamMember.teamId!!, teamMember.userId!!) { removed ->
                        if (removed) {
                            finish()
                        } else {
                            dialog.dismiss()
                        }
                    }
                }
                .setNegativeButton(R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}
