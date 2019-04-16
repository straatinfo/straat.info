package com.straatinfo.straatinfo.Models

class User {
    var error: String? = null


    constructor(fetchingError: String?) {
        this.error = fetchingError
    }
}