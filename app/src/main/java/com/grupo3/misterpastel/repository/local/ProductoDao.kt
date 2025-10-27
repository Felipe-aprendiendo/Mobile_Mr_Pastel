package com.grupo3.misterpastel.repository.local


import androidx.room.*
import kotlinx.coroutines.flow.Flow

// MOD: Nuevo DAO para CRUD básico de productos.
@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>)

    @Query("SELECT * FROM producto ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM producto WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): ProductoEntity?

    @Query("SELECT COUNT(*) FROM producto")
    suspend fun contar(): Int

    @Query("DELETE FROM producto")
    suspend fun eliminarTodos()
}
