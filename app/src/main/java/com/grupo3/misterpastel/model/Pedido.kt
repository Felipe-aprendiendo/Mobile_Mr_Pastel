package com.grupo3.misterpastel.model

data class Pedido(
    val id: String,
    val userId: String,
    val fecha: Long,
    val items: List<com.grupo3.misterpastel.repository.CarritoItem>,
    val total: Double,
    val estado: com.grupo3.misterpastel.repository.EstadoPedido
    
    
)