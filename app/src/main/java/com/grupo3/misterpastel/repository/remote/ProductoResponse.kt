package com.grupo3.misterpastel.repository.remote

import com.grupo3.misterpastel.model.Producto

data class ProductosResponse(
    val items: List<Producto>
)
