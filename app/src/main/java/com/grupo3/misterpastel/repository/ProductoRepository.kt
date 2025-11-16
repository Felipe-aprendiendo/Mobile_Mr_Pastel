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

class ProductoRepository private constructor(context: Context) {

    private val dao = AppDatabase.getDatabase(context).productoDao()
    private val remote = ProductoRemoteDataSource()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> get() = _productos

    // ============================================================
    // Catálogo local (fallback)
    // ============================================================
    private val initialCatalog = listOf(
        Producto(
            id = 1,
            nombre = "Torta Circular de Chocolate",
            precio = "45000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216602/torta_chocolate_w0ljvk.jpg",
            categoria = Categoria.TORTA_CIRCULAR,
            descripcion = "Bizcocho húmedo de cacao con ganache y cobertura de chocolate amargo.",
            imagenLocal = "torta_chocolate"
        ),
        Producto(
            id = 2,
            nombre = "Torta Cuadrada de Frutas",
            precio = "50000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216602/torta_frutas_aorc8b.jpg",
            categoria = Categoria.TORTA_CUADRADA,
            descripcion = "Esponjosa con crema pastelera y frutas frescas de temporada.",
            imagenLocal = "torta_frutas"
        ),
        Producto(
            id = 3,
            nombre = "Torta Especial de Cumpleaños",
            precio = "55000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216601/torta_cumple_utfh3i.jpg",
            categoria = Categoria.TORTA_ESPECIAL,
            descripcion = "Diseño personalizado con crema o fondant. Sabor vainilla o chocolate.",
            imagenLocal = "torta_cumple"
        ),
        Producto(
            id = 4,
            nombre = "Torta de Vainilla",
            precio = "42000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216602/torta_de_vainilla_filuid.jpg",
            categoria = Categoria.TORTA_CIRCULAR,
            descripcion = "Clásica torta de vainilla con relleno de crema chantilly y duraznos.",
            imagenLocal = "torta_de_vainilla"
        ),
        Producto(
            id = 5,
            nombre = "Torta Manjar y Nuez",
            precio = "48000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216602/torta_manjar_nuez_lsu1ry.jpg",
            categoria = Categoria.TORTA_CUADRADA,
            descripcion = "Bizcocho suave relleno con manjar casero y trozos de nuez tostada.",
            imagenLocal = "torta_manjar_nuez"
        ),
        Producto(
            id = 6,
            nombre = "Torta de Naranja",
            precio = "43000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216604/torta_naranja_perpgi.jpg",
            categoria = Categoria.TORTA_CIRCULAR,
            descripcion = "Húmeda y aromática con crema de naranja natural.",
            imagenLocal = "torta_naranja"
        ),
        Producto(
            id = 7,
            nombre = "Mousse de Chocolate",
            precio = "5000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216652/mousse_chocolate_jqz8jy.jpg",
            categoria = Categoria.POSTRE_INDIVIDUAL,
            descripcion = "Textura ligera de cacao premium decorada con virutas de chocolate.",
            imagenLocal = "mousse_chocolate"
        ),
        Producto(
            id = 8,
            nombre = "Cheesecake de Frutilla",
            precio = "5500",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216652/chee_frutilla_mepcix.jpg",
            categoria = Categoria.POSTRE_INDIVIDUAL,
            descripcion = "Base crocante con suave mezcla de queso crema y salsa de frutilla natural.",
            imagenLocal = "chee_frutilla"
        ),
        Producto(
            id = 9,
            nombre = "Pie de Limón",
            precio = "4800",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216654/pie_limon_rrcovt.jpg",
            categoria = Categoria.POSTRE_INDIVIDUAL,
            descripcion = "Suave crema de limón sobre masa crujiente, cubierta de merengue italiano.",
            imagenLocal = "pie_limon"
        ),
        Producto(
            id = 10,
            nombre = "Tiramisú Tradicional",
            precio = "6000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216688/tiramisu_xtppjk.jpg",
            categoria = Categoria.POSTRE_INDIVIDUAL,
            descripcion = "Clásico italiano con mascarpone, bizcochos de café y cacao en polvo.",
            imagenLocal = "tiramisu"
        ),
        Producto(
            id = 11,
            nombre = "Empanada de Manzana",
            precio = "3000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216773/empanada_manzana_q9wr9m.jpg",
            categoria = Categoria.PASTELERIA_TRADICIONAL,
            descripcion = "Masa crujiente rellena de compota de manzana con canela.",
            imagenLocal = "empanada_manzana"
        ),
        Producto(
            id = 12,
            nombre = "Galletas de Avena",
            precio = "2000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216774/galletas_avena_ebdlbm.jpg",
            categoria = Categoria.PASTELERIA_TRADICIONAL,
            descripcion = "Crujientes galletas caseras con avena natural y toque de miel.",
            imagenLocal = "galletas_avena"
        ),
        Producto(
            id = 13,
            nombre = "Brownie Sin Gluten",
            precio = "3800",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216779/brownie_sin_gluten_lhisws.jpg",
            categoria = Categoria.PRODUCTO_SIN_AZUCAR,
            descripcion = "Delicioso brownie sin harina de trigo ni azúcar refinada.",
            imagenLocal = "brownie_sin_gluten"
        ),
        Producto(
            id = 14,
            nombre = "Pan Sin Gluten",
            precio = "2500",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216800/pan_sin_gluten_ibypba.jpg",
            categoria = Categoria.PRODUCTO_SIN_GLUTEN,
            descripcion = "Pan artesanal sin gluten, ideal para acompañar desayunos y meriendas.",
            imagenLocal = "pan_sin_gluten"
        ),
        Producto(
            id = 15,
            nombre = "Torta Vegana de Chocolate",
            precio = "46000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216853/chocolate_vegano_eeerik.jpg",
            categoria = Categoria.PRODUCTO_VEGANO,
            descripcion = "Preparada con cacao puro, aceite vegetal y sin ingredientes de origen animal.",
            imagenLocal = "chocolate_vegano"
        ),
        Producto(
            id = 16,
            nombre = "Torta Vegana de Frutas",
            precio = "48000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216855/torta_vegana_lkwg5u.jpg",
            categoria = Categoria.PRODUCTO_VEGANO,
            descripcion = "Esponjosa y natural, endulzada con frutas y sin lácteos.",
            imagenLocal = "torta_vegana"
        ),
        Producto(
            id = 17,
            nombre = "Pastel de Boda",
            precio = "75000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216908/pastel_boda_jz4wdp.jpg",
            categoria = Categoria.TORTA_ESPECIAL,
            descripcion = "Diseño elegante con fondant blanco y detalles florales comestibles.",
            imagenLocal = "pastel_boda"
        ),
        Producto(
            id = 18,
            nombre = "Pastel de Cumpleaños",
            precio = "60000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216903/pastel_cumpleanos_ldd1ji.jpg",
            categoria = Categoria.TORTA_ESPECIAL,
            descripcion = "Personalizable con mensaje, color y relleno al gusto.",
            imagenLocal = "pastel_cumpleanos"
        ),
        Producto(
            id = 19,
            nombre = "Pastel Empresarial",
            precio = "70000",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763216906/pastel_empresa_c6ag0c.jpg",
            categoria = Categoria.TORTA_ESPECIAL,
            descripcion = "Decorado con el logo de la empresa y colores institucionales.",
            imagenLocal = "pastel_empresa"
        ),
        Producto(
            id = 20,
            nombre = "Tarta Santiago",
            precio = "4500",
            imagen = "https://res.cloudinary.com/dk9zqy62c/image/upload/v1763241853/tarta_santiago_hy30gt.jpg",
            categoria = Categoria.POSTRE_INDIVIDUAL,
            descripcion = "Tradicional tarta gallega de almendras, sin gluten y sin lácteos.",
            imagenLocal = "tarta_santiago"
        )
    )



