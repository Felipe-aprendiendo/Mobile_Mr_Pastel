package com.grupo3.misterpastel.viewmodel

import android.app.Application
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.repository.UsuarioRepository
import com.grupo3.misterpastel.repository.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

    sealed class RegistrationState {
        object Idle : RegistrationState()
        object Loading : RegistrationState()
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    private val _registrationState =
        MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> =
        _registrationState.asStateFlow()

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    private val repository = UsuarioRepository.getInstance(
        application,
        RetrofitInstance.api
    )

    fun onNombreChange(v: String) { _uiState.update { it.copy(nombre = v) }; clearError() }
    fun onEmailChange(v: String) { _uiState.update { it.copy(email = v) }; clearError() }
    fun onPasswordChange(v: String) { _uiState.update { it.copy(password = v) }; clearError() }
    fun onConfirmPasswordChange(v: String) { _uiState.update { it.copy(confirmPassword = v) }; clearError() }
    fun onFechaNacimientoChange(v: String) { _uiState.update { it.copy(fechaNacimiento = v) }; clearError() }
    fun onDireccionChange(v: String) { _uiState.update { it.copy(direccion = v) }; clearError() }
    fun onTelefonoChange(v: String) { _uiState.update { it.copy(telefono = v) }; clearError() }

    private fun clearError() {
        if (_registrationState.value is RegistrationState.Error) {
            _registrationState.value = RegistrationState.Idle
        }
    }

    fun register() {
        val state = _uiState.value

        if (
            state.nombre.isBlank() ||
            state.email.isBlank() ||
            state.password.isBlank() ||
            state.confirmPassword.isBlank() ||
            state.fechaNacimiento.isBlank() ||
            state.direccion.isBlank() ||
            state.telefono.isBlank()
        ) {
            _registrationState.value =
                RegistrationState.Error("Todos los campos son obligatorios.")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _registrationState.value =
                RegistrationState.Error("El formato del correo no es válido.")
            return
        }

        val passwordRegex = "^[a-zA-Z0-9]{6,}$".toRegex()
        if (!passwordRegex.matches(state.password)) {
            _registrationState.value =
                RegistrationState.Error("La contraseña debe tener al menos 6 caracteres alfanuméricos.")
            return
        }

        if (state.password != state.confirmPassword) {
            _registrationState.value =
                RegistrationState.Error("Las contraseñas no coinciden.")
            return
        }

        val fechaIso = try {
            val fmtInput = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val fecha = LocalDate.parse(state.fechaNacimiento, fmtInput)

            val edad = Period.between(fecha, LocalDate.now()).years
            if (edad < 13) {
                _registrationState.value =
                    RegistrationState.Error("Debes tener al menos 13 años para registrarte.")
                return
            }

            fecha.format(DateTimeFormatter.ISO_DATE)
        } catch (_: DateTimeParseException) {
            _registrationState.value =
                RegistrationState.Error("Formato de fecha no válido. Usa dd/MM/yyyy.")
            return
        }

        _registrationState.value = RegistrationState.Loading

        viewModelScope.launch {
            val result = repository.registrar(
                nombre = state.nombre.trim(),
                email = state.email.trim(),
                password = state.password.trim(),
                edad = null,
                fechaNacimiento = fechaIso,
                direccion = state.direccion.trim(),
                telefono = state.telefono.trim(),
                fotoUrl = null
            )

            result.onSuccess {
                _registrationState.value = RegistrationState.Success
            }.onFailure {
                _registrationState.value =
                    RegistrationState.Error(it.message ?: "Error al registrar usuario.")
            }
        }
    }
}
