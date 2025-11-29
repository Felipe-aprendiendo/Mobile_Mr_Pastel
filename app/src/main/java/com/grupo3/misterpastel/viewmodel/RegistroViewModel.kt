package com.grupo3.misterpastel.viewmodel

import android.app.Application
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**IMPORTANTE
 * Estado que representa los datos del formulario de registro.
 * Vive en el ViewModel para sobrevivir a cambios de configuración.
 */
data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fechaNacimiento: String = "",
    val direccion: String = "",
    val telefono: String = ""
)

@RequiresApi(Build.VERSION_CODES.O)
class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    // Estado del proceso de registro
    sealed class RegistrationState {
        object Idle : RegistrationState()
        object Loading : RegistrationState()
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    // Estado del formulario
    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    // Instancia del repositorio con acceso a Room
    private val repository = UsuarioRepository.getInstance(application)

    // ======== Actualización de campos ========
    fun onNombreChange(valor: String) { _uiState.update { it.copy(nombre = valor) }; clearError() }
    fun onEmailChange(valor: String) { _uiState.update { it.copy(email = valor) }; clearError() }
    fun onPasswordChange(valor: String) { _uiState.update { it.copy(password = valor) }; clearError() }
    fun onConfirmPasswordChange(valor: String) { _uiState.update { it.copy(confirmPassword = valor) }; clearError() }
    fun onFechaNacimientoChange(valor: String) { _uiState.update { it.copy(fechaNacimiento = valor) }; clearError() }
    fun onDireccionChange(valor: String) { _uiState.update { it.copy(direccion = valor) }; clearError() }
    fun onTelefonoChange(valor: String) { _uiState.update { it.copy(telefono = valor) }; clearError() }

    private fun clearError() {
        if (_registrationState.value is RegistrationState.Error)
            _registrationState.value = RegistrationState.Idle
    }

    // ======== Registro de usuario ========
    fun register() {
        val state = _uiState.value

        // --- Validaciones ---
        if (state.nombre.isBlank() || state.email.isBlank() || state.password.isBlank() ||
            state.confirmPassword.isBlank() || state.fechaNacimiento.isBlank() ||
            state.direccion.isBlank() || state.telefono.isBlank()
        ) {
            _registrationState.value = RegistrationState.Error("Todos los campos son obligatorios.")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _registrationState.value = RegistrationState.Error("El formato del correo no es válido.")
            return
        }

        if (state.password != state.confirmPassword) {
            _registrationState.value = RegistrationState.Error("Las contraseñas no coinciden.")
            return
        }

        if (state.password.length < 6) {
            _registrationState.value = RegistrationState.Error("La contraseña debe tener al menos 6 caracteres.")
            return
        }

        val edad = try {
            val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val fecha = LocalDate.parse(state.fechaNacimiento, fmt)
            val years = Period.between(fecha, LocalDate.now()).years
            if (years < 13) {
                _registrationState.value = RegistrationState.Error("Debes tener al menos 13 años para registrarte.")
                return
            }
            years
        } catch (_: DateTimeParseException) {
            _registrationState.value = RegistrationState.Error("Formato de fecha no válido. Usa dd/MM/yyyy.")
            return
        }

        _registrationState.value = RegistrationState.Loading

        // --- Llamada al repositorio ---
        viewModelScope.launch {
            val result = repository.registrar(
                nombre = state.nombre.trim(),
                email = state.email.trim(),
                password = state.password.trim(),
                edad = edad,
                fechaNacimiento = state.fechaNacimiento.trim(),
                direccion = state.direccion.trim(),
                telefono = state.telefono.trim(),
                fotoUrl = null
            )

            result.onSuccess {
                _registrationState.value = RegistrationState.Success
            }.onFailure {
                _registrationState.value =
                    RegistrationState.Error(it.message ?: "Error desconocido al registrar usuario.")
            }
        }
    }
}
