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

    fun getCantidad(producto: Producto): Int {
        return _carritoItems.value?.find { it.producto.id == producto.id }?.cantidad ?: 0
    }

    fun setCantidad(producto: Producto, cantidad: Int) {
        val listaActual = _carritoItems.value?.toMutableList() ?: mutableListOf()
        val itemExistente = listaActual.find { it.producto.id == producto.id }

        if (itemExistente != null) {
            if (cantidad > 0) {
                itemExistente.cantidad = cantidad
            } else {
                listaActual.remove(itemExistente)
            }
        } else {
            if (cantidad > 0) {
                listaActual.add(CarritoItem(producto = producto, cantidad = cantidad))
            }
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

    fun vaciarCarrito() {
        _carritoItems.value = emptyList()
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