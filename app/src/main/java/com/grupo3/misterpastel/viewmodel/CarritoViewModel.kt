package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.repository.CarritoRepository
import com.grupo3.misterpastel.repository.CarritoItem
import com.grupo3.misterpastel.viewmodel.state.CarritoState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Orquesta items/cupón y cálculos de totales.
 */
class CarritoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CarritoState())
    val uiState: StateFlow<CarritoState> = _uiState

    init {
        // Combina flows de items y cupón para recalcular totales
        viewModelScope.launch {
            CarritoRepository.items
                .combine(CarritoRepository.coupon) { items, cupon ->
                    val totalBruto = CarritoRepository.totalBruto()
                    // Para el descuento, simulamos un usuario genérico (sin sesión) → edad=null, email=null
                    val totalDesc = CarritoRepository.totalConDescuento(
                        edadUsuario = null,
                        emailUsuario = null,
                        cupon = cupon
                    )
                    CarritoState(
                        items = items,
                        cupon = cupon,
                        totalBruto = totalBruto,
                        totalConDescuento = totalDesc
                    )
                }
                .collect { _uiState.value = it }
        }
    }

    fun setCupon(codigo: String?) = CarritoRepository.setCupon(codigo)

    fun agregar(item: CarritoItem) = CarritoRepository.agregarProducto(item.producto, item.cantidad)

    fun agregar(producto: com.grupo3.misterpastel.model.Producto, cantidad: Int = 1) =
        CarritoRepository.agregarProducto(producto, cantidad)

    fun actualizarCantidad(productoId: Int, cantidad: Int) =
        CarritoRepository.actualizarCantidad(productoId, cantidad)

    fun eliminar(productoId: Int) = CarritoRepository.eliminarProducto(productoId)

    fun vaciar() = CarritoRepository.vaciar()
}
