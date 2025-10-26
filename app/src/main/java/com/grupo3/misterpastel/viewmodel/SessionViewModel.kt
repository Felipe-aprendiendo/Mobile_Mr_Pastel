package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.model.Usuario
import com.grupo3.misterpastel.repository.UsuarioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SessionViewModel : ViewModel() {

    private val usuarioRepository = UsuarioRepository

    val usuarioActual: StateFlow<Usuario?> = usuarioRepository.usuarioActual
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun logout() {
        usuarioRepository.logout()
    }
}