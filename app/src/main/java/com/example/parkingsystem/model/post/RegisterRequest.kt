package com.example.parkingsystem.model.post

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val birthday: String,
    val matricula: String,
    val nomeCarro: String,
)
