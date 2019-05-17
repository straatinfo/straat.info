package com.straatinfo.straatinfo.Services

import com.straatinfo.straatinfo.Utilities.EMAL_REGEX
import com.straatinfo.straatinfo.Utilities.PASSWORD_REGEX
import com.straatinfo.straatinfo.Utilities.UK_PHONE_REGEX

object RegexService {
    fun testEmail (email: String) : Boolean {
        val regex = Regex(EMAL_REGEX)
        return regex.containsMatchIn(email)
    }
    fun testPhoneNumber (phoneNumber: String) : Boolean {
        val regex = Regex(UK_PHONE_REGEX)
        var pass = false
        if (regex.matches(phoneNumber)) pass = true
        if (phoneNumber.length > 1) {
            if (phoneNumber[0] != '0' || phoneNumber[1] != '6') pass = false
        }

        return pass
    }
    fun testpassword (password: String) : Boolean {
        val regex = Regex(PASSWORD_REGEX)
        return regex.matches(password)
    }
}