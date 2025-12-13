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

class AutenticarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository.getInstance(
        application,
        RetrofitInstance.api
    )

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    init {
        viewModelScope.launch {
            repository.restaurarSesionLocal()
            val usuario = repository.usuarioActual.value
            _usuarioActual.value = usuario
            _isLoggedIn.value = usuario != null
        }
    }

    fun iniciarSesion(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.onSuccess { usuario ->
                _usuarioActual.value = usuario
                _isLoggedIn.value = true
            }.onFailure {
                _usuarioActual.value = null
                _isLoggedIn.value = false
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            repository.logout()
            _usuarioActual.value = null
            _isLoggedIn.value = false
        }
    }
}
