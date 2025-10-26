package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.model.Usuario
import com.grupo3.misterpastel.model.Pedido
import kotlinx.coroutines.launch
import java.util.UUID

class PerfilViewModel : ViewModel() {

    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario

    private val _pedidos = MutableLiveData<List<Pedido>>()
    val pedidos: LiveData<List<Pedido>> = _pedidos

    init {
        cargarDatosUsuario()
    }

    /**
     * Carga un usuario de ejemplo.
     */
    private fun cargarDatosUsuario() {
        viewModelScope.launch {
            _usuario.value = Usuario(
                id = "U001",
                nombre = "Felipe Hernández",
                email = "felipe@email.com",
                edad = 30,
                fechaNacimiento = "1995-05-10",
                direccion = "Calle Falsa 123",
                telefono = "987654321",
                password = "123456",
                fotoUrl = null
            )
        }
    }

    /**
     * Actualiza los datos del usuario.
     */
    fun actualizarDatosUsuario(
        nombre: String,
        email: String,
        edad: Int,
        fechaNacimiento: String,
        direccion: String,
        telefono: String,
        password: String,
        fotoUrl: String? = null
    ) {
        viewModelScope.launch {
            val usuarioActualizado = Usuario(
                id = _usuario.value?.id ?: UUID.randomUUID().toString(),
                nombre = nombre,
                email = email,
                edad = edad,
                fechaNacimiento = fechaNacimiento,
                direccion = direccion,
                telefono = telefono,
                password = password,
                fotoUrl = fotoUrl
            )
            _usuario.value = usuarioActualizado
        }
    }

}
