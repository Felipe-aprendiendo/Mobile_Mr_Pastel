package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo3.misterpastel.repository.UsuarioRepository

class LoginViewModel : ViewModel() {

    sealed class LoginState {
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
        object Loading : LoginState()
    }

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = LoginState.Error("El correo y la contraseña son obligatorios.")
            return
        }

        _loginState.value = LoginState.Loading

        val result = UsuarioRepository.login(email, pass)

        result.onSuccess {
            _loginState.postValue(LoginState.Success)
        }.onFailure {
            _loginState.postValue(LoginState.Error(it.message ?: "Error desconocido"))
        }
    }
}