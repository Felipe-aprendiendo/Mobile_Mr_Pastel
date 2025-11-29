package com.grupo3.misterpastel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.repository.ProductoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel del catálogo de productos.
 * Se comunica con ProductoRepository para acceder a datos locales y remotos.
 *
 * 🔹 Mantiene el estado del catálogo mediante StateFlow.
 * 🔹 Permite sincronizar los datos desde la API (Retrofit) y almacenarlos en Room.
 */
class CatalogoViewModel(application: Application) : AndroidViewModel(application) {

    private val productoRepository = ProductoRepository.getInstance(application)

    // Estado observable del catálogo
    val productos: StateFlow<List<Producto>> = productoRepository.productos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Obtiene un producto específico por su ID.
     */
    fun getProductoById(id: Int): Producto? {
        return productoRepository.getProductoById(id)
    }

    /**
     * Descarga los productos desde la API remota y sincroniza Room.
     * Se puede llamar desde una pantalla (ej: HomeSesionIniciada) o al iniciar la app.
     */
    fun cargarDesdeApi() {
        viewModelScope.launch {
            val exito = productoRepository.sincronizarDesdeApi()
            if (exito) {
                println("✅ Catálogo sincronizado correctamente desde la API remota.")
            } else {
                println("⚠️ No se pudo sincronizar el catálogo (problema de red o servidor).")
            }
        }
    }
}
