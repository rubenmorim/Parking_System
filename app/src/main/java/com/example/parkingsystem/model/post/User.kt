package com.example.parkingsystem.model.post


data class Res(
    val responseStatus: Boolean,
    val response: User
)
data class User(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val birthday: String
)
