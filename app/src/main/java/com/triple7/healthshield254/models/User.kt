package com.triple7.healthshield254.models



class User {

    var username:String = ""
    var email:String = ""
    var password:String = ""
    var id:String = ""

    constructor(username: String, email: String, password: String, id: String) {
        this.username = username
        this.email = email
        this.password = password
        this.id = id
    }

    constructor()
}