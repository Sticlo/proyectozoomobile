package com.practica.proyectozoo.data

data class Animal(
    val idAnimal: Int,
    val idZoo: Int,
    val idEspecie: Int,
    val sexo: Char,
    val anioNacimiento: Int?,
    val paisOrigen: String?,
    val continente: String?
)
