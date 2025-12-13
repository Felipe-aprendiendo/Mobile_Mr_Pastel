package com.grupo3.misterpastel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.model.Usuario
import com.grupo3.misterpastel.repository.UsuarioRepository
import com.grupo3.misterpastel.repository.remote.RetrofitInstance
import kotlinx.coroutines.launch

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository.getInstance(
        application,
        RetrofitInstance.api
    )

    val usuario: LiveData<Usuario?> =
        repository.usuarioActual.asLiveData()

    fun actualizarPerfil(
        nombre: String,
        direccion: String,
        telefono: String,
        fotoUrl: String?
    ) {
        val actual = usuario.value ?: return

        val actualizado = actual.copy(
            nombre = nombre,
            direccion = direccion,
            telefono = telefono,
            fotoUrl = fotoUrl
        )

        viewModelScope.launch {
            repository.actualizarPerfil(actualizado)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
