package com.grupo3.misterpastel.repository

import com.grupo3.misterpastel.model.CarritoItem
import com.grupo3.misterpastel.model.ComprobantePago
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.model.subtotal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

// Nueva Data Class para manejar descuentos individuales de forma acumulativa
data class DescuentoAplicado(
    val etiqueta: String,
    val porcentaje: Double, // Ejemplo: 0.10 para 10%
)

object CarritoRepository {

    private val _items = MutableStateFlow<List<CarritoItem>>(emptyList())
    val items: StateFlow<List<CarritoItem>> = _items

    private val _coupon = MutableStateFlow<String?>(null)
    val coupon: StateFlow<String?> = _coupon

    private val _ultimoComprobante = MutableStateFlow<ComprobantePago?>(null)
    val ultimoComprobante: StateFlow<ComprobantePago?> = _ultimoComprobante

    // Lógica de cupón y descuentos

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

    // Función auxiliar que obtiene todos los descuentos aplicables
    // TODOS SON ACUMULATIVOS
    fun obtenerDescuentosAplicados(
        edadUsuario: Int?,
        emailUsuario: String?,
        cupon: String?
    ): List<DescuentoAplicado> {
        val descuentos = mutableListOf<DescuentoAplicado>()

        // 1. Descuento DUOC (Estudiante)
        if (!emailUsuario.isNullOrBlank() && emailUsuario.endsWith("@duocuc.cl", true)) {
            descuentos.add(DescuentoAplicado("DUOC", 0.10)) // 10%
        }

        // 2. Descuento Mayor de 50
        if ((edadUsuario ?: 0) >= 50) {
            descuentos.add(DescuentoAplicado("+ 50 años ", 0.50)) // 50%
        }

        // 3. Descuento Cupón FELICES50
        if (cupon.equals("FELICES50", ignoreCase = true)) {
            descuentos.add(DescuentoAplicado("Cupón FELICES50", 0.10)) // 10%
        }

        return descuentos.toList()
    }

    fun totalConDescuento(edadUsuario: Int?, emailUsuario: String?, cupon: String?): Double {
        val subtotal = totalBruto()
        val descuentos = obtenerDescuentosAplicados(edadUsuario, emailUsuario, cupon)

        // Calcula el factor multiplicativo total: (1 - d1) * (1 - d2) * ...
        val factorMultiplicativo = descuentos.fold(1.0) { acc, d -> acc * (1.0 - d.porcentaje) }

        return subtotal * factorMultiplicativo
    }

    // Cálculo del resumen y generación comprob.
    data class ResumenPago(
        val subtotal: Double,
        val descuentosAplicados: List<DescuentoAplicado>, // Lista de descuentos
        val descuentoMontoTotal: Double, // Monto total de descuento
        val totalFinal: Double
    )

    private fun calcularResumenPago(
        edadUsuario: Int?,
        emailUsuario: String?,
        cupon: String?
    ): ResumenPago {
        val subtotal = totalBruto()
        val descuentos = obtenerDescuentosAplicados(edadUsuario, emailUsuario, cupon)

        val factorMultiplicativo = descuentos.fold(1.0) { acc, d -> acc * (1.0 - d.porcentaje) }
        val totalFinal = subtotal * factorMultiplicativo
        val descuentoMontoTotal = subtotal - totalFinal

        return ResumenPago(subtotal, descuentos, descuentoMontoTotal, totalFinal)
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
            // Etiqueta simplificada
            descuentoEtiqueta = "Total Descuentos",
            descuentoMonto = resumen.descuentoMontoTotal,
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
