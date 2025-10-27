package com.grupo3.misterpastel.repository

import com.grupo3.misterpastel.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

object UsuarioRepository {

    private val usuarios = mutableListOf<Usuario>()
    internal val _usuarioActual = MutableStateFlow<Usuario?>(null)
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

        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

        val nuevo = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            email = email,
            edad = edad,
            fechaNacimiento = fechaNacimiento,
            direccion = direccion,
            telefono = telefono,
            password = hashedPassword, // Guardamos el hash
            fotoUrl = fotoUrl
        )

        usuarios.add(nuevo)
        _usuarioActual.value = nuevo
        return Result.success(nuevo)
    }

    // --- ¡CORREGIDO! ---
    // Esta función ahora usa 'BCrypt.checkpw' para comparar la contraseña
    fun buscarPorCredenciales(email: String, password: String): Usuario? {
        val usuario = usuarios.find { it.email.equals(email, ignoreCase = true) }
            ?: return null // Usuario no encontrado

        // Verificar la contraseña usando checkpw
        if (BCrypt.checkpw(password, usuario.password)) {
            _usuarioActual.value = usuario
            return usuario
        } else {
            return null // Contraseña incorrecta
        }
    }

    fun login(email: String, password: String): Result<Usuario> {
        val u = usuarios.find { it.email.equals(email, ignoreCase = true) }
            ?: return Result.failure(IllegalArgumentException("Credenciales inválidas"))

        return if (BCrypt.checkpw(password, u.password)) {
            _usuarioActual.value = u
            Result.success(u)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }

    fun logout() = cerrarSesion()

    fun actualizarPerfil(usuarioActualizado: Usuario): Result<Unit> {
        val idx = usuarios.indexOfFirst { it.id == usuarioActualizado.id }
        if (idx == -1) return Result.failure(IllegalArgumentException("Usuario no encontrado"))
        usuarios[idx] = usuarioActualizado
        _usuarioActual.value = usuarioActualizado
        return Result.success(Unit)
    }
}