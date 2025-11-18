package com.project.autofacil.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.project.autofacil.R

@Entity(tableName = "autos")
data class AutoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val marca: String,
    val modelo: String,
    val anio: Int,
    val kilometraje: Int = 0,
    val precio: Int,
    val disponible: Boolean = true,
    val fotoId: Int = R.drawable.placeholder_car,
    val fotoUri: String? = null
)
