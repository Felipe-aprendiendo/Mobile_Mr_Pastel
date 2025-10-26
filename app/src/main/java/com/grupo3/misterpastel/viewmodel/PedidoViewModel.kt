package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.model.CarritoItem
import com.grupo3.misterpastel.model.Pedido
import com.grupo3.misterpastel.model.EstadoPedido
import kotlinx.coroutines.launch
import java.util.UUID

class PedidoViewModel : ViewModel() {

    // Lista observable de pedidos
    private val _pedidos = MutableLiveData<List<Pedido>>(emptyList())
    val pedidos: LiveData<List<Pedido>> = _pedidos

    init {
        cargarPedidosDeEjemplo()
    }

    /**
     * Crea un nuevo pedido a partir de los items del carrito.
     */
    fun crearPedido(userId: String, carritoItems: List<CarritoItem>, total: Double) {
        val nuevoPedido = Pedido(
            id = UUID.randomUUID().toString(),
            userId = userId,
            fecha = System.currentTimeMillis(), // fecha como Long
            items = carritoItems,
            total = total,
            estado = EstadoPedido.PENDIENTE
        )

        val listaActual = _pedidos.value?.toMutableList() ?: mutableListOf()
        listaActual.add(nuevoPedido)
        _pedidos.value = listaActual
    }

    /**
     * Actualiza el estado de un pedido existente.
     */
    fun actualizarEstadoPedido(pedidoId: String, nuevoEstado: EstadoPedido) {
        val listaActual = _pedidos.value?.toMutableList() ?: return
        val indice = listaActual.indexOfFirst { it.id == pedidoId }

        if (indice != -1) {
            val pedidoActualizado = listaActual[indice].copy(estado = nuevoEstado)
            listaActual[indice] = pedidoActualizado
            _pedidos.value = listaActual
        }
    }

    /**
     * Carga pedidos de ejemplo para visualización inicial.
     */
    private fun cargarPedidosDeEjemplo() {
        viewModelScope.launch {
            val pedidoEjemplo1 = Pedido(
                id = "1",
                userId = "U001",
                fecha = System.currentTimeMillis() - 86400000, // 1 día atrás
                items = listOf(
                    CarritoItem(
                        producto = Producto(
                            id = 1,
                            nombre = "Torta de Chocolate",
                            precio = "20000",
                            imagen = R.drawable.ic_launcher_background,
                            categoria = Categoria.TORTA_CIRCULAR,
                            descripcion = "Torta de chocolate para 15 personas"
                        ),
                        cantidad = 1
                    )
                ),
                total = 20000.0,
                estado = EstadoPedido.ENTREGADO
            )

            val pedidoEjemplo2 = Pedido(
                id = "2",
                userId = "U002",
                fecha = System.currentTimeMillis() - 43200000, // 12 horas atrás
                items = listOf(
                    CarritoItem(
                        producto = Producto(
                            id = 2,
                            nombre = "Cheesecake de Fresa",
                            precio = "15000",
                            imagen = R.drawable.ic_launcher_background,
                            categoria = Categoria.POSTRE_INDIVIDUAL,
                            descripcion = "Postre individual de cheesecake de fresa"
                        ),
                        cantidad = 1
                    )
                ),
                total = 15000.0,
                estado = EstadoPedido.EN_PREPARACION
            )

            _pedidos.value = listOf(pedidoEjemplo1, pedidoEjemplo2)
        }
    }
}
