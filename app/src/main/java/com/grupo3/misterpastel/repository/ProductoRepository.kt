package com.grupo3.misterpastel.repository

import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repositorio de catálogo. Expone un StateFlow para que los ViewModels
 * puedan observar cambios (en el futuro si cargan desde red/JSON).
 */
object ProductoRepository {

    // Catálogo inicial (puedes moverlo a JSON local más adelante)
    private val initialCatalog = listOf(
        Producto(
            id = 1,
            nombre = "Torta Cuadrada de Chocolate",
            precio = "$45.000 CLP",
            imagen = R.drawable.torta_chocolate,
            categoria = Categoria.TORTA_CUADRADA,
            descripcion = "Bizcocho húmedo de cacao con ganache y cobertura de chocolate amargo."
        ),
        Producto(
            id = 2,
            nombre = "Torta Circular de Frutas",
            precio = "$50.000 CLP",
            imagen = R.drawable.torta_frutas,
            categoria = Categoria.TORTA_CIRCULAR,
            descripcion = "Esponjosa con crema pastelera y frutas frescas de temporada."
        ),
        Producto(
            id = 3,
            nombre = "Mousse de Chocolate",
            precio = "$5.000 CLP",
            imagen = R.drawable.mousse_chocolate,
            categoria = Categoria.POSTRE_INDIVIDUAL,
            descripcion = "Textura ligera con cacao premium, decorado con virutas de chocolate."
        ),
        Producto(
            id = 4,
            nombre = "Torta Especial Cumpleaños",
            precio = "$55.000 CLP",
            imagen = R.drawable.torta_cumple,
            categoria = Categoria.TORTA_ESPECIAL,
            descripcion = "Diseño personalizado con crema/fondant. Sabor vainilla o chocolate."
        ),
        Producto(
            id = 5,
            nombre = "Empanada de Manzana",
            precio = "$3.000 CLP",
            imagen = R.drawable.empanada_manzana,
            categoria = Categoria.PASTELERIA_TRADICIONAL,
            descripcion = "Masa crujiente con compota de manzana y canela."
        )
    )

    private val _productos = MutableStateFlow(initialCatalog)
    val productos: StateFlow<List<Producto>> = _productos

    fun getProductoById(id: Int): Producto? = _productos.value.find { it.id == id }

    fun filtrarPorCategoria(cat: Categoria?): List<Producto> =
        if (cat == null) _productos.value else _productos.value.filter { it.categoria == cat }

    // Ejemplo de actualización futura (cargar JSON o remoto)
    fun actualizarCatalogo(nuevo: List<Producto>) {
        _productos.value = nuevo
    }
}
