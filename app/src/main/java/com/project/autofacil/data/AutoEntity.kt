package com.project.autofacil.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autos")
data class AutoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val marca: String,
    val modelo: String,
    val anio: Int,
    val precio: Double
)
