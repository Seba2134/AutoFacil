package com.project.autofacil.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AutoDao {

    // (Opcional) Puedes mantener tu método 'insertar' original si lo usas en otro lugar.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(auto: AutoEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarVarios(autos: List<AutoEntity>)


    @Query("SELECT * FROM autos")
    suspend fun obtenerTodos(): List<AutoEntity>

    @Query("SELECT COUNT(*) FROM autos")
    suspend fun contarAutos(): Int
}