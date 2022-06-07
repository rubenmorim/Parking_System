package com.example.parkingsystem.api.historico

import com.example.parkingsystem.model.Historico
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Path

interface HistoricoEndpoint {

    @GET("/api/estacionamento/gethistorico/{id}")
    fun getHistoricoByUser(@Path("id") id: Int): Call<List<Historico>>
}