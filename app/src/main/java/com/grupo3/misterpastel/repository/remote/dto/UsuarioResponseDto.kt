package com.grupo3.misterpastel.repository.remote.dto


/**
 * Respuesta de:
 * - POST /auth/login
 * - POST /usuarios/
 * - GET /usuarios/{id}
 */
data class UsuarioResponseDto(
    val id: Long,
    val nombre: String,
    val email: String,
    val edad: Int?,
    val descuento: Double
)
