package com.grupo3.misterpastel.viewmodel.state

/**
 * Estado del perfil de usuario para edición simple.
 */
data class PerfilState(
    val nombre: String = "",
    val email: String = "",
    val edad: Int = 0,
    val fotoUrl: String? = null,
    val error: String? = null,
    val guardado: Boolean = false
)
