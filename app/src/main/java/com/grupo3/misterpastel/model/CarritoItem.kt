package com.grupo3.misterpastel.model

data class CarritoItem(
    val producto: Producto,
    val cantidad: Int
)

// Extensión para calcular subtotal (precio * cantidad)
fun CarritoItem.subtotal(): Double = producto.precio
    .replace("[^\\d.,]".toRegex(), "")
    .replace(".", "")
    .replace(",", ".")
    .toDoubleOrNull()
    ?.times(cantidad) ?: 0.0
