package com.grupo3.misterpastel.repository.remote

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("productos/")
    suspend fun getProductos(): Response<ProductosResponse>
}

