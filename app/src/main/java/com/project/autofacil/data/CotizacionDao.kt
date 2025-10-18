package com.project.autofacil.data
import androidx.room.Dao
import androidx.room.Insert



@Dao
interface CotizacionDao {
    @Insert
    suspend fun guardarCotizacion(cotizacion: CotizacionEntity)
}