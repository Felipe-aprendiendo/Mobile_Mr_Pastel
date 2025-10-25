package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.model.CarritoItem
import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.model.EstadoPedido
import com.grupo3.misterpastel.model.Pedido
import com.grupo3.misterpastel.model.Producto
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PedidoViewModel : ViewModel() {

    private val _pedidos = MutableLiveData<List<Pedido>>(emptyList())
    val pedidos: LiveData<List<Pedido>> = _pedidos

    init {
        cargarPedidosDeEjemplo()
    }

    fun crearPedido(carritoItems: List<CarritoItem>, total: Double) {
        val productos = carritoItems.map { it.producto }
        val nuevoPedido = Pedido(
            id = UUID.randomUUID().toString(),
            fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            productos = productos,
            total = total,
            estado = EstadoPedido.PENDIENTE
        )

        val listaActual = _pedidos.value?.toMutableList() ?: mutableListOf()
        listaActual.add(nuevoPedido)
        _pedidos.value = listaActual
    }

    fun actualizarEstadoPedido(pedidoId: String, nuevoEstado: EstadoPedido) {
        val listaActual = _pedidos.value?.toMutableList() ?: return
        val pedidoAActualizar = listaActual.find { it.id == pedidoId }

        if (pedidoAActualizar != null) {
            val indice = listaActual.indexOf(pedidoAActualizar)
            val pedidoActualizado = pedidoAActualizar.copy(estado = nuevoEstado)
            listaActual[indice] = pedidoActualizado
            _pedidos.value = listaActual
        }
    }

    private fun cargarPedidosDeEjemplo() {
        viewModelScope.launch {
            _pedidos.value = listOf(
                Pedido(
                    id = "1",
                    fecha = "15/05/2024",
                    productos = listOf(
                        Producto(1, "Torta de Chocolate", "20.000", R.drawable.ic_launcher_background, Categoria.TORTA_CIRCULAR, "Torta de chocolate para 15 personas")
                    ),
                    total = 20000.0,
                    estado = EstadoPedido.ENTREGADO
                ),
                Pedido(
                    id = "2",
                    fecha = "16/05/2024",
                    productos = listOf(
                        Producto(2, "Cheesecake de Fresa", "15.000", R.drawable.ic_launcher_background, Categoria.POSTRE_INDIVIDUAL, "Postre individual de cheesecake.")
                    ),
                    total = 15000.0,
                    estado = EstadoPedido.EN_PREPARACION
                )
            )
        }
    }
}