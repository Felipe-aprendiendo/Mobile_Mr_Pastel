package com.grupo3.misterpastel.repository

import android.content.Context
import com.grupo3.misterpastel.model.CarritoItem
import com.grupo3.misterpastel.model.ComprobantePago
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.model.subtotal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

object CarritoRepository {

    private val _items = MutableStateFlow<List<CarritoItem>>(emptyList())
    val items: StateFlow<List<CarritoItem>> = _items

    private val _coupon = MutableStateFlow<String?>(null)
    val coupon: StateFlow<String?> = _coupon

    private val _ultimoComprobante = MutableStateFlow<ComprobantePago?>(null)
    val ultimoComprobante: StateFlow<ComprobantePago?> = _ultimoComprobante

    // -------------------------------
    // 🧾 Lógica de cupón y descuentos
    // -------------------------------
    fun setCupon(codigo: String?) {
        _coupon.value = codigo?.takeIf { it.isNotBlank() }
    }

    fun vaciar() {
        _items.value = emptyList()
        _coupon.value = null
    }

    fun agregarProducto(producto: Producto, cantidad: Int = 1) {
        if (cantidad <= 0) return
        _items.update { list ->
            val idx = list.indexOfFirst { it.producto.id == producto.id }
            if (idx >= 0) {
                list.toMutableList().apply {
                    this[idx] = this[idx].copy(cantidad = this[idx].cantidad + cantidad)
                }
            } else {
                list + CarritoItem(producto, cantidad)
            }
        }
    }

    fun actualizarCantidad(productoId: Int, cantidad: Int) {
        _items.update { list ->
            val idx = list.indexOfFirst { it.producto.id == productoId }
            if (idx < 0) return@update list
            if (cantidad <= 0) list.filterNot { it.producto.id == productoId }
            else list.toMutableList().apply {
                this[idx] = this[idx].copy(cantidad = cantidad)
            }
        }
    }

    fun eliminarProducto(productoId: Int) {
        _items.update { list -> list.filterNot { it.producto.id == productoId } }
    }

    fun totalBruto(): Double = _items.value.sumOf { it.subtotal() }

    fun totalConDescuento(edadUsuario: Int?, emailUsuario: String?, cupon: String?): Double {
        val subtotal = totalBruto()
        var totalFinal = subtotal

        when {
            !emailUsuario.isNullOrBlank() && emailUsuario.endsWith("@duocuc.cl", true) -> totalFinal = 0.0
            (edadUsuario ?: 0) >= 50 -> totalFinal = subtotal * 0.5
            cupon.equals("FELICES50", ignoreCase = true) -> totalFinal = subtotal * 0.9
        }

        return totalFinal
    }

    // -------------------------------------------
    // 🧮 Cálculo del resumen y generación comprob.
    // -------------------------------------------
    data class ResumenPago(
        val subtotal: Double,
        val descuentoEtiqueta: String,
        val descuentoMonto: Double,
        val totalFinal: Double
    )

    private fun calcularResumenPago(edadUsuario: Int?, emailUsuario: String?, cupon: String?): ResumenPago {
        val subtotal = totalBruto()
        var descuentoEtiqueta = "0%"
        var totalFinal = subtotal

        when {
            !emailUsuario.isNullOrBlank() && emailUsuario.endsWith("@duocuc.cl", true) -> {
                descuentoEtiqueta = "100% (DUOC)"
                totalFinal = 0.0
            }
            (edadUsuario ?: 0) >= 50 -> {
                descuentoEtiqueta = "50% (edad)"
                totalFinal = subtotal * 0.5
            }
            cupon.equals("FELICES50", ignoreCase = true) -> {
                descuentoEtiqueta = "10% (cupón)"
                totalFinal = subtotal * 0.9
            }
        }

        val descuentoMonto = subtotal - totalFinal
        return ResumenPago(subtotal, descuentoEtiqueta, descuentoMonto, totalFinal)
    }

    private fun generarIdComprobante(): String {
        val formato = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault())
        return "PED-${formato.format(Date())}"
    }

    fun confirmarPedidoYGuardarComprobante(
        usuarioNombre: String,
        usuarioEmail: String,
        edadUsuario: Int?,
        metodoPago: String = "Tarjeta de crédito"
    ): ComprobantePago {
        val snapshot = _items.value.toList()
        val resumen = calcularResumenPago(edadUsuario, usuarioEmail, _coupon.value)

        val comprobante = ComprobantePago(
            idComprobante = generarIdComprobante(),
            usuarioNombre = usuarioNombre,
            usuarioEmail = usuarioEmail,
            fechaHoraMillis = System.currentTimeMillis(),
            items = snapshot,
            subtotal = resumen.subtotal,
            descuentoEtiqueta = resumen.descuentoEtiqueta,
            descuentoMonto = resumen.descuentoMonto,
            totalFinal = resumen.totalFinal,
            metodoPago = metodoPago
        )

        _ultimoComprobante.value = comprobante
        vaciar()
        return comprobante
    }

    fun limpiarComprobante() {
        _ultimoComprobante.value = null
    }
}
