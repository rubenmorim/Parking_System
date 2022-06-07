package com.example.parkingsystem.api.user

import com.example.parkingsystem.model.post.LoginRequest
import com.example.parkingsystem.model.Res
import retrofit2.Call
import retrofit2.http.*

interface UserEndPoints {

    @POST("/api/utilizador/login")
    fun login(@Body req: LoginRequest): Call<Res>

}