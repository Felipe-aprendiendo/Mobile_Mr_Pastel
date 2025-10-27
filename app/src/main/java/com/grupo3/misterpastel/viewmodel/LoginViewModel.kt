package com.grupo3.misterpastel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.repository.UsuarioRepository
import kotlinx.coroutines.launch

/**
 * ViewModel encargado del inicio de sesión del usuario.
 * Ahora usa Room a través del repositorio Singleton con contexto.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    sealed class LoginState {
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
        object Loading : LoginState()
    }

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    // Repositorio conectado con SQLite (Room)
    private val repository = UsuarioRepository.getInstance(application)

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = LoginState.Error("El correo y la contraseña son obligatorios.")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = repository.login(email, pass)

            result.onSuccess {
                _loginState.postValue(LoginState.Success)
            }.onFailure {
                _loginState.postValue(LoginState.Error(it.message ?: "Error desconocido"))
            }
        }
    }
}
