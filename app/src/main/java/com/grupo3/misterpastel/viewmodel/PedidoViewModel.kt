package com.grupo3.misterpastel.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.repository.CarritoRepository
import com.grupo3.misterpastel.repository.EstadoPedido
import com.grupo3.misterpastel.viewmodel.state.PedidoState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Simula el flujo de un pedido: PENDIENTE -> PREPARANDO -> ENTREGADO,
 * mostrando una notificación local al confirmar.
 */
class PedidoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PedidoState())
    val uiState: StateFlow<PedidoState> = _uiState

    fun confirmarPedido(context: Context) {
        // Dispara notificación y vacía carrito
        CarritoRepository.confirmarPedido(context) {
            _uiState.value = PedidoState(estado = EstadoPedido.PENDIENTE, mensaje = "Pedido confirmado")
        }

        // Simula progreso del pedido
        viewModelScope.launch {
            delay(1200)
            _uiState.value = _uiState.value.copy(estado = EstadoPedido.PREPARANDO, mensaje = "Estamos preparando tu pedido")
            delay(1800)
            _uiState.value = _uiState.value.copy(estado = EstadoPedido.ENTREGADO, mensaje = "Pedido entregado 🎉")
        }
    }
}
