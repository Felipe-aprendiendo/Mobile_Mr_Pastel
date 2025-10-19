package com.grupo3.misterpastel.model

import androidx.annotation.DrawableRes





data class Producto(
    val id: Int,
    val nombre: String,
    val precio: String,
    @DrawableRes val imagen: Int, //ver el comentario que dejé abajo
    val categoria: Categoria,
    val descripcion: String
)


/*
* Una cosa importantante sobre @DrawableRes es que maneja la imagen asociada al producto. De esta manera, cuando se crea una instancia de la clase Producto se debe importar la imagen directamente en la instancia que se está creando, como en el ejemplo de abajo
*
* val p1 = Producto(
    id = 1,
    nombre = "Torta Chocolate",
    precio = "$45.000 CLP",
    imagen = R.drawable.torta_chocolate,
    categoria = Categoria.TORTA_CUADRADA
)
* */

