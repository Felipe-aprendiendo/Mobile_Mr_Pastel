package com.grupo3.misterpastel.repository.local


import androidx.room.*

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuario WHERE email = :email LIMIT 1")
    suspend fun obtenerPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: String): UsuarioEntity?

    @Query("UPDATE usuario SET nombre = :nombre, direccion = :direccion, telefono = :telefono, fotoUrl = :fotoUrl WHERE id = :id")
    suspend fun actualizarPerfil(
        id: String,
        nombre: String,
        direccion: String,
        telefono: String,
        fotoUrl: String?
    )

    @Query("DELETE FROM usuario")
    suspend fun eliminarTodos()
}
