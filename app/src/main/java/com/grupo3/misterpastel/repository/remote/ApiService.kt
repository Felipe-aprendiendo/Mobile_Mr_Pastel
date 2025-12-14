package com.grupo3.misterpastel.repository.remote

import com.grupo3.misterpastel.repository.remote.dto.LoginRequestDto
import com.grupo3.misterpastel.repository.remote.dto.OkResponseDto
import com.grupo3.misterpastel.repository.remote.dto.RegistroRequestDto
import com.grupo3.misterpastel.repository.remote.dto.UpdateUsuarioRequestDto
import com.grupo3.misterpastel.repository.remote.dto.UsuarioResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    /**
     * Obtiene el catálogo de productos desde APEX.
     * Endpoint: GET /api/productos/
     */
    @GET("productos/")
    suspend fun getProductos(): Response<ProductosResponse>

    /**
     * Autenticación de usuario.
     * Endpoint: POST /api/auth/login
     */
    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequestDto
    ): UsuarioResponseDto

    /**
     * Registro de usuario.
     * Endpoint: POST /api/usuarios/
     */
    @POST("usuarios/")
    suspend fun registrarUsuario(
        @Body body: RegistroRequestDto
    ): UsuarioResponseDto

    /**
     * Obtiene el perfil de un usuario por id.
     * Endpoint: GET /api/usuarios/{id}
     */
    @GET("usuarios/{id}")
    suspend fun getUsuario(
        @Path("id") id: Long
    ): UsuarioResponseDto

    /**
     * Actualiza los datos del usuario.
     * Endpoint: PUT /api/usuarios/{id}
     * Respuesta esperada: { "ok": true }
     */
    @PUT("usuarios/{id}")
    suspend fun updateUsuario(
        @Path("id") id: Long,
        @Body body: UpdateUsuarioRequestDto
    ): OkResponseDto
}
