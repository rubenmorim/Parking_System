package com.example.parkingsystem.model

data class Parque(
    val id: Long,
    val idTipoParque: Long,
    val nomeParque: String,
    val precoHora: Double,
    val totalVagas: Int,
    val totalOcupados: Int,
    val latitude: Double,
    val longitude: Double,
    val morada: String
)