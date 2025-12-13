package com.grupo3.misterpastel.repository.remote.dto


/**
 * Body para POST /usuarios/
 * fechaNacimiento debe ir como "YYYY-MM-DD" o null.
 * fotoUrl puede ser null.
 */
data class RegistroRequestDto(
    val nombre: String,
    val email: String,
    val edad: Int? = null,
    val fechaNacimiento: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val password: String,
    val fotoUrl: String? = null
)
