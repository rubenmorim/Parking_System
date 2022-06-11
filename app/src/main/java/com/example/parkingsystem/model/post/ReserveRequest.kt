package com.example.parkingsystem.model.post

data class ReserveRequest(
    val idUtilizador: Long,
    val dataentrada: String,
    val tempoParque: String,
    val idParque: Int,
)
