package com.example.parkingsystem.model

data class Matricula(
    val id: Int,
    val nomeCarro: String,
    val matricula: String,
    val idUtilizador: Int,
    val isSelected: Boolean
)