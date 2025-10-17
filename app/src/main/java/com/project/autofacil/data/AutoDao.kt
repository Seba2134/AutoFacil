package com.project.autofacil.data

import androidx.room.*

@Dao
interface AutoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(auto: AutoEntity)

    @Query("SELECT * FROM autos")
    suspend fun obtenerTodos(): List<AutoEntity>

    @Delete
    suspend fun eliminar(auto: AutoEntity)
}
