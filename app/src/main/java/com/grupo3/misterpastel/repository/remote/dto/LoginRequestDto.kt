package com.grupo3.misterpastel.repository.remote.dto


/**
 * Body para POST /auth/login
 */
data class LoginRequestDto(
    val email: String,
    val password: String
)
