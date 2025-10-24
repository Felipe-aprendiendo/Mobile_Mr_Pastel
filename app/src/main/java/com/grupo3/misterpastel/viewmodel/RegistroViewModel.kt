package com.grupo3.misterpastel.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistroViewModel: ViewModel() {

    // Sealed class para representar los diferentes estados del proceso de registro
    sealed class RegistrationState {
        object Success : RegistrationState() // El registro fue exitoso
        data class Error(val message: String) : RegistrationState() // Ocurrió un error
    }

    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> = _registrationState


    fun register(name: String, email: String, pass: String, confirmPass: String) {

        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
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

        // Si todas las validaciones pasan, registramos al usuario
        // Por ahora, simulamos un éxito inmediato.
        _registrationState.value = RegistrationState.Success
    }
}
