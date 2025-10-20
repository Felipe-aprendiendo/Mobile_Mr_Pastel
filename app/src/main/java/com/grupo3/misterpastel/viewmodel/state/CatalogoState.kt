package com.grupo3.misterpastel.viewmodel.state

import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.model.Producto

/**
 * Estado del catálogo, con filtro por categoría.
 */
data class CatalogoState(
    val productos: List<Producto> = emptyList(),
    val categoriaSeleccionada: Categoria? = null
)
