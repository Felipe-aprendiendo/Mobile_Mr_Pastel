package com.grupo3.misterpastel.repository.local


import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para representar los pedidos pagados.
 * Los items se almacenan como JSON simplificado.
 */
@Entity(tableName = "pedido")
data class PedidoEntity(
    @PrimaryKey val idPedido: String,
    val userId: String,
    val fecha: Long,
    val total: Double,
    val estado: String,
    val itemsJson: String // Se almacenan los CarritoItem serializados en JSON
)
