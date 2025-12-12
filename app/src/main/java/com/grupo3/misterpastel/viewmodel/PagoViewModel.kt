package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import com.grupo3.misterpastel.model.ComprobantePago
import com.grupo3.misterpastel.repository.CarritoRepository
import com.grupo3.misterpastel.repository.DescuentoAplicado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PagoViewModel : ViewModel() {

    // Comprobante actual (se usa para mostrar la boleta después del pago)
    private val _comprobante = MutableStateFlow<ComprobantePago?>(null)
    val comprobante: StateFlow<ComprobantePago?> = _comprobante

    // Almacena la lista de descuentos aplicados para el desglose en el comprobante final
    private val _descuentosAplicados = MutableStateFlow<List<DescuentoAplicado>>(emptyList())
    val descuentosAplicados: StateFlow<List<DescuentoAplicado>> = _descuentosAplicados

    /**
     * Inicia el pago generando el comprobante desde el CarritoRepository.
     */
    fun iniciarPago(nombre: String, email: String, edad: Int?) {
        val nuevoComprobante = CarritoRepository.confirmarPedidoYGuardarComprobante(
            usuarioNombre = nombre,
            usuarioEmail = email,
            edadUsuario = edad
        )
        _comprobante.value = nuevoComprobante
    }

    // Guarda manualmente un comprobante generado desde CarritoViewModel.
    fun setComprobante(comprobante: ComprobantePago) {
        _comprobante.value = comprobante
    }

    // Función para establecer el comprobante y la lista de descuentos
    fun setComprobanteYDescuentos(
        comprobante: ComprobantePago,
        descuentos: List<DescuentoAplicado>
    ) {
        _comprobante.value = comprobante
        _descuentosAplicados.value = descuentos
    }

    fun limpiarComprobante() {
        CarritoRepository.limpiarComprobante()
        _comprobante.value = null
        _descuentosAplicados.value = emptyList()
    }
}
