package com.grupo3.misterpastel.viewmodel.state

/**
 * Estado para registro de usuario.
 */
data class RegistroState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)
