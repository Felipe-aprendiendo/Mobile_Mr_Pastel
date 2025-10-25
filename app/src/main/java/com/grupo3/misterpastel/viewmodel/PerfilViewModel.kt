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
import com.grupo3.misterpastel.repository.CarritoItem
import com.grupo3.misterpastel.repository.EstadoPedido
import kotlinx.coroutines.launch
import java.util.UUID

class PerfilViewModel : ViewModel() {

    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario

    private val _pedidos = MutableLiveData<List<Pedido>>()
    val pedidos: LiveData<List<Pedido>> = _pedidos

    init {
        cargarDatosUsuario()
        cargarPedidos()
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

    /**
     * Carga pedidos de ejemplo asociados al usuario actual.
     */
    private fun cargarPedidos() {
        viewModelScope.launch {
            val pedido1 = Pedido(
                id = "P001",
                userId = "U001",
                fecha = System.currentTimeMillis() - 86400000, // hace 1 día
                items = listOf(
                    CarritoItem(
                        producto = Producto(
                            id = 1,
                            nombre = "Torta de Chocolate",
                            precio = "20000",
                            imagen = R.drawable.ic_launcher_background,
                            categoria = Categoria.TORTA_CIRCULAR,
                            descripcion = "Torta clásica de chocolate"
                        ),
                        cantidad = 1
                    )
                ),
                total = 20000.0,
                estado = EstadoPedido.ENTREGADO
            )

            val pedido2 = Pedido(
                id = "P002",
                userId = "U001",
                fecha = System.currentTimeMillis() - 43200000, // hace 12 horas
                items = listOf(
                    CarritoItem(
                        producto = Producto(
                            id = 2,
                            nombre = "Cheesecake de Fresa",
                            precio = "15000",
                            imagen = R.drawable.ic_launcher_background,
                            categoria = Categoria.POSTRE_INDIVIDUAL,
                            descripcion = "Cheesecake con fresas naturales"
                        ),
                        cantidad = 1
                    )
                ),
                total = 15000.0,
                estado = EstadoPedido.PREPARANDO
            )

            _pedidos.value = listOf(pedido1, pedido2)
        }
    }
}
