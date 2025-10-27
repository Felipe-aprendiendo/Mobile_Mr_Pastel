package com.grupo3.misterpastel.repository.local


import androidx.room.Entity
import androidx.room.PrimaryKey

// MOD: Nueva entidad Room para persistir productos del catálogo.
@Entity(tableName = "producto")
data class ProductoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val precio: String,
    val imagen: Int,          // Guardamos el @DrawableRes como Int
    val categoria: String,    // Se almacena el enum como String (name)
    val descripcion: String
)
