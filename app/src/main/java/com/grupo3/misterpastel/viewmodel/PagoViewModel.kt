package com.grupo3.misterpastel.viewmodel


import androidx.lifecycle.ViewModel
import com.grupo3.misterpastel.model.ComprobantePago
import com.grupo3.misterpastel.repository.CarritoRepository
import kotlinx.coroutines.flow.StateFlow

class PagoViewModel : ViewModel() {

    val comprobante: StateFlow<ComprobantePago?> = CarritoRepository.ultimoComprobante

    fun iniciarPago(nombre: String, email: String, edad: Int?) {
        CarritoRepository.confirmarPedidoYGuardarComprobante(
            usuarioNombre = nombre,
            usuarioEmail = email,
            edadUsuario = edad
        )
    }

    fun limpiarComprobante() = CarritoRepository.limpiarComprobante()
}
