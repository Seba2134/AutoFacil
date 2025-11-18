package com.project.autofacil.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.project.autofacil.R
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [AutoEntity::class, UsuarioEntity::class, CotizacionEntity::class],
    version = 13,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun autoDao(): AutoDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "autofacil_db"
                )
                    .fallbackToDestructiveMigration()
                    // --- CORRECCIÓN 1: Pasar el 'scope' al Callback ---
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    private class AppDatabaseCallback(
        // El 'context' no se estaba usando, así que lo quitamos para limpiar.
        // Solo necesitamos el scope.
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    // Pre-popular autos
                    prepopulate(database.autoDao())
                    // Pre-popular usuario admin
                    prepopulateUsuarios(database.usuarioDao())
                }
            }
        }
    }
}

suspend fun prepopulate(autoDao: AutoDao) {
    Log.d("ROOM_DEBUG", "Ejecutando prepopulate()...")

    val autos = listOf(
        AutoEntity(
            marca = "Toyota",
            modelo = "Corolla XLi",
            anio = 2021,
            kilometraje = 34000,
            precio = 12500000,
            disponible = true,
            fotoId = R.drawable.toyota_corolla_xli_2021
        ),
        AutoEntity(
            marca = "Mazda",
            modelo = "3 Sport",
            anio = 2022,
            kilometraje = 21000,
            precio = 15490000,
            disponible = true,
            fotoId = R.drawable.mazda_3_sport_2022
        ),
        AutoEntity(
            marca = "Suzuki",
            modelo = "Swift Sport",
            anio = 2023,
            kilometraje = 8000,
            precio = 17900000,
            disponible = true,
            fotoId = R.drawable.suzuki_swift_2023
        ),
        AutoEntity(
            marca = "Kia",
            modelo = "Rio Sedan",
            anio = 2020,
            kilometraje = 56000,
            precio = 11200000,
            disponible = false,
            fotoId = R.drawable.kia_rio_sedan_2020
        )
    )

    try {
        autos.forEach {
            Log.d("ROOM_DEBUG", "Insertando auto: ${it.marca}")
            autoDao.insertar(it)
        }

        val count = autoDao.contarAutos()
        Log.d("ROOM_DEBUG", "Total autos en DB después del prepopulate(): $count")
    } catch (e: Exception) {
        Log.e("ROOM_DEBUG", "Error al insertar autos: ${e.message}")
        e.printStackTrace()
    }
}
suspend fun prepopulateUsuarios(usuarioDao: UsuarioDao) {
    Log.d("ROOM_DEBUG", "Insertando usuario administrador por defecto...")

    val admin = UsuarioEntity(
        nombre = "Administrador",
        correo = "admin@autofacil.cl",
        contrasena = "admin123",
        direccion = "Oficina central",
        rol = "admin"
    )

    try {
        usuarioDao.registrar(admin)
        Log.d("ROOM_DEBUG", "Admin insertado correctamente")
    } catch (e: Exception) {
        Log.e("ROOM_DEBUG", "Error al insertar admin: ${e.message}")
        e.printStackTrace()
    }
}
