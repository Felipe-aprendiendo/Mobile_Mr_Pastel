package com.grupo3.misterpastel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.model.Usuario
import com.grupo3.misterpastel.repository.UsuarioRepository
import com.grupo3.misterpastel.repository.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository.getInstance(
        application,
        RetrofitInstance.api
    )

    val usuarioActual: StateFlow<Usuario?> = repository.usuarioActual

    private val _sessionChecked = MutableStateFlow(false)
    val sessionChecked: StateFlow<Boolean> = _sessionChecked

    init {
        viewModelScope.launch {
            repository.restaurarSesionLocal()
            _sessionChecked.value = true
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun actualizarPerfil(actualizado: Usuario, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.actualizarPerfil(actualizado)
            result.onFailure { onError(it.message ?: "Error al actualizar perfil") }
        }
    }

    fun actualizarFoto(fotoUrl: String?, onError: (String) -> Unit = {}) {
        val usuario = usuarioActual.value ?: return
        val actualizado = usuario.copy(fotoUrl = fotoUrl)

        viewModelScope.launch {
            val result = repository.actualizarPerfil(actualizado)
            result.onFailure { onError(it.message ?: "Error al actualizar la foto") }
        }
    }
}
