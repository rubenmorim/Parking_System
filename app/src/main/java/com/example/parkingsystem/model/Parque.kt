package com.example.parkingsystem.model

data class Parque(
    val id: Long = 0,
    val idTipoParque: Long = 0,
    val nomeParque: String = "",
    val precoHora: Double = 0.0,
    val totalVagas: Int = 0,
    val totalOcupados: Int  = 0,
    val latitude: Double  = 0.0,
    val longitude: Double = 0.0,
    val morada: String = ""
)