package com.example.parkingsystem.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {
    @FormUrlEncoded
    @POST("/api/utilizador/login")
    fun login(@Body req: LoginRequest): Call<User>

}