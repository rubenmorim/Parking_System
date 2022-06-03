package com.example.parkingsystem.api.parque

import com.example.parkingsystem.model.Parque
import retrofit2.Call
import retrofit2.http.GET

interface ParqueEndpoint {

    @GET("/api/parques/allParques")
    fun getParques(): Call<List<Parque>>
}