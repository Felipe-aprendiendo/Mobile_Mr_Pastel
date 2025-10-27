package com.grupo3.misterpastel.repository.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val email: String,
    val edad: Int,
    val fechaNacimiento: String,
    val direccion: String,
    val telefono: String,
    val passwordHash: String,
    val fotoUrl: String? = null
)
