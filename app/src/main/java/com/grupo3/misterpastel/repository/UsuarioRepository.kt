package com.grupo3.misterpastel.repository

import com.grupo3.misterpastel.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Repositorio que gestiona usuarios simulando un backend en memoria.
 * Compatible con AutenticarViewModel y futuras implementaciones con Room o DataStore.
 */
object UsuarioRepository {

    // Lista de usuarios simulados
    private val usuarios = mutableListOf<Usuario>()

    // Usuario actualmente autenticado (flujo observable)
    internal val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    // === REGISTRO DE USUARIO ===
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

        val nuevo = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            email = email,
            edad = edad,
            fechaNacimiento = fechaNacimiento,
            direccion = direccion,
            telefono = telefono,
            password = password,
            fotoUrl = fotoUrl
        )

        usuarios.add(nuevo)
        _usuarioActual.value = nuevo
        return Result.success(nuevo)
    }

    // === LOGIN ===
    // Mantiene compatibilidad con el ViewModel de autenticación (buscarPorCredenciales)
    fun buscarPorCredenciales(email: String, password: String): Usuario? {
        val usuario = usuarios.find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
        _usuarioActual.value = usuario
        return usuario
    }

    // Alias alternativo si se usa directamente en otras pantallas
    fun login(email: String, password: String): Result<Usuario> {
        val u = usuarios.find { it.email.equals(email, ignoreCase = true) && it.password == password }
            ?: return Result.failure(IllegalArgumentException("Credenciales inválidas"))
        _usuarioActual.value = u
        return Result.success(u)
    }

    // === CERRAR SESIÓN ===
    fun cerrarSesion() {
        _usuarioActual.value = null
    }

    // Alias alternativo
    fun logout() = cerrarSesion()

    // === ACTUALIZAR PERFIL ===
    fun actualizarPerfil(usuarioActualizado: Usuario): Result<Unit> {
        val idx = usuarios.indexOfFirst { it.id == usuarioActualizado.id }
        if (idx == -1) return Result.failure(IllegalArgumentException("Usuario no encontrado"))
        usuarios[idx] = usuarioActualizado
        _usuarioActual.value = usuarioActualizado
        return Result.success(Unit)
    }
}
