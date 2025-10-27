package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import com.grupo3.misterpastel.model.ComprobantePago
import com.grupo3.misterpastel.repository.CarritoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PagoViewModel : ViewModel() {

    // Comprobante actual (se usa para mostrar la boleta después del pago)
    private val _comprobante = MutableStateFlow<ComprobantePago?>(null)
    val comprobante: StateFlow<ComprobantePago?> = _comprobante

    /**IMPORTANTE
     * Inicia el pago generando el comprobante desde el CarritoRepository.
     * Esta función es opcional si el pago se maneja desde CarritoViewModel.
     */
    fun iniciarPago(nombre: String, email: String, edad: Int?) {
        val nuevoComprobante = CarritoRepository.confirmarPedidoYGuardarComprobante(
            usuarioNombre = nombre,
            usuarioEmail = email,
            edadUsuario = edad
        )
        _comprobante.value = nuevoComprobante
    }


     //Guarda manualmente un comprobante generado desde CarritoViewModel.

    fun setComprobante(comprobante: ComprobantePago) {
        _comprobante.value = comprobante
    }


    fun limpiarComprobante() {
        CarritoRepository.limpiarComprobante()
        _comprobante.value = null
    }
}
