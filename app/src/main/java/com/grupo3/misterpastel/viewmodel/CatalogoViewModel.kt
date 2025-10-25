package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.model.Producto
import kotlinx.coroutines.launch

class CatalogoViewModel : ViewModel() {

    // TODO: Inyectar ProductoRepository cuando esté creado
    // private val productoRepository = ProductoRepository()

    private val _productos = MutableLiveData<List<Producto>>()
    val productos: LiveData<List<Producto>> = _productos

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            // TODO: Reemplazar con la llamada al repositorio: _productos.value = productoRepository.obtenerProductos()
            _productos.value = listOf(
                Producto(
                    id = 1,
                    nombre = "Torta de Chocolate",
                    precio = "$20.000 CLP",
                    imagen = R.drawable.ic_launcher_background, // Placeholder
                    categoria = Categoria.TORTA_CIRCULAR,
                    descripcion = "Deliciosa torta de chocolate con cobertura de ganache."
                ),
                Producto(
                    id = 2,
                    nombre = "Cheesecake de Fresa",
                    precio = "$15.000 CLP",
                    imagen = R.drawable.ic_launcher_background, // Placeholder
                    categoria = Categoria.POSTRE_INDIVIDUAL,
                    descripcion = "Cremoso cheesecake con una capa de mermelada de fresa casera."
                ),
                Producto(
                    id = 3,
                    nombre = "Torta Vegana de Zanahoria",
                    precio = "$25.000 CLP",
                    imagen = R.drawable.ic_launcher_background, // Placeholder
                    categoria = Categoria.PRODUCTO_VEGANO,
                    descripcion = "Torta de zanahoria vegana con frosting de queso crema de anacardos."
                )
            )
        }
    }
}