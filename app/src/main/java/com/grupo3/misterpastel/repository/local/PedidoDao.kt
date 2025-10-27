package com.grupo3.misterpastel.repository.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPedido(pedido: PedidoEntity)

    @Query("SELECT * FROM pedido ORDER BY fecha DESC")
    fun obtenerTodosLosPedidos(): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedido WHERE userId = :userId ORDER BY fecha DESC")
    fun obtenerPedidosPorUsuario(userId: String): Flow<List<PedidoEntity>>
}
