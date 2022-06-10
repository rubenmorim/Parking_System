package com.example.parkingsystem.api.parque

import com.example.parkingsystem.model.Historico
import com.example.parkingsystem.model.Parque
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ParqueEndpoint {

    @GET("/api/parques/allParques")
    fun getParques(): Call<List<Parque>>

    @GET("/api/parques/getParqueById?id")
    fun getParqueByID(@Query("id") id: Long): Call<List<Parque>>
}