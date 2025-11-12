package com.grupo3.misterpastel.repository

import android.content.Context
import android.util.Log
import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.repository.local.AppDatabase
import com.grupo3.misterpastel.repository.local.ProductoEntity
import com.grupo3.misterpastel.repository.remote.ProductoRemoteDataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository principal del catálogo de productos.
 * Integra Room (local) + Retrofit (remoto) bajo patrón Repository.
 *
 * 🔹 Expone un StateFlow observable con todos los productos.
 * 🔹 Si la base local está vacía, la inicializa o la sincroniza desde API.
 */
class ProductoRepository private constructor(context: Context) {

    private val dao = AppDatabase.getDatabase(context).productoDao()
    private val remoteDataSource = ProductoRemoteDataSource()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // === Catálogo inicial (respaldo local) ===
    private val initialCatalog = listOf(
        Producto(1, "Torta Circular de Chocolate", "$45.000 CLP", "torta_chocolate",
            Categoria.TORTA_CIRCULAR, "Bizcocho húmedo de cacao con ganache y cobertura de chocolate amargo."),
        Producto(2, "Torta Cuadrada de Frutas", "$50.000 CLP", "torta_frutas",
            Categoria.TORTA_CUADRADA, "Esponjosa con crema pastelera y frutas frescas de temporada."),
        // ... resto del catálogo sin cambios ...
    )

    init {
        scope.launch {
            if (dao.contar() == 0) {
                // Primera carga: usa semilla local para evitar pantalla vacía
                dao.insertarProductos(initialCatalog.map { it.toEntity() })
            }

            // Observa continuamente los cambios locales
            dao.obtenerTodos().collect { entities ->
                _productos.value = entities.map { it.toModel() }
            }
        }
    }

    /**
     * Descarga los productos desde la API remota y actualiza la base local (Room).
     * Devuelve true si la sincronización fue exitosa.
     */
    suspend fun sincronizarDesdeApi(): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = withTimeoutOrNull(10_000) {
                remoteDataSource.obtenerProductos()
            } ?: return@withContext false

            if (response.isSuccessful) {
                val productosRemotos = response.body().orEmpty()
                actualizarCatalogoLocal(productosRemotos)
                Log.i("ProductoRepository", "✅ Sincronización exitosa: ${productosRemotos.size} productos.")
                true
            } else {
                Log.e("ProductoRepository", "⚠️ Error HTTP ${response.code()}: ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ProductoRepository", "❌ Error de red o parsing: ${e.message}", e)
            false
        }
    }

    /**
     * Actualiza el catálogo local en Room y en el flujo de datos.
     */
    private suspend fun actualizarCatalogoLocal(nuevo: List<Producto>) {
        dao.eliminarTodos()
        dao.insertarProductos(nuevo.map { it.toEntity() })
        _productos.value = nuevo
    }

    fun getProductoById(id: Int): Producto? =
        _productos.value.find { it.id == id }

    fun filtrarPorCategoria(cat: Categoria?): List<Producto> =
        if (cat == null) _productos.value else _productos.value.filter { it.categoria == cat }

    fun actualizarCatalogo(nuevo: List<Producto>) {
        scope.launch {
            actualizarCatalogoLocal(nuevo)
        }
    }

    // === Mapeos entre Entity y Model ===
    private fun ProductoEntity.toModel(): Producto = Producto(
        id = id,
        nombre = nombre,
        precio = precio,
        imagen = imagen,
        categoria = Categoria.valueOf(categoria),
        descripcion = descripcion
    )

    private fun Producto.toEntity(): ProductoEntity = ProductoEntity(
        id = id,
        nombre = nombre,
        precio = precio,
        imagen = imagen,
        categoria = categoria.name,
        descripcion = descripcion
    )

    companion object {
        @Volatile
        private var INSTANCE: ProductoRepository? = null

        fun getInstance(context: Context): ProductoRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ProductoRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
