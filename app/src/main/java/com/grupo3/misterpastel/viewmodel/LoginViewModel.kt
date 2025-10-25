package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    fun login(email: String, pass: String) {
        // TODO: Implementar la lógica de inicio de sesión real (repositorio)
        _loginSuccess.value = email.isNotEmpty() && pass.isNotEmpty()
    }
}