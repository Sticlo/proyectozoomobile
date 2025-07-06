package com.practica.proyectozoo.data

data class Usuario(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val perfilId: Int,
    val fechaRegistro: String? = null
)
