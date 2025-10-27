package com.grupo3.misterpastel.repository

import android.content.Context
import com.grupo3.misterpastel.R
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
 * IMPORTANTE:
 * - Antes era un object en memoria. Ahora es una clase Repository con Room y Singleton por contexto.
 * - Expone StateFlow<List<Producto>> alimentado desde Room.
 * - Si la tabla está vacía, siembra los 20 productos iniciales.
 */
class ProductoRepository private constructor(context: Context) {

    private val dao = AppDatabase.getDatabase(context).productoDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Catálogo observable desde la UI
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // === Catálogo inicial  ===
    private val initialCatalog = listOf(
        // === TORTAS CLÁSICAS ===
        Producto(
            id = 1,
            nombre = "Torta Circular de Chocolate",
            precio = "$45.000 CLP",
            imagen = R.drawable.torta_chocolate,
            categoria = Categoria.TORTA_CIRCULAR,
            descripcion = "Bizcocho húmedo de cacao con ganache y cobertura de chocolate amargo."
        ),
        Producto(
            id = 2,
            nombre = "Torta Cuadrada de Frutas",
            precio = "$50.000 CLP",
            imagen = R.drawable.torta_frutas,
            categoria = Categoria.TORTA_CUADRADA,
            descripcion = "Esponjosa con crema pastelera y frutas frescas de temporada."
        ),
        Producto(
            id = 3,
            nombre = "Torta Especial de Cumpleaños",
            precio = "$55.000 CLP",
            imagen = R.drawable.torta_cumple,
            categoria = Categoria.TORTA_ESPECIAL,
            descripcion = "Diseño personalizado con crema o fondant. Sabor vainilla o chocolate."
        ),
        Producto(
            id = 4,
            nombre = "Torta de Vainilla",
            precio = "$42.000 CLP",
            imagen = R.drawable.torta_de_vainilla,
            categoria = Categoria.TORTA_CIRCULAR,
            descripcion = "Clásica torta de vainilla con relleno de crema chantilly y duraznos."
        ),
        Producto(
            id = 5,
            nombre = "Torta Manjar y Nuez",
            precio = "$48.000 CLP",
            imagen = R.drawable.torta_manjar_nuez,
            categoria = Categoria.TORTA_CUADRADA,
            descripcion = "Bizcocho suave relleno con manjar casero y trozos de nuez tostada."
        ),
        Producto(
            id = 6,
            nombre = "Torta de Naranja",
            precio = "$43.000 CLP",
            imagen = R.drawable.torta_naranja,
            categoria = Categoria.TORTA_CIRCULAR,
            descripcion = "Húmeda y aromática con crema de naranja natural."
        ),

        // === POSTRES INDIVIDUALES ===
        Producto(
            id = 7,
            nombre = "Mousse de Chocolate",
            precio = "$5.000 CLP",
            imagen = R.drawable.mousse_chocolate,
            categoria = Categoria.POSTRE_INDIVIDUAL,
            descripcion = "Textura ligera de cacao premium decorada con virutas de chocolate."
        ),
        Producto(
            id = 8,
            nombre = "Cheesecake de Frutilla",
            precio = "$5.500 CLP",
            imagen = R.drawable.chee_frutilla,
            categoria = Categoria.POSTRE_INDIVIDUAL,
            descripcion = "Base crocante con suave mezcla de queso crema y salsa de frutilla natural."
        ),
        Producto(
            id = 9,
            nombre = "Pie de Limón",
            precio = "$4.800 CLP",
            imagen = R.drawable.pie_limon,
            categoria = Categoria.POSTRE_INDIVIDUAL,
            descripcion = "Suave crema de limón sobre masa crujiente, cubierta de merengue italiano."
        ),
        Producto(
            id = 10,
            nombre = "Tiramisú Tradicional",
            precio = "$6.000 CLP",
            imagen = R.drawable.tiramisu,
            categoria = Categoria.POSTRE_INDIVIDUAL,
            descripcion = "Clásico italiano con mascarpone, bizcochos de café y cacao en polvo."
        ),
        Producto(
            id = 11,
            nombre = "Empanada de Manzana",
            precio = "$3.000 CLP",
            imagen = R.drawable.empanada_manzana,
            categoria = Categoria.PASTELERIA_TRADICIONAL,
            descripcion = "Masa crujiente rellena de compota de manzana con canela."
        ),
        Producto(
            id = 12,
            nombre = "Galletas de Avena",
            precio = "$2.000 CLP",
            imagen = R.drawable.galletas_avena,
            categoria = Categoria.PASTELERIA_TRADICIONAL,
            descripcion = "Crujientes galletas caseras con avena natural y toque de miel."
        ),

        // === ESPECIALES SIN AZÚCAR / SIN GLUTEN / VEGANOS ===
        Producto(
            id = 13,
            nombre = "Brownie Sin Gluten",
            precio = "$3.800 CLP",
            imagen = R.drawable.brownie_sin_gluten,
            categoria = Categoria.PRODUCTO_SIN_AZUCAR,
            descripcion = "Delicioso brownie sin harina de trigo ni azúcar refinada."
        ),
        Producto(
            id = 14,
            nombre = "Pan Sin Gluten",
            precio = "$2.500 CLP",
            imagen = R.drawable.pan_sin_gluten,
            categoria = Categoria.PRODUCTO_SIN_GLUTEN,
            descripcion = "Pan artesanal sin gluten, ideal para acompañar desayunos y meriendas."
        ),
        Producto(
            id = 15,
            nombre = "Torta Vegana de Chocolate",
            precio = "$46.000 CLP",
            imagen = R.drawable.chocolate_vegano,
            categoria = Categoria.PRODUCTO_VEGANO,
            descripcion = "Preparada con cacao puro, aceite vegetal y sin ingredientes de origen animal."
        ),
        Producto(
            id = 16,
            nombre = "Torta Vegana de Frutas",
            precio = "$48.000 CLP",
            imagen = R.drawable.torta_vegana,
            categoria = Categoria.PRODUCTO_VEGANO,
            descripcion = "Esponjosa y natural, endulzada con frutas y sin lácteos."
        ),

        // === TORTAS PERSONALIZADAS / EMPRESARIALES ===
        Producto(
            id = 17,
            nombre = "Pastel de Boda",
            precio = "$75.000 CLP",
            imagen = R.drawable.pastel_boda,
            categoria = Categoria.TORTA_ESPECIAL,
            descripcion = "Diseño elegante con fondant blanco y detalles florales comestibles."
        ),
        Producto(
            id = 18,
            nombre = "Pastel de Cumpleaños",
            precio = "$60.000 CLP",
            imagen = R.drawable.pastel_cumpleanos,
            categoria = Categoria.TORTA_ESPECIAL,
            descripcion = "Personalizable con mensaje, color y relleno al gusto."
        ),
        Producto(
            id = 19,
            nombre = "Pastel Empresarial",
            precio = "$70.000 CLP",
            imagen = R.drawable.pastel_empresa,
            categoria = Categoria.TORTA_ESPECIAL,
            descripcion = "Decorado con el logo de la empresa y colores institucionales."
        ),
        Producto(
            id = 20,
            nombre = "Tarta Santiago",
            precio = "$4.500 CLP",
            imagen = R.drawable.tarta_santiago,
            categoria = Categoria.POSTRE_INDIVIDUAL,
            descripcion = "Tradicional tarta gallega de almendras, sin gluten y sin lácteos."
        )
    )

    init {
        scope.launch {
            // Siembra inicial si está vacío
            if (dao.contar() == 0) {
                dao.insertarProductos(initialCatalog.map { it.toEntity() })
            }
            // Observa cambios en Room y los expone como modelos de UI
            dao.obtenerTodos().collect { entities ->
                _productos.value = entities.map { it.toModel() }
            }
        }
    }

    fun getProductoById(id: Int): Producto? = _productos.value.find { it.id == id }

    fun filtrarPorCategoria(cat: Categoria?): List<Producto> =
        if (cat == null) _productos.value else _productos.value.filter { it.categoria == cat }

    // Mantengo esta API por compatibilidad: reemplaza catálogo por completo
    fun actualizarCatalogo(nuevo: List<Producto>) {
        scope.launch {
            dao.eliminarTodos()
            dao.insertarProductos(nuevo.map { it.toEntity() })
        }
    }

    // === Mapeos ===
    private fun ProductoEntity.toModel(): Producto =
        Producto(
            id = id,
            nombre = nombre,
            precio = precio,
            imagen = imagen,
            categoria = Categoria.valueOf(categoria),
            descripcion = descripcion
        )

    private fun Producto.toEntity(): ProductoEntity =
        ProductoEntity(
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
