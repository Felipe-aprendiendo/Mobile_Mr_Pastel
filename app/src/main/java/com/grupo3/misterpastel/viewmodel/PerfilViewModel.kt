package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.model.Usuario
import com.grupo3.misterpastel.repository.UsuarioRepository
import com.grupo3.misterpastel.viewmodel.state.PerfilState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Carga el usuario actual y permite actualizarlo (en memoria).
 */
class PerfilViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilState())
    val uiState: StateFlow<PerfilState> = _uiState

    init {
        viewModelScope.launch {
            val u = UsuarioRepository.usuarioActual.value
            if (u != null) {
                _uiState.value = PerfilState(
                    nombre = u.nombre,
                    email = u.email,
                    edad = u.edad,
                    fotoUrl = u.fotoUrl
                )
            }
        }
    }

    fun guardar(nombre: String, email: String, edad: Int, fotoUrl: String?) {
        viewModelScope.launch {
            val actual = UsuarioRepository.usuarioActual.value
            if (actual == null) {
                _uiState.value = _uiState.value.copy(error = "No hay usuario en sesión")
                return@launch
            }
            val actualizado = Usuario(
                id = actual.id,
                nombre = nombre,
                email = email,
                edad = edad,
                password = actual.password,
                fotoUrl = fotoUrl
            )
            val r = UsuarioRepository.actualizarPerfil(actualizado)
            _uiState.value = r.fold(
                onSuccess = { _uiState.value.copy(guardado = true, error = null) },
                onFailure = { _uiState.value.copy(guardado = false, error = it.message) }
            )
        }
    }
}