    init {
        scope.launch {
            // Si Room está vacío, insertar fallback local
            if (dao.contar() == 0) {
                dao.insertarProductos(initialCatalog.map { it.toEntity() })
            }

            // Observar los datos locales siempre
            dao.obtenerTodos().collect { entities ->
                _productos.value = entities.map { it.toModel() }
            }
        }
    }

    // ============================================================
    // 🟧 Sincronización con APEX (ORDS: { "items": [...] })
    // ============================================================
    suspend fun sincronizarDesdeApi(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i("ProductoRepository", "🌐 Sincronizando productos desde API...")

            val response = remote.obtenerProductos()

            if (!response.isSuccessful) {
                Log.e("ProductoRepository",
                    "❌ Error HTTP ${response.code()}: ${response.message()}")
                return@withContext false
            }

            val items = response.body()?.items ?: emptyList()

            if (items.isEmpty()) {
                Log.w("ProductoRepository", "⚠ API devolvió lista vacía")
                return@withContext false
            }

            // 🔥 Merge para NO PERDER imagenLocal
            val merged = items.map { rem ->
                val local = dao.findById(rem.id)
                rem.copy(
                    imagenLocal = local?.imagenLocal // conserva imagen local
                )
            }

            // Guardar en Room
            dao.eliminarTodos()
            dao.insertarProductos(merged.map { it.toEntity() })

            Log.i("ProductoRepository", "✅ Sincronización completada con éxito")

            true

        } catch (e: Exception) {
            Log.e("ProductoRepository",
                "❌ Error de red: ${e.message}", e)
            false
        }
    }

    // ============================================================
    // Helpers
    // ============================================================

    fun getProductoById(id: Int) =
        _productos.value.find { it.id == id }

    fun filtrarPorCategoria(cat: Categoria?) =
        if (cat == null) _productos.value else _productos.value.filter { it.categoria == cat }

    // ---------------------- MAPEOS ------------------------------
    private fun ProductoEntity.toModel() = Producto(
        id, nombre, precio, imagen, Categoria.valueOf(categoria), descripcion, imagenLocal
    )

    private fun Producto.toEntity() = ProductoEntity(
        id, nombre, precio, imagen, categoria.name, descripcion, imagenLocal
    )

    // Singleton
    companion object {
        @Volatile private var INSTANCE: ProductoRepository? = null

        fun getInstance(context: Context): ProductoRepository {
            return INSTANCE ?: synchronized(this) {
                ProductoRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
