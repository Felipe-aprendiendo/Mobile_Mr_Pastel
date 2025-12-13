package com.grupo3.misterpastel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.repository.UsuarioRepository
import com.grupo3.misterpastel.repository.remote.RetrofitInstance
import kotlinx.coroutines.launch

/**
 * ViewModel encargado del inicio de sesión del usuario.
 * Login remoto vía APEX.
 * Restaura sesión local al iniciar.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    sealed class LoginState {
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
        object Loading : LoginState()
        object Idle : LoginState()
    }

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    private val repository = UsuarioRepository.getInstance(
        application,
        RetrofitInstance.api
    )

    init {
        // Restaurar sesión si existe
        viewModelScope.launch {
            repository.restaurarSesionLocal()
        }
    }

    fun login(email: String, password: String) {

        // Validación email
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!emailRegex.matches(email)) {
            _loginState.value = LoginState.Error("El correo no tiene un formato válido.")
            return
        }

        // Validación password
        val passwordRegex = "^[a-zA-Z0-9]{6,}$".toRegex()
        if (!passwordRegex.matches(password)) {
            _loginState.value =
                LoginState.Error("La contraseña debe tener al menos 6 caracteres alfanuméricos.")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = repository.login(email, password)

            result.onSuccess {
                _loginState.postValue(LoginState.Success)
            }.onFailure { error ->
                _loginState.postValue(
                    LoginState.Error(error.message ?: "Error desconocido")
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _loginState.postValue(LoginState.Idle)
        }
    }
}
