package com.grupo3.misterpastel.viewmodel


import androidx.lifecycle.ViewModel
import com.grupo3.misterpastel.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeSessionViewModel : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    fun logout() { /* noop */ }
}
