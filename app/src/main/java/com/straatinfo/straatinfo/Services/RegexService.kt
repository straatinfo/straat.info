package com.straatinfo.straatinfo.Services

import com.straatinfo.straatinfo.Utilities.EMAL_REGEX
import com.straatinfo.straatinfo.Utilities.UK_PHONE_REGEX

object RegexService {
    fun testEmail (email: String) : Boolean {
        val regex = Regex(EMAL_REGEX)
        return regex.containsMatchIn(email)
    }
    fun testPhoneNumber (phoneNumber: String) : Boolean {
        val regex = Regex(UK_PHONE_REGEX)
        return regex.matches(phoneNumber)
    }
}