package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.repository.CarritoRepository
import com.grupo3.misterpastel.repository.DescuentoAplicado
import kotlinx.coroutines.flow.StateFlow

class CarritoViewModel : ViewModel() {

    val items = CarritoRepository.items
    val coupon: StateFlow<String?> = CarritoRepository.coupon

    // Datos “ligeros” del usuario (para calcular descuentos en UI)
    // AHORA como estado de Compose para gatillar recomposición
    var edadUsuario by mutableStateOf<Int?>(null)
        private set

    var emailUsuario by mutableStateOf<String?>(null)
        private set

    fun actualizarDatosUsuario(edad: Int?, email: String?) {
        edadUsuario = edad
        emailUsuario = email
    }

    fun agregar(producto: Producto, cantidad: Int = 1) =
        CarritoRepository.agregarProducto(producto, cantidad)

    fun actualizarCantidad(idProducto: Int, cantidad: Int) =
        CarritoRepository.actualizarCantidad(idProducto, cantidad)

    fun eliminar(idProducto: Int) =
        CarritoRepository.eliminarProducto(idProducto)

    fun vaciar() = CarritoRepository.vaciar()

    fun setCupon(codigo: String?) = CarritoRepository.setCupon(codigo)

    fun totalBruto(): Double = CarritoRepository.totalBruto()

    fun totalConDescuento(): Double =
        CarritoRepository.totalConDescuento(edadUsuario, emailUsuario, coupon.value)

    // Función expuesta para obtener la lista de descuentos
    fun obtenerDescuentosAplicados(): List<DescuentoAplicado> =
        CarritoRepository.obtenerDescuentosAplicados(edadUsuario, emailUsuario, coupon.value)

    fun pagar(
        usuarioId: String,
        nombre: String,
        email: String,
        edad: Int?,
        pedidoViewModel: PedidoViewModel
    ) {
        val comprobante = CarritoRepository.confirmarPedidoYGuardarComprobante(
            usuarioNombre = nombre,
            usuarioEmail = email,
            edadUsuario = edad
        )

        // Guarda el pedido en Room
        pedidoViewModel.registrarPedidoDesdeComprobante(usuarioId, comprobante)
    }

    fun confirmarPedidoYGuardarComprobante(
        usuarioNombre: String,
        usuarioEmail: String,
        edadUsuario: Int?,
        metodoPago: String = "Tarjeta de crédito"
    ): com.grupo3.misterpastel.model.ComprobantePago {
        return CarritoRepository.confirmarPedidoYGuardarComprobante(
            usuarioNombre = usuarioNombre,
            usuarioEmail = usuarioEmail,
            edadUsuario = edadUsuario,
            metodoPago = metodoPago
        )
    }
}
