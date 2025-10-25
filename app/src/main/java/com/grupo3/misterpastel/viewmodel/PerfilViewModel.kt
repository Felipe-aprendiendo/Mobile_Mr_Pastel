package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.model.Pedido
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.model.Usuario
import kotlinx.coroutines.launch

class PerfilViewModel: ViewModel() {

    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario

    private val _pedidos = MutableLiveData<List<Pedido>>()
    val pedidos: LiveData<List<Pedido>> = _pedidos

    init {
        cargarDatosUsuario()
        cargarPedidos()
    }

    private fun cargarDatosUsuario() {
        viewModelScope.launch {
            // TODO: Cargar datos de usuario desde el repositorio
            _usuario.value = Usuario(
                nombre = "Nombre de Usuario",
                email = "usuario@email.com",
                edad = 30, // Placeholder
                fechaNacimiento = "01/01/1990", // Placeholder
                direccion = "Calle Falsa 123", // Placeholder
                telefono = "123456789" // Placeholder
            )
        }
    }

    fun actualizarDatosUsuario(nombre: String, email: String, edad: Int, fechaNacimiento: String, direccion: String, telefono: String) {
        viewModelScope.launch {
            // TODO: Actualizar datos de usuario en el repositorio
            val usuarioActualizado = Usuario(nombre, email, edad, fechaNacimiento, direccion, telefono)
            _usuario.value = usuarioActualizado
        }
    }

    private fun cargarPedidos() {
        viewModelScope.launch {
            // TODO: Cargar pedidos desde el repositorio
            // TODO: Debes reemplazar R.drawable.ic_launcher_background con tus propias imágenes
            _pedidos.value = listOf(
                Pedido(
                    id = "1",
                    fecha = "2024-01-15",
                    productos = listOf(
                        Producto(
                            id = 1,
                            nombre = "Pastel de Chocolate",
                            precio = "25.0",
                            imagen = R.drawable.ic_launcher_background, // Placeholder
                            categoria = Categoria.TORTA_CIRCULAR,
                            descripcion = "Delicioso pastel de chocolate"
                        )
                    ),
                    total = 25.0
                ),
                Pedido(
                    id = "2",
                    fecha = "2024-02-20",
                    productos = listOf(
                        Producto(
                            id = 2,
                            nombre = "Pastel de Fresa",
                            precio = "22.0",
                            imagen = R.drawable.ic_launcher_background, // Placeholder
                            categoria = Categoria.TORTA_CUADRADA,
                            descripcion = "Pastel con fresas frescas"
                        )
                    ),
                    total = 22.0
                )
            )
        }
    }
}