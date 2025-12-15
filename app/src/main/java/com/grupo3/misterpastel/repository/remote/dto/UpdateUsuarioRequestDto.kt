package com.grupo3.misterpastel.repository.remote.dto


/**
 * Body para PUT /usuarios/{id}
 * Se envían solo los campos a actualizar. Los null quedan como "no cambiar".
 * fechaNacimiento debe ir como "YYYY-MM-DD" o null.
 */
data class UpdateUsuarioRequestDto(
    val nombre: String? = null,
    val edad: Int? = null,
    val fechaNacimiento: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val fotoUrl: String? = null
)
