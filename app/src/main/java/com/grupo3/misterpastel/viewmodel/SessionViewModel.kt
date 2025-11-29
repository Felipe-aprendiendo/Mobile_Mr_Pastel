package com.grupo3.misterpastel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.model.Usuario
import com.grupo3.misterpastel.repository.UsuarioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**IMPORTANTE
 * ViewModel que maneja la sesión del usuario y sincroniza los datos
 * con la base de datos Room mediante UsuarioRepository.
 */
class SessionViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia del repositorio persistente
    private val repository = UsuarioRepository.getInstance(application)

    // Estado observable del usuario actual (flujo de Room → UI)
    val usuarioActual: StateFlow<Usuario?> = repository.usuarioActual
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Cierra la sesión actual (borra el flujo de usuario en memoria).
    fun logout() {
        repository.logout()
    }


    // Actualiza los datos del perfil en la base local.Este cambio se refleja automáticamente en la UI.

    fun actualizarPerfil(actualizado: Usuario, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.actualizarPerfil(actualizado)
            result.onFailure { onError(it.message ?: "Error al actualizar perfil") }
        }
    }

    // Actualiza solo la foto de perfil del usuario actual.
    fun actualizarFoto(fotoUrl: String?, onError: (String) -> Unit = {}) {
        val usuario = usuarioActual.value ?: return
        val actualizado = usuario.copy(fotoUrl = fotoUrl)
        viewModelScope.launch {
            val result = repository.actualizarPerfil(actualizado)
            result.onFailure { onError(it.message ?: "Error al actualizar la foto") }
        }
    }
}
