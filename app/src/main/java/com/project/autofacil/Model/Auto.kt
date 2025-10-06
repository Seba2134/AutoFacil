package com.project.autofacil.Model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autos")
data class Auto (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val marca: String,
    val modelo: String,
    val anio: Int,
    val kilometraje: Int = 0,
    val precio: Int,
    val disponible: Boolean = true,
    val fotoUrl: String? = null)
{

    }