package com.grupo3.misterpastel.viewmodel

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo3.misterpastel.repository.UsuarioRepository
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
class RegistroViewModel: ViewModel() {

    // Sealed class para representar los diferentes estados del proceso de registro
    sealed class RegistrationState {
        object Success : RegistrationState() // El registro fue exitoso
        data class Error(val message: String) : RegistrationState() // Ocurrió un error
    }

    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> = _registrationState


    fun register(name: String, email: String, pass: String, confirmPass: String, fechaNacimiento: String) {

        if (name.isBlank() || email.isBlank() || pass.isBlank() || fechaNacimiento.isBlank()) {
            _registrationState.value = RegistrationState.Error("Todos los campos son obligatorios.")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registrationState.value = RegistrationState.Error("El formato del correo no es válido.")
            return
        }

        if (pass != confirmPass) {
            _registrationState.value = RegistrationState.Error("Las contraseñas no coinciden.")
            return
        }

        if (pass.length < 6) {
            _registrationState.value = RegistrationState.Error("La contraseña debe tener al menos 6 caracteres.")
            return
        }

        val edad = try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val fecha = LocalDate.parse(fechaNacimiento, formatter)
            val calculatedAge = Period.between(fecha, LocalDate.now()).years
            if (calculatedAge < 13) {
                _registrationState.value = RegistrationState.Error("Debes tener al menos 13 años para registrarte.")
                return
            }
            calculatedAge
        } catch (e: DateTimeParseException) {
            _registrationState.value = RegistrationState.Error("Formato de fecha de nacimiento no válido. Usa dd/MM/yyyy.")
            return
        }

        val result = UsuarioRepository.registrar(
            nombre = name,
            email = email,
            password = pass,
            edad = edad,
            fechaNacimiento = fechaNacimiento,
            direccion = "", // Campo no disponible en la pantalla de registro
            telefono = "" // Campo no disponible en la pantalla de registro
        )

        result.onSuccess {
            _registrationState.value = RegistrationState.Success
        }.onFailure {
            _registrationState.value = RegistrationState.Error(it.message ?: "Error desconocido en el registro")
        }
    }
}