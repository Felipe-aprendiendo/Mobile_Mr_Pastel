package com.grupo3.misterpastel.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos central de la aplicación Mr. Pastel.
 *
 * Incluye:
 *  - PedidoEntity → Persistencia de pedidos (historial de compras)
 *  - UsuarioEntity → Datos de usuarios y sesiones
 *  - ProductoEntity → Catálogo persistente de productos
 *
 * 🔹 Usa Room 2.6+ con `StateFlow` en los repositorios.
 * 🔹 Se mantiene `fallbackToDestructiveMigration()` solo para entorno de desarrollo.
 *    (⚠️ En producción debería reemplazarse por migraciones explícitas.)
 */
@Database(
    entities = [
        PedidoEntity::class,
        UsuarioEntity::class,
        ProductoEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pedidoDao(): PedidoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao

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
                    // ⚠️ Borra y recrea la BD al cambiar schema (solo para desarrollo)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
