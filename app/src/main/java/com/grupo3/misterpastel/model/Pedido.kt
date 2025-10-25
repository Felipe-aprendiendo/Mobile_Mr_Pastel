package com.grupo3.misterpastel.model

data class Pedido(
    val id: String,
    val fecha: String,
    val productos: List<Producto>,
    val total: Double,
    val estado: EstadoPedido
)