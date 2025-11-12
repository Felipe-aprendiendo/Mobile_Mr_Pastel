package com.grupo3.misterpastel.repository.remote


import com.grupo3.misterpastel.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Fuente de datos remotos para productos.
 * Aísla las llamadas de red y facilita el testeo.
 */
class ProductoRemoteDataSource {

    private val api = RetrofitInstance.api

    /**
     * Obtiene los productos desde la API utilizando corrutinas.
     * Se ejecuta en el contexto IO.
     */
    suspend fun obtenerProductos(): Response<List<Producto>> = withContext(Dispatchers.IO) {
        api.getProductos()
    }
}
