package com.example.parkingsystem.api.wallet

import com.example.parkingsystem.model.Wallet
import com.example.parkingsystem.model.Message
import com.example.parkingsystem.model.post.WalletRequest
import retrofit2.Call
import retrofit2.http.*

interface WalletEndpoints {

    @GET("/api/tipoPagamento/allTipoPagamentoByUtilizador/{id}")
    fun getTipoPagamento(@Path("id") id: Long): Call<List<Wallet>>

    @POST("/api/tipoPagamento/createTipoPagamento")
    fun createPagamento(@Body req: WalletRequest): Call<Message>

}