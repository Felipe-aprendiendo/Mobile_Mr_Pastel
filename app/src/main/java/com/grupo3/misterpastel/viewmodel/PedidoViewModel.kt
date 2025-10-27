package com.grupo3.misterpastel.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.grupo3.misterpastel.model.CarritoItem
import com.grupo3.misterpastel.model.ComprobantePago
import com.grupo3.misterpastel.model.EstadoPedido
import com.grupo3.misterpastel.model.Pedido
import com.grupo3.misterpastel.repository.PedidoRepository
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel responsable de gestionar los pedidos:
 * - Lee los pedidos persistidos en Room (SQLite).
 * - Permite crear o registrar pedidos nuevos.
 * - Actualiza el estado de los pedidos.
 */
class PedidoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PedidoRepository(application)

    // ID del usuario actual (se setea tras el login o sesión activa)
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    // LiveData que observa los pedidos del usuario en Room
    val pedidos: LiveData<List<Pedido>> =
        _userId.switchMap { id ->
            repository.obtenerPedidosPorUsuario(id).asLiveData()
        }

    /**
     * Define el usuario actual para filtrar sus pedidos.
     * Llamar desde SessionViewModel o al iniciar sesión.
     */
    fun setUserId(id: String) {
        _userId.value = id
    }

    /**
     * Inserta un nuevo pedido en Room a partir de los items del carrito.
     */
    fun crearPedido(userId: String, carritoItems: List<CarritoItem>, total: Double) {
        viewModelScope.launch {
            val nuevoPedido = Pedido(
                id = UUID.randomUUID().toString(),
                userId = userId,
                fecha = System.currentTimeMillis(),
                items = carritoItems,
                total = total,
                estado = EstadoPedido.PENDIENTE
            )
            repository.insertarPedido(nuevoPedido)
        }
    }

    /**
     * Registra un nuevo pedido en Room a partir de un comprobante de pago.
     */
    fun registrarPedidoDesdeComprobante(userId: String, comprobante: ComprobantePago) {
        viewModelScope.launch {
            repository.insertarPedidoDesdeComprobante(userId, comprobante)
        }
    }

    /**
     * Actualiza el estado de un pedido existente (por ejemplo: PREPARANDO → ENTREGADO).
     */
    fun actualizarEstadoPedido(pedidoId: String, nuevoEstado: EstadoPedido) {
        val pedidosActuales = pedidos.value?.toMutableList() ?: return
        val indice = pedidosActuales.indexOfFirst { it.id == pedidoId }
        if (indice != -1) {
            val pedidoActualizado = pedidosActuales[indice].copy(estado = nuevoEstado)
            pedidosActuales[indice] = pedidoActualizado
            // Reinsertamos el pedido actualizado en Room
            viewModelScope.launch {
                repository.insertarPedido(pedidoActualizado)
            }
        }
    }
}
