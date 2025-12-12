package com.grupo3.misterpastel.viewmodel

import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.repository.CarritoRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CarritoViewModelTest {

    private lateinit var viewModel: CarritoViewModel

    private fun crearProductoDummy(id: Int, precio: String = "1000"): Producto {
        return Producto(
            id = id,
            nombre = "Producto $id",
            precio = precio,
            imagen = "img$id",
            categoria = Categoria.TORTA_CIRCULAR,
            descripcion = "desc"
        )
    }

    @Before
    fun setup() {
        CarritoRepository.vaciar()
        viewModel = CarritoViewModel()
    }

    // 1.- agregar() agrega un producto correctamente
    @Test
    fun agregar_agrega_producto_correctamente() = runBlocking {
        val producto = crearProductoDummy(1)

        viewModel.agregar(producto)

        val items = viewModel.items.value
        assertEquals(1, items.size)
        assertEquals(1, items[0].producto.id)
        assertEquals(1, items[0].cantidad)
    }

    // 2) agregar() aumenta la cantidad si el producto ya existe
    @Test
    fun agregar_aumenta_cantidad_si_ya_existe() = runBlocking {
        val producto = crearProductoDummy(1)

        viewModel.agregar(producto, 1)
        viewModel.agregar(producto, 1)

        val items = viewModel.items.value
        assertEquals(1, items.size)
        assertEquals(2, items[0].cantidad)
    }

    // 3.- actualizarCantidad() cambiia correctamente la cantidad
    @Test
    fun actualizarCantidad_cambia_cantidad_correctamente() = runBlocking {
        val producto = crearProductoDummy(1)

        viewModel.agregar(producto, 1)
        viewModel.actualizarCantidad(1, 5)

        val items = viewModel.items.value
        assertEquals(1, items.size)
        assertEquals(5, items[0].cantidad)
    }
}

