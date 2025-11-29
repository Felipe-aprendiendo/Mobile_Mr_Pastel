package com.grupo3.misterpastel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.model.Usuario
import com.grupo3.misterpastel.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


 // Se comunica con la base de datos local (Room) a través de UsuarioRepository.

class AutenticarViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia del repositorio con persistencia local (Room)
    private val repository = UsuarioRepository.getInstance(application)

    // Estado de sesión (si hay usuario logueado)
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // Usuario actualmente autenticado
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    // === INICIAR SESIÓN ===
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

    // === CERRAR SESIÓN ===
    fun cerrarSesion() {
        viewModelScope.launch {
            repository.logout()
            _usuarioActual.value = null
            _isLoggedIn.value = false
        }
    }

    // === VERIFICAR SESIÓN ACTIVA ===
    fun verificarSesionActiva() {
        viewModelScope.launch {
            val usuario = repository.usuarioActual.value
            _usuarioActual.value = usuario
            _isLoggedIn.value = usuario != null
        }
    }
}
