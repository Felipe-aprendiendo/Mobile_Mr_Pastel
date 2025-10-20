package com.grupo3.misterpastel.viewmodel.state

/**
 * Estado simple para la pantalla de Login.
 * - loading: indica operación en curso
 * - error: mensaje de error a mostrar (si existe)
 * - success: indica si el login fue exitoso
 */
data class LoginState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)
