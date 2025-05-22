package com.example.chatapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIServices {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAnpshWzo:APA91bGr9SsrXQrpjRx26Py_wVSv6JJjvDhqpPoDP3Z1opcUM7nrkz5_PzHKz-fmyrHE4NoA21tJP57mj2hoWmco8FnNbwL7puF5VA3xQDUEeimgTYAujAuUXzsk-kT580mEHC0xorhG"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: Sender?): Call<MyResponse?>?
}