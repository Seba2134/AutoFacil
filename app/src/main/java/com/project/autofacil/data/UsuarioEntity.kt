package com.project.autofacil.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "usuarios", indices = [
    Index(value = ["correo"], unique = true) // correo unico
])
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val direccion: String,
    val rol: String = "cliente"
)
