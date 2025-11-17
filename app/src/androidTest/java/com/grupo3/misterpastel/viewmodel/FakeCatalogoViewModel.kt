package com.grupo3.misterpastel.viewmodel


import androidx.lifecycle.ViewModel
import com.grupo3.misterpastel.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeCatalogoViewModel(productosMock: List<Producto>) : ViewModel() {

    private val _productos = MutableStateFlow(productosMock)
    val productos: StateFlow<List<Producto>> = _productos

    fun cargarDesdeApi() { /* no-op */ }
}
