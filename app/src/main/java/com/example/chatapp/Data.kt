package com.example.chatapp

class Data {
    var user: String? = null
    var icon: String? = null
    var body: String? = null
    var title: String? = null
    var sented: String? = null

    constructor()

    constructor(user: String?, icon: String?, body: String?, title: String?, sented: String?) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sented = sented
    }
}