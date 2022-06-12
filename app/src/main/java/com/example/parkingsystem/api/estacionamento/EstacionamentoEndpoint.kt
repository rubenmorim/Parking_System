package com.example.parkingsystem.api.estacionamento

import com.example.parkingsystem.model.EstacionamentoAtual
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface EstacionamentoEndpoint {
    @POST("/api/estacionamento/iniciarParquimetro/")
    fun postEstacionamento(@Body req: com.example.parkingsystem.model.post.Estacionamento): Call<com.example.parkingsystem.model.post.Estacionamento>

    @GET("/api/estacionamento/getEstacionamentoAtual?idUtilizador")
    fun getConcluirParquimetro(@Query("idUtilizador") idUtilizador: Long): Call<com.example.parkingsystem.model.Estacionamento>

    @GET("/api/estacionamento/getEstacionamentoAtual?idUtilizador?tempoAdicional")
    fun getRenovarParquimetro(@Query("idUtilizador") idUtilizador: Long,
                              @Query("tempoAdicional") tempoAdicional: Long): Call<com.example.parkingsystem.model.Estacionamento>

    @GET("/api/estacionamento/getEstacionamentoAtual?idUtilizador")
    fun getEstacionamentoAtual(@Query("idUtilizador") idUtilizador: Long): Call<EstacionamentoAtual>
}