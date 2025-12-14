package com.grupo3.misterpastel.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.repository.UsuarioRepository
import com.grupo3.misterpastel.repository.remote.RetrofitInstance
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    sealed class LoginState {
        data object Idle : LoginState()
        data object Loading : LoginState()
        data object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    private val repository = UsuarioRepository.getInstance(
        application,
        RetrofitInstance.api
    )

    fun login(email: String, pass: String) {
        val emailTrim = email.trim()
        val passTrim = pass.trim()

        if (emailTrim.isBlank() || passTrim.isBlank()) {
            _loginState.value = LoginState.Error("El correo y la contraseña son obligatorios.")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailTrim).matches()) {
            _loginState.value = LoginState.Error("El formato del correo no es válido.")
            return
        }

        val passwordRegex = Regex("^[a-zA-Z0-9]{6,}$")
        if (!passwordRegex.matches(passTrim)) {
            _loginState.value = LoginState.Error("La contraseña debe tener al menos 6 caracteres alfanuméricos.")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = repository.login(emailTrim, passTrim)

            result.onSuccess {
                _loginState.postValue(LoginState.Success)
            }.onFailure { err ->
                val msg = (err.message ?: "Error desconocido").trim()

                // Normalizamos a los mensajes UX requeridos
                val normalized = when {
                    msg.equals("Correo no registrado", ignoreCase = true) -> "correo no registrado"
                    msg.equals("Contraseña incorrecta", ignoreCase = true) -> "contraseña incorrecta"
                    else -> msg
                }

                _loginState.postValue(LoginState.Error(normalized))
            }
        }
    }

    fun reset() {
        _loginState.value = LoginState.Idle
    }
}
