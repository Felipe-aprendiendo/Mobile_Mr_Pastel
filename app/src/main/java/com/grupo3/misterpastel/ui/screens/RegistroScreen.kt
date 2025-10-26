package com.grupo3.misterpastel.viewmodel

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.grupo3.misterpastel.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
class RegistroViewModel : ViewModel() {

    sealed class RegistrationState {
        object Idle : RegistrationState()
        object Loading : RegistrationState()
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun register(
        nombre: String,
        email: String,
        password: String,
        confirmPassword: String,
        fechaNacimiento: String,
        direccion: String,
        telefono: String
    ) {
        // Validaciones sincrónicas (no necesitas coroutine para esto)
        if (nombre.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()
            || fechaNacimiento.isBlank() || direccion.isBlank() || telefono.isBlank()
        ) {
            _registrationState.value = RegistrationState.Error("Todos los campos son obligatorios.")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registrationState.value = RegistrationState.Error("El formato del correo no es válido.")
            return
        }

        if (password != confirmPassword) {
            _registrationState.value = RegistrationState.Error("Las contraseñas no coinciden.")
            return
        }

        if (password.length < 6) {
            _registrationState.value = RegistrationState.Error("La contraseña debe tener al menos 6 caracteres.")
            return
        }

        val edad = try {
            val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val fecha = LocalDate.parse(fechaNacimiento, fmt)
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

        val result = UsuarioRepository.registrar(
            nombre = nombre.trim(),
            email = email.trim(),
            password = password.trim(),
            edad = edad,
            fechaNacimiento = fechaNacimiento.trim(),
            direccion = direccion.trim(),
            telefono = telefono.trim(),
            fotoUrl = null // NO foto en registro
        )

        result.onSuccess {
            _registrationState.value = RegistrationState.Success
        }.onFailure {
            _registrationState.value =
                RegistrationState.Error(it.message ?: "Error desconocido al registrar usuario.")
        }
    }
}
