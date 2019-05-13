package com.straatinfo.straatinfo.Models

class Team {
    var id: String? = null
    var email: String? = null
    var name: String? = null

    constructor(id: String, email: String, name: String) {
        this.id = id
        this.email = email
        this.name = name
    }
}