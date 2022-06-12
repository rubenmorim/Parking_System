package com.example.parkingsystem.model

data class Estacionamento (
    val id: Long = 0,
    val idMatricula: Int = 0,
    val idParque: Int = 0,
    val idUtilizador: Int = 0,
    val entrada: String = "",
    val saida: String? = null,
    val isPago: Boolean = false
)