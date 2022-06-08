package com.example.parkingsystem.api.user

import com.example.parkingsystem.model.post.LoginRequest
import com.example.parkingsystem.model.Res
import com.example.parkingsystem.model.User
import com.example.parkingsystem.model.post.RegisterRequest
import retrofit2.Call
import retrofit2.http.*

interface UserEndPoints {

    @POST("/api/utilizador/login")
    fun login(@Body req: LoginRequest): Call<Res>

    @POST("/api/utilizador/registar")
    fun register(@Body req: RegisterRequest): Call<User>

}