package com.grupo3.misterpastel.repository


import android.content.Context
import com.grupo3.misterpastel.model.Usuario
import com.grupo3.misterpastel.repository.local.AppDatabase
import com.grupo3.misterpastel.repository.local.UsuarioEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

/**IMPORTANTE
 * Repositorio central de usuarios con persistencia en SQLite (Room).
 * Mantiene sincronía con los ViewModels existentes mediante StateFlow.
 */
class UsuarioRepository private constructor(private val context: Context) {

    private val usuarioDao = AppDatabase.getDatabase(context).usuarioDao()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    companion object {
        @Volatile
        private var INSTANCE: UsuarioRepository? = null

        fun getInstance(context: Context): UsuarioRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UsuarioRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }

    // ==============================
    // REGISTRO DE NUEVO USUARIO
    // ==============================
    suspend fun registrar(
        nombre: String,
        email: String,
        password: String,
        edad: Int,
        fechaNacimiento: String,
        direccion: String,
        telefono: String,
        fotoUrl: String? = null
    ): Result<Usuario> = withContext(Dispatchers.IO) {
        val existente = usuarioDao.obtenerPorEmail(email)
        if (existente != null) {
            return@withContext Result.failure(IllegalArgumentException("El correo ya está registrado"))
        }


        val nuevo = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            email = email,
            edad = edad,

            fechaNacimiento = fechaNacimiento,
            direccion = direccion,
            telefono = telefono,
            password = BCrypt.hashpw(password, BCrypt.gensalt()),
            fotoUrl = fotoUrl
        )

        val entity = UsuarioEntity(
            id = nuevo.id,
            nombre = nuevo.nombre,
            email = nuevo.email,
            edad = nuevo.edad,
            fechaNacimiento = nuevo.fechaNacimiento,
            direccion = nuevo.direccion,
            telefono = nuevo.telefono,
            passwordHash = nuevo.password,
            fotoUrl = nuevo.fotoUrl
        )

        usuarioDao.insertarUsuario(entity)
        _usuarioActual.value = nuevo
        Result.success(nuevo)
    }

    // ==============================
    // LOGIN DE USUARIO
    // ==============================
    suspend fun login(email: String, password: String): Result<Usuario> =
        withContext(Dispatchers.IO) {
            val entity = usuarioDao.obtenerPorEmail(email)
                ?: return@withContext Result.failure(IllegalArgumentException("Correo no registrado"))

            return@withContext if (BCrypt.checkpw(password, entity.passwordHash)) {
                val usuario = Usuario(
                    id = entity.id,
                    nombre = entity.nombre,
                    email = entity.email,
                    edad = entity.edad,
                    fechaNacimiento = entity.fechaNacimiento,
                    direccion = entity.direccion,
                    telefono = entity.telefono,
                    password = entity.passwordHash,
                    fotoUrl = entity.fotoUrl
                )
                _usuarioActual.value = usuario
                Result.success(usuario)
            } else {
                Result.failure(IllegalArgumentException("Contraseña incorrecta"))
            }
        }

    // ==============================
    // CERRAR SESIÓN
    // ==============================

    fun logout() {
        _usuarioActual.value = null
    }


    // ==============================
    // ACTUALIZAR PERFIL
    // ==============================
    suspend fun actualizarPerfil(usuario: Usuario): Result<Unit> =
        withContext(Dispatchers.IO) {
            val encontrado = usuarioDao.obtenerPorId(usuario.id)
                ?: return@withContext Result.failure(IllegalArgumentException("Usuario no encontrado"))

            usuarioDao.actualizarPerfil(
                id = usuario.id,
                nombre = usuario.nombre,
                direccion = usuario.direccion,
                telefono = usuario.telefono,
                fotoUrl = usuario.fotoUrl
            )

            _usuarioActual.value = usuario
            Result.success(Unit)
        }

}
