package com.project.autofacil.Model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Cotizaciones")
data class Cotizacion (
    @PrimaryKey(autoGenerate = true) val idCotizacion: Int = 0,
    val rutCliente: String,
    val idAuto: Int,
    val fecha: Long = System.currentTimeMillis(),
    val marcaAuto: String,
    val modeloAuto: String,
    val valoresAuto: String

){
}