package com.example.parkingsystem.model.post

data class Estacionamento(
    var idUtilizador: Long = 0,
    var idParque: Long = 0,
    var tempoParque: Long? = null
)