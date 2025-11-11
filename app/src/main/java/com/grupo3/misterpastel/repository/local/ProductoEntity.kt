package com.grupo3.misterpastel.repository.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para persistir los productos del catálogo.
 *
 * 🔹 La propiedad [imagen] almacena el **nombre del recurso drawable** (por ejemplo: "torta_chocolate"),
 *     no el ID numérico, para evitar problemas de compatibilidad entre compilaciones.
 * 🔹 El campo [categoria] guarda el nombre del enum (por ejemplo: "TORTA_CIRCULAR").
 */
@Entity(tableName = "producto")
data class ProductoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val precio: String,
    val imagen: String,       // Guarda el nombre del recurso (sin extensión)
    val categoria: String,    // Nombre del enum Categoria
    val descripcion: String
)
