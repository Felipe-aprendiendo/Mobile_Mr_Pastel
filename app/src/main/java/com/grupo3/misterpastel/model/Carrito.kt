package com.grupo3.misterpastel.model

data class Carrito(
    val items: List<com.grupo3.misterpastel.repository.CarritoItem> = emptyList(),
    val coupon: String? = null
)