package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.model.Usuario
import com.grupo3.misterpastel.repository.UsuarioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Maneja el estado de la sesión del usuario.
 * - Expone el usuario actual desde el repositorio (StateFlow).
 * - Permite cerrar sesión.
 * - Permite actualizar datos de perfil o la foto.
 */
class SessionViewModel : ViewModel() {

    private val usuarioRepository = UsuarioRepository

    // Estado del usuario actual (observado por las pantallas)
    val usuarioActual: StateFlow<Usuario?> = usuarioRepository.usuarioActual
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /** Cierra la sesión actual. */
    fun logout() {
        usuarioRepository.logout()
    }

    /** Actualiza los datos del perfil (nombre, dirección, etc.). */
    fun actualizarPerfil(actualizado: Usuario, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val result = usuarioRepository.actualizarPerfil(actualizado)
            result.onFailure { onError(it.message ?: "Error al actualizar perfil") }
        }
    }

    /** Cambia solo la URL/URI de la foto del usuario. */
    fun actualizarFoto(fotoUrl: String?, onError: (String) -> Unit = {}) {
        val usuario = usuarioActual.value ?: return
        val actualizado = usuario.copy(fotoUrl = fotoUrl)
        viewModelScope.launch {
            val result = usuarioRepository.actualizarPerfil(actualizado)
            result.onFailure { onError(it.message ?: "Error al actualizar la foto") }
        }
    }
}
