package com.practica.proyectozoo.data

data class Especie(
    val id: Int,
    val nombreVulgar: String,
    val nombreCientifico: String,
    val familia: String?,
    val enPeligro: Boolean
)
