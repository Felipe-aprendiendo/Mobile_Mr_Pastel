package com.grupo3.misterpastel.viewmodel.state

import com.grupo3.misterpastel.repository.EstadoPedido

/**
 * Estado del pedido simulado para mostrar en la UI.
 */
data class PedidoState(
    val estado: EstadoPedido = EstadoPedido.PENDIENTE,
    val mensaje: String? = null
)
