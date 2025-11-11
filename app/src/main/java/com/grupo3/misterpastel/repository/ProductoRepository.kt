package com.grupo3.misterpastel.repository

import android.content.Context
import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.repository.local.AppDatabase
import com.grupo3.misterpastel.repository.local.ProductoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Repository principal del catálogo de productos.
 * Integra Room con un catálogo inicial y expone un StateFlow observable.
 *
 * 🔹 Almacena nombres de imágenes (String) en lugar de IDs volátiles de recursos.
 * 🔹 Si la base de datos está vacía, realiza siembra inicial con 20 productos.
 */
class ProductoRepository private constructor(context: Context) {

    private val dao = AppDatabase.getDatabase(context).productoDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // === Catálogo inicial ===
    private val initialCatalog = listOf(
        Producto(1, "Torta Circular de Chocolate", "$45.000 CLP", "torta_chocolate",
            Categoria.TORTA_CIRCULAR, "Bizcocho húmedo de cacao con ganache y cobertura de chocolate amargo."),
        Producto(2, "Torta Cuadrada de Frutas", "$50.000 CLP", "torta_frutas",
            Categoria.TORTA_CUADRADA, "Esponjosa con crema pastelera y frutas frescas de temporada."),
        Producto(3, "Torta Especial de Cumpleaños", "$55.000 CLP", "torta_cumple",
            Categoria.TORTA_ESPECIAL, "Diseño personalizado con crema o fondant. Sabor vainilla o chocolate."),
        Producto(4, "Torta de Vainilla", "$42.000 CLP", "torta_de_vainilla",
            Categoria.TORTA_CIRCULAR, "Clásica torta de vainilla con relleno de crema chantilly y duraznos."),
        Producto(5, "Torta Manjar y Nuez", "$48.000 CLP", "torta_manjar_nuez",
            Categoria.TORTA_CUADRADA, "Bizcocho suave relleno con manjar casero y trozos de nuez tostada."),
        Producto(6, "Torta de Naranja", "$43.000 CLP", "torta_naranja",
            Categoria.TORTA_CIRCULAR, "Húmeda y aromática con crema de naranja natural."),
        Producto(7, "Mousse de Chocolate", "$5.000 CLP", "mousse_chocolate",
            Categoria.POSTRE_INDIVIDUAL, "Textura ligera de cacao premium decorada con virutas de chocolate."),
        Producto(8, "Cheesecake de Frutilla", "$5.500 CLP", "chee_frutilla",
            Categoria.POSTRE_INDIVIDUAL, "Base crocante con suave mezcla de queso crema y salsa de frutilla natural."),
        Producto(9, "Pie de Limón", "$4.800 CLP", "pie_limon",
            Categoria.POSTRE_INDIVIDUAL, "Suave crema de limón sobre masa crujiente, cubierta de merengue italiano."),
        Producto(10, "Tiramisú Tradicional", "$6.000 CLP", "tiramisu",
            Categoria.POSTRE_INDIVIDUAL, "Clásico italiano con mascarpone, bizcochos de café y cacao en polvo."),
        Producto(11, "Empanada de Manzana", "$3.000 CLP", "empanada_manzana",
            Categoria.PASTELERIA_TRADICIONAL, "Masa crujiente rellena de compota de manzana con canela."),
        Producto(12, "Galletas de Avena", "$2.000 CLP", "galletas_avena",
            Categoria.PASTELERIA_TRADICIONAL, "Crujientes galletas caseras con avena natural y toque de miel."),
        Producto(13, "Brownie Sin Gluten", "$3.800 CLP", "brownie_sin_gluten",
            Categoria.PRODUCTO_SIN_AZUCAR, "Delicioso brownie sin harina de trigo ni azúcar refinada."),
        Producto(14, "Pan Sin Gluten", "$2.500 CLP", "pan_sin_gluten",
            Categoria.PRODUCTO_SIN_GLUTEN, "Pan artesanal sin gluten, ideal para acompañar desayunos y meriendas."),
        Producto(15, "Torta Vegana de Chocolate", "$46.000 CLP", "chocolate_vegano",
            Categoria.PRODUCTO_VEGANO, "Preparada con cacao puro, aceite vegetal y sin ingredientes de origen animal."),
        Producto(16, "Torta Vegana de Frutas", "$48.000 CLP", "torta_vegana",
            Categoria.PRODUCTO_VEGANO, "Esponjosa y natural, endulzada con frutas y sin lácteos."),
        Producto(17, "Pastel de Boda", "$75.000 CLP", "pastel_boda",
            Categoria.TORTA_ESPECIAL, "Diseño elegante con fondant blanco y detalles florales comestibles."),
        Producto(18, "Pastel de Cumpleaños", "$60.000 CLP", "pastel_cumpleanos",
            Categoria.TORTA_ESPECIAL, "Personalizable con mensaje, color y relleno al gusto."),
        Producto(19, "Pastel Empresarial", "$70.000 CLP", "pastel_empresa",
            Categoria.TORTA_ESPECIAL, "Decorado con el logo de la empresa y colores institucionales."),
        Producto(20, "Tarta Santiago", "$4.500 CLP", "tarta_santiago",
            Categoria.POSTRE_INDIVIDUAL, "Tradicional tarta gallega de almendras, sin gluten y sin lácteos.")
    )

    init {
        scope.launch {
            if (dao.contar() == 0) {
                dao.insertarProductos(initialCatalog.map { it.toEntity() })
            }
            dao.obtenerTodos().collect { entities ->
                _productos.value = entities.map { it.toModel() }
            }
        }
    }

    fun getProductoById(id: Int): Producto? = _productos.value.find { it.id == id }

    fun filtrarPorCategoria(cat: Categoria?): List<Producto> =
        if (cat == null) _productos.value else _productos.value.filter { it.categoria == cat }

    fun actualizarCatalogo(nuevo: List<Producto>) {
        scope.launch {
            dao.eliminarTodos()
            dao.insertarProductos(nuevo.map { it.toEntity() })
        }
    }

    // === Mapeos ===
    private fun ProductoEntity.toModel(): Producto = Producto(
        id = id,
        nombre = nombre,
        precio = precio,
        imagen = imagen, // String
        categoria = Categoria.valueOf(categoria),
        descripcion = descripcion
    )

    private fun Producto.toEntity(): ProductoEntity = ProductoEntity(
        id = id,
        nombre = nombre,
        precio = precio,
        imagen = imagen, // String
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
