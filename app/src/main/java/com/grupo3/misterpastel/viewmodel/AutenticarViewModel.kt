package com.grupo3.misterpastel.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AutenticarViewModel : ViewModel() {


    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn


    // En una implementación futura, este campo podría contener el objeto Usuario logueado.
    // TODO: conectar con el modelo de datos real del usuario.
    private val _usuarioActual = MutableStateFlow<String?>(null)
    val usuarioActual: StateFlow<String?> = _usuarioActual


    // === INICIAR SESIÓN ===
    // Recibe email y password, valida y actualiza el estado.
    fun iniciarSesion(email: String, password: String) {
        viewModelScope.launch {
            // TODO: Validar credenciales con SQLite o DataStore.
            // Ejemplo futuro:
            // val usuario = userRepository.validarCredenciales(email, password)
            // if (usuario != null) {
            //     _usuarioActual.value = usuario.nombre
            //     _isLoggedIn.value = true
            // }

            // Por ahora, se simula un login exitoso si los campos no están vacíos.
            if (email.isNotBlank() && password.isNotBlank()) {
                _usuarioActual.value = "Cliente Prueba"
                _isLoggedIn.value = true
            }
        }
    }


    // === CERRAR SESIÓN ===
    // Resetea los valores del flujo de estado.
    fun cerrarSesion() {
        viewModelScope.launch {
            _usuarioActual.value = null
            _isLoggedIn.value = false
            // TODO: Borrar datos persistidos si se implementa almacenamiento local
        }
    }


    // === VERIFICAR SESIÓN ===
    // Este método se puede llamar desde el SplashScreen o HomeScreen
    // para determinar si el usuario ya tiene una sesión activa.
    fun verificarSesionActiva() {
        viewModelScope.launch {
            // TODO: Consultar SQLite o DataStore para ver si existe una sesión guardada
            // Ejemplo:
            // _isLoggedIn.value = dataStore.obtenerEstadoSesion()
        }
    }
}
