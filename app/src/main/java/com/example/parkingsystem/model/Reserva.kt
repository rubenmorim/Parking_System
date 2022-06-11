package com.example.parkingsystem.model

data class Reserva(
    val id: Long,
    val idMatricula: Long,
    val idParque: Long,
    val idUtilizador: Long,
    val entrada: String,
    val saida: String,
    val isPago: Boolean,
)