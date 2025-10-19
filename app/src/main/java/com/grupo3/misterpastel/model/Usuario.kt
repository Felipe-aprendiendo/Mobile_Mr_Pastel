package com.grupo3.misterpastel.model

data class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
    val edad: Int,
    val password: String,
    val fotoUrl: String? = null
)