package com.example.qring

class User {
    var username: String? = null
    var user_id: String? = null

    constructor(username: String, user_id: String) {
        this.username = username
        this.user_id = user_id
    }

    constructor() {}

}