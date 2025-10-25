package com.grupo3.misterpastel.repository

import android.content.Context
import com.grupo3.misterpastel.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Gestión del carrito en memoria + cálculo de totales y descuentos.
 * - Descuento 50%: edad >= 50
 * - Descuento 10%: código "FELICES50"
 * - Descuento 100%: correo DUOC (termina en @duocuc.cl)
 */
object CarritoRepository {

    private val _items = MutableStateFlow<List<CarritoItem>>(emptyList())
    val items: StateFlow<List<CarritoItem>> = _items

    private val _coupon = MutableStateFlow<String?>(null)
    val coupon: StateFlow<String?> = _coupon

    fun vaciar() {
        _items.value = emptyList()
        _coupon.value = null
    }

    fun setCupon(codigo: String?) {
        _coupon.value = codigo?.takeIf { it.isNotBlank() }
    }

    fun agregarProducto(producto: Producto, cantidad: Int = 1) {
        if (cantidad <= 0) return
        val lista = _items.value.toMutableList()
        val idx = lista.indexOfFirst { it.producto.id == producto.id }
        if (idx >= 0) {
            val existente = lista[idx]
            lista[idx] = existente.copy(cantidad = existente.cantidad + cantidad)
        } else {
            lista.add(CarritoItem(producto, cantidad))
        }
        _items.value = lista
    }

    fun actualizarCantidad(productoId: Int, cantidad: Int) {
        val lista = _items.value.toMutableList()
        val idx = lista.indexOfFirst { it.producto.id == productoId }
        if (idx >= 0) {
            if (cantidad <= 0) {
                lista.removeAt(idx)
            } else {
                lista[idx] = lista[idx].copy(cantidad = cantidad)
            }
            _items.value = lista
        }
    }

    fun eliminarProducto(productoId: Int) {
        _items.value = _items.value.filterNot { it.producto.id == productoId }
    }

    fun totalBruto(): Double = _items.value.sumOf { it.subtotal }

    /**
     * Aplica la misma política de descuentos definida en la propuesta.
     * - Se evalúa 100% DUOC primero, luego 50% edad, luego 10% cupón.
     */
    fun totalConDescuento(
        edadUsuario: Int?,
        emailUsuario: String?,
        cupon: String? = _coupon.value
    ): Double {
        val bruto = totalBruto()
        if (bruto <= 0.0) return 0.0

        // 100% si el correo es DUOC
        if (!emailUsuario.isNullOrBlank() && emailUsuario.endsWith("@duocuc.cl", ignoreCase = true)) {
            return 0.0
        }

        // 50% si edad >= 50
        if ((edadUsuario ?: 0) >= 50) {
            return bruto * 0.5
        }

        // 10% si usa FELICES50
        if (cupon.equals("FELICES50", ignoreCase = true)) {
            return bruto * 0.9
        }

        return bruto
    }

    /**
     * Confirmar “pedido” (simulado).
     * Aquí puedes integrar persistencia real y notificaciones.
     */
    fun confirmarPedido(context: Context, onSuccess: () -> Unit = {}) {
        // Notificación local como recurso nativo
        NotificationHelper.showSimpleNotification(
            context = context,
            title = "Pedido confirmado",
            message = "¡Gracias por tu compra! Estamos preparando tu pedido 🍰"
        )
        // Vaciar carrito tras confirmar
        vaciar()
        onSuccess()
    }
}
