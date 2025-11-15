package com.grupo3.misterpastel.repository.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ProductoRemoteDataSource {

    private val api = RetrofitInstance.api

    suspend fun obtenerProductos(): Response<ProductosResponse> =
        withContext(Dispatchers.IO) {
            api.getProductos()  // ⬅️ ESTE ERA EL PROBLEMA
        }
}
