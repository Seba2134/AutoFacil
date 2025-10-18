package com.project.autofacil.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cliente(
    @PrimaryKey(autoGenerate = true) val idCliente: Int = 0,
    val nombre: String,
    val rut: String,
    val telefono : String,
    val email: String,
    val password: String,
    val rol: String //admnin o cliente
)