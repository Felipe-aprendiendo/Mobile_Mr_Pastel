package com.grupo3.misterpastel.repository

import com.grupo3.misterpastel.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Simula registro/login de usuarios en memoria.
 * Si luego agregan DataStore, solo reemplazan el backend de estas funciones.
 */
object UsuarioRepository {

    private val usuarios = mutableListOf<Usuario>()
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    fun registrar(nombre: String, email: String, password: String, edad: Int, fotoUrl: String? = null): Result<Usuario> {
        if (usuarios.any { it.email.equals(email, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("El correo ya está registrado"))
        }
        val nuevo = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            email = email,
            edad = edad,
            password = password,
            fotoUrl = fotoUrl
        )
        usuarios.add(nuevo)
        _usuarioActual.value = nuevo
        return Result.success(nuevo)
    }

    fun login(email: String, password: String): Result<Usuario> {
        val u = usuarios.find { it.email.equals(email, ignoreCase = true) && it.password == password }
            ?: return Result.failure(IllegalArgumentException("Credenciales inválidas"))
        _usuarioActual.value = u
        return Result.success(u)
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
