package com.grupo3.misterpastel.repository

import com.grupo3.misterpastel.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import org.mindrot.jbcrypt.BCrypt


/**
 * Simula registro/login de usuarios en memoria.
 * Si luego agregan DataStore, solo reemplazan el backend de estas funciones.
 */
object UsuarioRepository {

    private val usuarios = mutableListOf<Usuario>()
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    fun registrar(
        nombre: String,
        email: String,
        password: String,
        edad: Int,
        fechaNacimiento: String,
        direccion: String,
        telefono: String,
        fotoUrl: String? = null
    ): Result<Usuario> {
        if (usuarios.any { it.email.equals(email, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("El correo ya está registrado"))
        }

        // Genera el hash de la contraseña
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

        val nuevo = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            email = email,
            edad = edad,
            fechaNacimiento = fechaNacimiento,
            direccion = direccion,
            telefono = telefono,
            password = hashedPassword, // ✅ guardamos el hash
            fotoUrl = fotoUrl
        )

        usuarios.add(nuevo)
        _usuarioActual.value = nuevo
        return Result.success(nuevo)
    }



    fun login(email: String, password: String): Result<Usuario> {
        val u = usuarios.find { it.email.equals(email, ignoreCase = true) }
            ?: return Result.failure(IllegalArgumentException("Credenciales inválidas"))

        // Verificamos el hash en lugar de comparar texto plano
        return if (BCrypt.checkpw(password, u.password)) {
            _usuarioActual.value = u
            Result.success(u)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }


    fun logout() {
        _usuarioActual.value = null
    }

    fun actualizarPerfil(usuarioActualizado: Usuario): Result<Unit> {
        val idx = usuarios.indexOfFirst { it.id == usuarioActualizado.id }
        if (idx == -1) return Result.failure(IllegalArgumentException("Usuario no encontrado"))
        usuarios[idx] = usuarioActualizado
        _usuarioActual.value = usuarioActualizado
        return Result.success(Unit)
    }
}
