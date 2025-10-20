package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.repository.UsuarioRepository
import com.grupo3.misterpastel.viewmodel.state.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Valida credenciales y, si son correctas, setea success=true.
 */
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginState(loading = true)
            val result = UsuarioRepository.login(email, password)
            _uiState.value = result.fold(
                onSuccess = { LoginState(success = true) },
                onFailure = { LoginState(error = it.message ?: "Error de login") }
            )
        }
    }
}
