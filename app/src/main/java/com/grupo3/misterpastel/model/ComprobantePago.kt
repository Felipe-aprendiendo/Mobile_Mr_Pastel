package com.grupo3.misterpastel.model


data class ComprobantePago(
    val idComprobante: String,
    val usuarioNombre: String,
    val usuarioEmail: String,
    val fechaHoraMillis: Long,
    val items: List<CarritoItem>,
    val subtotal: Double,
    val descuentoEtiqueta: String,
    val descuentoMonto: Double,
    val totalFinal: Double,
    val metodoPago: String,
    val estado: String = "Pagado"
)
