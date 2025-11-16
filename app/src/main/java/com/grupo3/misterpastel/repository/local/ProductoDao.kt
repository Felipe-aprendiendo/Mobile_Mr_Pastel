package com.grupo3.misterpastel.repository.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM producto")
    fun obtenerTodos(): Flow<List<ProductoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>)

    @Query("DELETE FROM producto")
    suspend fun eliminarTodos()

    @Query("SELECT COUNT(*) FROM producto")
    suspend fun contar(): Int

    // 🔥 Necesario para mergear imagenLocal
    @Query("SELECT * FROM producto WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): ProductoEntity?
}
