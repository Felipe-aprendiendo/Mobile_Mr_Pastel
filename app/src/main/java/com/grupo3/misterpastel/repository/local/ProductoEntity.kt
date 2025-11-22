package com.grupo3.misterpastel.repository.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "producto")
data class ProductoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val precio: String,
    val imagen: String,            // URL Cloudinary
    val categoria: String,
    val descripcion: String,
    val imagenLocal: String?       // Nombre del drawable local (opcional)
)
