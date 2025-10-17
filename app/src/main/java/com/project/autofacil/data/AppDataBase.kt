package com.project.autofacil.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AutoEntity::class, UsuarioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun autoDao(): AutoDao
    abstract fun usuarioDao(): UsuarioDao
}
