package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.repository.CarritoRepository
import kotlinx.coroutines.flow.StateFlow

class CarritoViewModel : ViewModel() {

    val items = CarritoRepository.items
    val coupon: StateFlow<String?> = CarritoRepository.coupon

    // Datos “ligeros” del usuario (para calcular descuentos en UI)
    var edadUsuario: Int? = null
    var emailUsuario: String? = null

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

    fun pagar(usuarioId: String, nombre: String, email: String, edad: Int?, pedidoViewModel: PedidoViewModel) {
        val comprobante = CarritoRepository.confirmarPedidoYGuardarComprobante(
            usuarioNombre = nombre,
            usuarioEmail = email,
            edadUsuario = edad
        )

        // Guarda el pedido en Room
        pedidoViewModel.registrarPedidoDesdeComprobante(usuarioId, comprobante)
    }



}
