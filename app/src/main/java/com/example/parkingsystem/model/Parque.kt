package com.example.parkingsystem.model

data class Parque(
    val id: Int,
    val idTipoParque: Int,
    val nomeParque: String,
    val precoHora: Double,
    val totalVagas: Int,
    val totalOcupados: Int,
    val latitude: Double,
    val longitude: Double,
    val morada: String
)