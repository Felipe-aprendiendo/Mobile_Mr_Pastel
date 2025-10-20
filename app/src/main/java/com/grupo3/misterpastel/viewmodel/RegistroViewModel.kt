package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.repository.UsuarioRepository
import com.grupo3.misterpastel.viewmodel.state.RegistroState
import com.grupo3.misterpastel.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Realiza validaciones básicas y registra el usuario en repositorio en memoria.
 */
class RegistroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroState())
    val uiState: StateFlow<RegistroState> = _uiState

    fun registrar(nombre: String, email: String, password: String, confirm: String, edad: Int) {
        viewModelScope.launch {
            _uiState.value = RegistroState(loading = true)

            // Validaciones básicas (puedes mover todas a Validators si prefieres)
            if (nombre.isBlank()) {
                _uiState.value = RegistroState(error = "El nombre no puede estar vacío")
                return@launch
            }
            if (!Validators.isValidEmail(email)) {
                _uiState.value = RegistroState(error = "Correo inválido")
                return@launch
            }
            if (password.length < 6) {
                _uiState.value = RegistroState(error = "La contraseña debe tener al menos 6 caracteres")
                return@launch
            }
            if (password != confirm) {
                _uiState.value = RegistroState(error = "Las contraseñas no coinciden")
                return@launch
            }
            if (edad < 13) {
                _uiState.value = RegistroState(error = "Debes ser mayor de 13 años")
                return@launch
            }

            val result = UsuarioRepository.registrar(nombre, email, password, edad)
            _uiState.value = result.fold(
                onSuccess = { RegistroState(success = true) },
                onFailure = { RegistroState(error = it.message ?: "Error al registrar") }
            )
        }
    }
}
