package com.grupo3.misterpastel.repository.remote

/**
 * Fuente de datos remotos para los productos.
 * Encapsula las llamadas a Retrofit.
 */
class ProductoRemoteDataSource {

    private val api = RetrofitInstance.api

    /**
     * Ejecuta la llamada a la API.
     * ⚠ Esta función ya es 'suspend', así que NO necesita withContext aquí.
     * El ViewModel/Repository controla el dispatcher.
     */
    suspend fun obtenerProductos() = api.getProductos()
}
