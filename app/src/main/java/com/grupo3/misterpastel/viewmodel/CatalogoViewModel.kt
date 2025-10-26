package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.repository.ProductoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CatalogoViewModel : ViewModel() {

    private val productoRepository = ProductoRepository

    val productos: StateFlow<List<Producto>> = productoRepository.productos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getProductoById(id: Int): Producto? {
        return productoRepository.getProductoById(id)
    }
}