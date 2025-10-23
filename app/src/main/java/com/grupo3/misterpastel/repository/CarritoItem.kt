package com.grupo3.misterpastel.repository

import com.grupo3.misterpastel.model.Producto

/**
 * Tipo auxiliar que usa tu data class Carrito (en model.Carrito).
 */
data class CarritoItem(
    val producto: Producto,
    val cantidad: Int
) {
    val subtotal: Double
        get() = producto.precio
            .replace("[^\\d.,]".toRegex(), "")
            .replace(".", "")
            .replace(",", ".")
            .toDoubleOrNull()
            ?.times(cantidad) ?: 0.0
}
