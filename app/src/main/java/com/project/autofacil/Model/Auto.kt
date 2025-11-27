package com.project.autofacil.Model // Ojo con la mayúscula en Model si tu paquete es así

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "autos")
data class Auto(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") // Mapea el campo "id" del JSON a esta variable
    val id: Int = 0,

    @SerializedName("marca")
    val marca: String,

    @SerializedName("modelo")
    val modelo: String,

    @SerializedName("anio")
    val anio: Int,

    @SerializedName("kilometraje")
    val kilometraje: Int = 0,

    @SerializedName("precio")
    val precio: Int,

    @SerializedName("disponible")
    val disponible: Boolean = true,

    // fotoId es para recursos locales (R.drawable.x), la API quizás no lo use,
    // pero lo dejamos por compatibilidad con tu código actual.
    val fotoId: Int = 0,

    // Este es el importante para internet (URL de la imagen)
    @SerializedName("fotoUri")
    val fotoUri: String? = null
)