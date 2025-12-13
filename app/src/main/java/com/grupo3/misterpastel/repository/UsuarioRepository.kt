package com.grupo3.misterpastel.repository

import android.content.Context
import com.grupo3.misterpastel.model.Usuario
import com.grupo3.misterpastel.repository.local.AppDatabase
import com.grupo3.misterpastel.repository.local.UsuarioEntity
import com.grupo3.misterpastel.repository.remote.ApiService
import com.grupo3.misterpastel.repository.remote.dto.LoginRequestDto
import com.grupo3.misterpastel.repository.remote.dto.RegistroRequestDto
import com.grupo3.misterpastel.repository.remote.dto.UpdateUsuarioRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Repositorio central de usuarios.
 *
 * Fuente de verdad: APEX (ORDS).
 * Room se usa solo como cache de sesión local.
 */
class UsuarioRepository private constructor(
    private val context: Context,
    private val api: ApiService
) {

    private val usuarioDao = AppDatabase.getDatabase(context).usuarioDao()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    companion object {
        @Volatile
        private var INSTANCE: UsuarioRepository? = null

        fun getInstance(context: Context, api: ApiService): UsuarioRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UsuarioRepository(context, api)
                INSTANCE = instance
                instance
            }
        }
    }

    // ==============================
    // REGISTRO DE USUARIO (APEX)
    // ==============================
    suspend fun registrar(
        nombre: String,
        email: String,
        password: String,
        edad: Int?,
        fechaNacimiento: String?,
        direccion: String?,
        telefono: String?,
        fotoUrl: String?
    ): Result<Usuario> = withContext(Dispatchers.IO) {
        try {
            val dto = RegistroRequestDto(
                nombre = nombre,
                email = email,
                edad = edad,
                fechaNacimiento = fechaNacimiento,
                direccion = direccion,
                telefono = telefono,
                password = password,
                fotoUrl = fotoUrl
            )

            val resp = api.registrarUsuario(dto)

            val usuario = Usuario(
                id = resp.id.toString(),
                nombre = resp.nombre,
                email = resp.email,
                edad = resp.edad ?: 0,
                fechaNacimiento = fechaNacimiento ?: "",
                direccion = direccion ?: "",
                telefono = telefono ?: "",
                password = "",
                fotoUrl = fotoUrl
            )

            guardarSesionLocal(usuario)
            _usuarioActual.value = usuario

            Result.success(usuario)
        } catch (e: HttpException) {
            when (e.code()) {
                409 -> Result.failure(IllegalArgumentException("El correo ya está registrado"))
                else -> Result.failure(IllegalArgumentException("Error del servidor"))
            }
        } catch (e: IOException) {
            Result.failure(IllegalArgumentException("Error de red"))
        }
    }

    // ==============================
    // LOGIN DE USUARIO (APEX)
    // ==============================
    suspend fun login(email: String, password: String): Result<Usuario> =
        withContext(Dispatchers.IO) {
            try {
                val resp = api.login(LoginRequestDto(email, password))

                val usuario = Usuario(
                    id = resp.id.toString(),
                    nombre = resp.nombre,
                    email = resp.email,
                    edad = resp.edad ?: 0,
                    fechaNacimiento = "",
                    direccion = "",
                    telefono = "",
                    password = "",
                    fotoUrl = null
                )

                guardarSesionLocal(usuario)
                _usuarioActual.value = usuario

                Result.success(usuario)
            } catch (e: HttpException) {
                when (e.code()) {
                    404 -> Result.failure(IllegalArgumentException("Correo no registrado"))
                    401 -> Result.failure(IllegalArgumentException("Contraseña incorrecta"))
                    else -> Result.failure(IllegalArgumentException("Error del servidor"))
                }
            } catch (e: IOException) {
                Result.failure(IllegalArgumentException("Error de red"))
            }
        }

    // ==============================
    // CERRAR SESIÓN
    // ==============================
    suspend fun logout() = withContext(Dispatchers.IO) {
        usuarioDao.eliminarTodos()
        _usuarioActual.value = null
    }

    // ==============================
    // ACTUALIZAR PERFIL (APEX)
    // ==============================
    suspend fun actualizarPerfil(usuario: Usuario): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val dto = UpdateUsuarioRequestDto(
                    nombre = usuario.nombre,
                    edad = usuario.edad,
                    direccion = usuario.direccion,
                    telefono = usuario.telefono,
                    fotoUrl = usuario.fotoUrl
                )

                api.updateUsuario(usuario.id.toLong(), dto)

                guardarSesionLocal(usuario)
                _usuarioActual.value = usuario

                Result.success(Unit)
            } catch (e: HttpException) {
                when (e.code()) {
                    404 -> Result.failure(IllegalArgumentException("Usuario no encontrado"))
                    else -> Result.failure(IllegalArgumentException("Error del servidor"))
                }
            } catch (e: IOException) {
                Result.failure(IllegalArgumentException("Error de red"))
            }
        }

    // ==============================
    // SESIÓN LOCAL (Room)
    // ==============================
    private suspend fun guardarSesionLocal(usuario: Usuario) {
        usuarioDao.eliminarTodos()

        val entity = UsuarioEntity(
            id = usuario.id,
            nombre = usuario.nombre,
            email = usuario.email,
            edad = usuario.edad,
            fechaNacimiento = usuario.fechaNacimiento,
            direccion = usuario.direccion,
            telefono = usuario.telefono,
            passwordHash = "",
            fotoUrl = usuario.fotoUrl
        )

        usuarioDao.insertarUsuario(entity)
    }

    suspend fun restaurarSesionLocal() = withContext(Dispatchers.IO) {
        val entity = usuarioDao.obtenerSesion()
        _usuarioActual.value = entity?.toDomain()
    }

    fun observarSesionLocal() = usuarioDao.observarSesion()



}


/* ==============================
   MAPPER: Room -> Dominio
   ============================== */

private fun UsuarioEntity.toDomain(): Usuario {
    return Usuario(
        id = id,
        nombre = nombre,
        email = email,
        edad = edad,
        fechaNacimiento = fechaNacimiento,
        direccion = direccion,
        telefono = telefono,
        password = "",
        fotoUrl = fotoUrl
    )
}
