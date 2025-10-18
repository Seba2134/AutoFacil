package com.project.autofacil.data

import androidx.room.*

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerTodos(): List<UsuarioEntity>
}
