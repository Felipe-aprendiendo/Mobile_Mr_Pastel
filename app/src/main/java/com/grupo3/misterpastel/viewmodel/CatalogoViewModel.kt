package com.grupo3.misterpastel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.repository.ProductoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * MOD:
 * - Cambiado a AndroidViewModel para acceder a Application y obtener el Repository Singleton con Room.
 * - Sin cambios en la API pública hacia la UI.
 */
class CatalogoViewModel(application: Application) : AndroidViewModel(application) {

    private val productoRepository = ProductoRepository.getInstance(application) // MOD: ahora via Singleton con Room

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
