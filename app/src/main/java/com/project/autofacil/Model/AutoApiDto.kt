package com.project.autofacil.Model

import com.google.gson.annotations.SerializedName

data class AutoApiDto(
    @SerializedName("id") val id: String,
    @SerializedName("marca") val marca: String,
    @SerializedName("modelo") val modelo: String,
    @SerializedName("anio") val anio: Int,
    @SerializedName("precio") val precio: Int,
    @SerializedName("kilometraje") val kilometraje: String,
    @SerializedName("imagenUrl") val imagenUrl: String // Esto será una foto de internet
)