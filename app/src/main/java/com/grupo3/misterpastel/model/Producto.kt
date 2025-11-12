package com.grupo3.misterpastel.model



data class Producto(
    val id: Int,
    val nombre: String,
    val precio: String,
    val imagen: String, // ahora guarda el nombre del recurso
    val categoria: Categoria,
    val descripcion: String
)



    /** Convierte strings tipo "45.000 CLP", "$12.990", "12990" en 12990.0 de forma tolerante. */
    fun Producto.precioDouble(): Double =
        precio
            .replace("[^\\d.,]".toRegex(), "") // deja solo dígitos y separadores
            .replace(".", "")                  // quita separador de miles
            .replace(",", ".")                 // coma -> punto
            .toDoubleOrNull() ?: 0.0


