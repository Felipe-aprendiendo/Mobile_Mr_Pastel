package com.grupo3.misterpastel.repository.remote


import com.grupo3.misterpastel.model.Producto
import retrofit2.Response
import retrofit2.http.GET

/**
 * Interfaz de definición de endpoints para la API remota de MrPastel.
 * Define las operaciones HTTP disponibles.
 */
interface ApiService {

    /**
     * Obtiene la lista completa de productos desde el endpoint remoto.
     * Base URL configurada en RetrofitInstance.
     */
    @GET("productos")
    suspend fun getProductos(): Response<List<Producto>>
}
