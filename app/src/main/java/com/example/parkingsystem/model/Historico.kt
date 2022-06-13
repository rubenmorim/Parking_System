package com.example.parkingsystem.model

data class Historico (
    val idParque: Long,
    val nomeParque: String,
    val entrada: String,
    val saida: String,
    val tempo:Int,
    val preco:Int,
)