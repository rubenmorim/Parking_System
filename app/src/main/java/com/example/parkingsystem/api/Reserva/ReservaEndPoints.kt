package com.example.parkingsystem.api.Reserva

import com.example.parkingsystem.model.*
import com.example.parkingsystem.model.post.LoginRequest
import com.example.parkingsystem.model.post.RegisterRequest
import com.example.parkingsystem.model.post.ReserveRequest
import retrofit2.Call
import retrofit2.http.*

interface ReservaEndPoints {

    @POST("/api/estacionamento/createReserva")
    fun creatReserva(@Body req: ReserveRequest): Call<Reserva>

    @GET("/api/estacionamento/getReservasbyUser/{id}")
    fun getReservas(@Path("id") id: Long): Call<List<Reservas>>

}