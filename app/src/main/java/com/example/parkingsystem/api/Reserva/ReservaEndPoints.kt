package com.example.parkingsystem.api.Reserva

import com.example.parkingsystem.model.Res
import com.example.parkingsystem.model.Reserva
import com.example.parkingsystem.model.User
import com.example.parkingsystem.model.post.LoginRequest
import com.example.parkingsystem.model.post.RegisterRequest
import com.example.parkingsystem.model.post.ReserveRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ReservaEndPoints {

    @POST("/api/estacionamento/createReserva")
    fun creatReserva(@Body req: ReserveRequest): Call<Reserva>

}