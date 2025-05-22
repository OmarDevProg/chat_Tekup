package com.example.chatapp

class ModelClass {
    var message: String? = null
    var from: String? = null

    constructor()

    constructor(message: String?, from: String?) {
        this.message = message
        this.from = from
    }
}