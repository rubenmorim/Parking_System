package com.example.parkingsystem.model

data class Matricula(
    val id: Long,
    val nomeCarro: String,
    val matricula: String,
    val idUtilizador: Long,
    val isSelected: Boolean
)