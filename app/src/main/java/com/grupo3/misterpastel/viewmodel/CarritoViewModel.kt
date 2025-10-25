package com.grupo3.misterpastel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grupo3.misterpastel.model.CarritoItem
import com.grupo3.misterpastel.model.Producto

class CarritoViewModel : ViewModel() {

    private val _carritoItems = MutableLiveData<List<CarritoItem>>(emptyList())
    val carritoItems: LiveData<List<CarritoItem>> = _carritoItems

    private val _total = MutableLiveData<Double>(0.0)
    val total: LiveData<Double> = _total

    fun agregarProducto(producto: Producto) {
        val listaActual = _carritoItems.value?.toMutableList() ?: mutableListOf()
        val itemExistente = listaActual.find { it.producto.id == producto.id }

        if (itemExistente != null) {
            itemExistente.cantidad++
        } else {
            listaActual.add(CarritoItem(producto = producto, cantidad = 1))
        }

        _carritoItems.value = listaActual
        actualizarTotal()
    }

    fun eliminarProducto(carritoItem: CarritoItem) {
        val listaActual = _carritoItems.value?.toMutableList() ?: mutableListOf()
        listaActual.remove(carritoItem)
        _carritoItems.value = listaActual
        actualizarTotal()
    }

    fun modificarCantidad(carritoItem: CarritoItem, nuevaCantidad: Int) {
        val listaActual = _carritoItems.value?.toMutableList() ?: mutableListOf()
        val itemEnLista = listaActual.find { it.producto.id == carritoItem.producto.id }

        if (itemEnLista != null) {
            if (nuevaCantidad > 0) {
                itemEnLista.cantidad = nuevaCantidad
            } else {
                listaActual.remove(itemEnLista)
            }
        }

        _carritoItems.value = listaActual
        actualizarTotal()
    }

    private fun actualizarTotal() {
        var nuevoTotal = 0.0
        _carritoItems.value?.forEach { item ->
            val precioNumerico = item.producto.precio
                .replace("$", "")
                .replace(".", "")
                .replace(" CLP", "")
                .trim()
                .toDoubleOrNull() ?: 0.0
            nuevoTotal += precioNumerico * item.cantidad
        }
        _total.value = nuevoTotal
    }
}