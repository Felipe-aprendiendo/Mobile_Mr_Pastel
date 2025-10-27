package com.grupo3.misterpastel.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.grupo3.misterpastel.model.Pedido
import com.grupo3.misterpastel.model.CarritoItem
import com.grupo3.misterpastel.model.EstadoPedido
import com.grupo3.misterpastel.model.ComprobantePago
import com.grupo3.misterpastel.repository.local.AppDatabase
import com.grupo3.misterpastel.repository.local.PedidoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Gestiona el almacenamiento y recuperación de pedidos
 * desde la base de datos local (Room).
 */
class PedidoRepository(private val context: Context) {

    private val pedidoDao = AppDatabase.getDatabase(context).pedidoDao()
    private val gson = Gson()

    // 🔹 Convierte la entidad Room en el modelo Pedido para la UI
    private fun PedidoEntity.toModel(): Pedido {
        val itemsList = try {
            gson.fromJson(itemsJson, Array<CarritoItem>::class.java)?.toList() ?: emptyList()
        } catch (e: Exception) {
            Log.e("PedidoRepository", "Error al convertir JSON de items", e)
            emptyList()
        }

        return Pedido(
            id = idPedido,
            userId = userId,
            fecha = fecha,
            items = itemsList,
            total = total,
            estado = EstadoPedido.valueOf(estado)
        )
    }

    // 🔹 Convierte un Pedido en la entidad Room para guardarlo
    private fun Pedido.toEntity(): PedidoEntity {
        val json = gson.toJson(items)
        return PedidoEntity(
            idPedido = id,
            userId = userId,
            fecha = fecha,
            total = total,
            estado = estado.name,
            itemsJson = json
        )
    }

    // 🔹 Flujo general de todos los pedidos
    fun obtenerPedidos(): Flow<List<Pedido>> =
        pedidoDao.obtenerTodosLosPedidos().map { list -> list.map { it.toModel() } }

    // 🔹 Flujo filtrado por usuario
    fun obtenerPedidosPorUsuario(userId: String): Flow<List<Pedido>> =
        pedidoDao.obtenerPedidosPorUsuario(userId).map { list -> list.map { it.toModel() } }

    // 🔹 Inserta un pedido directamente
    suspend fun insertarPedido(pedido: Pedido) = withContext(Dispatchers.IO) {
        pedidoDao.insertarPedido(pedido.toEntity())
        Log.d("PedidoRepository", "Pedido insertado manualmente con ID ${pedido.id}")
    }

    // 🔹 Crea un pedido a partir de un comprobante de pago
    suspend fun insertarPedidoDesdeComprobante(
        userId: String,
        comprobante: ComprobantePago
    ) = withContext(Dispatchers.IO) {
        if (userId.isBlank()) {
            Log.e("PedidoRepository", "⚠️ El userId está vacío. No se guardará el pedido.")
            return@withContext
        }

        val nuevoPedido = Pedido(
            id = comprobante.idComprobante,
            userId = userId,
            fecha = comprobante.fechaHoraMillis,
            items = comprobante.items,
            total = comprobante.totalFinal,
            estado = EstadoPedido.PENDIENTE
        )

        pedidoDao.insertarPedido(nuevoPedido.toEntity())
        Log.d("PedidoRepository", "✅ Pedido insertado en Room para usuario $userId")
    }
}
