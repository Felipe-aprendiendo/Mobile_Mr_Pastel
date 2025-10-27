package com.grupo3.misterpastel.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos central de la app.
 * Incluye pedidos (persistencia de compras) y usuarios (sesiones y perfil).
 *
 * MOD:
 * - Se añadió ProductoEntity y su DAO.
 * - Se incrementó la version de 2 a 3 (nuevo schema).
 */
@Database(
    entities = [PedidoEntity::class, UsuarioEntity::class, ProductoEntity::class], // MOD: agregar ProductoEntity
    version = 3, // MOD: antes era 2
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pedidoDao(): PedidoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao // MOD: nuevo DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mrpastel_db"
                )
                    // MOD: mantenemos destructive migration por simplicidad en desarrollo
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
