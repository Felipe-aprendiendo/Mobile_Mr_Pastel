package com.grupo3.misterpastel.repository

import android.content.Context
import com.grupo3.misterpastel.model.CarritoItem
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.model.subtotal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object CarritoRepository {

    private val _items = MutableStateFlow<List<CarritoItem>>(emptyList())
    val items: StateFlow<List<CarritoItem>> = _items

    private val _coupon = MutableStateFlow<String?>(null)
    val coupon: StateFlow<String?> = _coupon

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
            if (cantidad <= 0) {
                list.filterNot { it.producto.id == productoId }
            } else {
                list.toMutableList().apply {
                    this[idx] = this[idx].copy(cantidad = cantidad)
                }
            }
        }
    }

    fun eliminarProducto(productoId: Int) {
        _items.update { list -> list.filterNot { it.producto.id == productoId } }
    }

    fun totalBruto(): Double = _items.value.sumOf { it.subtotal() }

    /**
     * Política de descuentos:
     * 1) 100% si el correo termina en @duocuc.cl
     * 2) 50% si edad >= 50
     * 3) 10% si cupón FELICES50
     */
    fun totalConDescuento(
        edadUsuario: Int?,
        emailUsuario: String?,
        cupon: String? = _coupon.value
    ): Double {
        val bruto = totalBruto()
        if (bruto <= 0.0) return 0.0

        // 100% DUOC
        if (!emailUsuario.isNullOrBlank() && emailUsuario.endsWith("@duocuc.cl", ignoreCase = true)) {
            return 0.0
        }
        // 50% edad
        if ((edadUsuario ?: 0) >= 50) {
            return bruto * 0.5
        }
        // 10% cupón
        if (cupon.equals("FELICES50", ignoreCase = true)) {
            return bruto * 0.9
        }
        return bruto
    }

    // ---- Confirmación de pedido (opcional) ----
    fun confirmarPedido(context: Context, onSuccess: () -> Unit = {}) {
        // Si usas un helper de notificaciones, descomenta/ajusta:
        // NotificationHelper.showSimpleNotification(
        //    context = context,
        //    title = "Pedido confirmado",
        //    message = "¡Gracias por tu compra! Estamos preparando tu pedido 🍰"
        // )
        vaciar()
        onSuccess()
    }
}
