package com.example.parkingsystem.api.estacionamento

import com.example.parkingsystem.model.post.Estacionamento
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface EstacionamentoEndpoint {
    @POST("/api/estacionamento/iniciarParquimetro/")
    fun postEstacionamento(@Body req: Estacionamento): Call<Estacionamento>
}