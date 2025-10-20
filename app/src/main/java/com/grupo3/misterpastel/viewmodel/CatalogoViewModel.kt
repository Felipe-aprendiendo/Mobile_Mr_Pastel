package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.repository.ProductoRepository
import com.grupo3.misterpastel.viewmodel.state.CatalogoState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Observa el catálogo del repositorio y aplica filtro por categoría.
 */
class CatalogoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoState())
    val uiState: StateFlow<CatalogoState> = _uiState

    init {
        viewModelScope.launch {
            ProductoRepository.productos.collectLatest { lista ->
                _uiState.value = _uiState.value.copy(productos = listaFiltrada(lista, _uiState.value.categoriaSeleccionada))
            }
        }
    }

    fun setCategoria(cat: Categoria?) {
        val productosActuales = ProductoRepository.productos.value
        _uiState.value = _uiState.value.copy(
            categoriaSeleccionada = cat,
            productos = listaFiltrada(productosActuales, cat)
        )
    }

    fun getProductoById(id: Int) = ProductoRepository.getProductoById(id)

    private fun listaFiltrada(lista: List<com.grupo3.misterpastel.model.Producto>, cat: Categoria?): List<com.grupo3.misterpastel.model.Producto> {
        return if (cat == null) lista else lista.filter { it.categoria == cat }
    }
}
