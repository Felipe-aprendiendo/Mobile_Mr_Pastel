package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.model.Usuario
import com.grupo3.misterpastel.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de manejar la autenticación del usuario.
 * A futuro puede conectarse con SQLite o API remota.
 */
class AutenticarViewModel : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // 🧾 Usuario actual (completo, no solo nombre)
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    // === INICIAR SESIÓN ===
    fun iniciarSesion(email: String, password: String) {
        viewModelScope.launch {
            // 🔹 En implementación real se validará en SQLite
            val usuario = UsuarioRepository.buscarPorCredenciales(email, password)

            if (usuario != null) {
                _usuarioActual.value = usuario
                _isLoggedIn.value = true
                UsuarioRepository._usuarioActual.value = usuario // Mantiene sincronía global
            } else {
                _isLoggedIn.value = false
                _usuarioActual.value = null
            }
        }
    }

    // === CERRAR SESIÓN ===
    fun cerrarSesion() {
        viewModelScope.launch {
            _usuarioActual.value = null
            _isLoggedIn.value = false
            UsuarioRepository.cerrarSesion()
        }
    }

    // === VERIFICAR SESIÓN ===
    fun verificarSesionActiva() {
        viewModelScope.launch {
            val usuario = UsuarioRepository.usuarioActual.value
            _usuarioActual.value = usuario
            _isLoggedIn.value = usuario != null
        }
    }
}
