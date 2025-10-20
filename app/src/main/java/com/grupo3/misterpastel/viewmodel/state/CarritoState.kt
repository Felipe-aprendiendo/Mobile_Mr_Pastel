package com.grupo3.misterpastel.viewmodel.state

import com.grupo3.misterpastel.repository.CarritoItem

/**
 * Estado del carrito:
 * - items actuales
 * - cupón aplicado
 * - totales bruto y con descuento
 */
data class CarritoState(
    val items: List<CarritoItem> = emptyList(),
    val cupon: String? = null,
    val totalBruto: Double = 0.0,
    val totalConDescuento: Double = 0.0
)
