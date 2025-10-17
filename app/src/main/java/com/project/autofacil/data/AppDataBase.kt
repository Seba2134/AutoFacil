package com.project.autofacil.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [AutoEntity::class, UsuarioEntity::class],
    version = 4, // 🔺Aumenta la versión para aplicar los nuevos campos
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
                    .fallbackToDestructiveMigration() // Borra y recrea si cambia la estructura
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // 🔹 Callback que se ejecuta solo al crear la base por primera vez
    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    prepopulate(database.autoDao())
                }
            }
        }
    }
}

// ✅ Esta función va *fuera* de la clase AppDatabase
suspend fun prepopulate(autoDao: AutoDao) {
    val autos = listOf(
        AutoEntity(
            marca = "Toyota",
            modelo = "Corolla XLi",
            anio = 2021,
            kilometraje = 34000,
            precio = 12500000,
            disponible = true,
            fotoUrl = "https://cdn.motor1.com/images/mgl/p7Wey/s1/toyota-corolla-2021.jpg"
        ),
        AutoEntity(
            marca = "Mazda",
            modelo = "3 Sport",
            anio = 2022,
            kilometraje = 21000,
            precio = 15490000,
            disponible = true,
            fotoUrl = "https://cdn.motor1.com/images/mgl/7e94x/s1/mazda3-sport-2022.jpg"
        ),
        AutoEntity(
            marca = "Suzuki",
            modelo = "Swift Sport",
            anio = 2023,
            kilometraje = 8000,
            precio = 17900000,
            disponible = true,
            fotoUrl = "https://cdn.motor1.com/images/mgl/pj0Pp/s1/suzuki-swift-sport-2023.jpg"
        ),
        AutoEntity(
            marca = "Kia",
            modelo = "Rio Sedan",
            anio = 2020,
            kilometraje = 56000,
            precio = 11200000,
            disponible = false,
            fotoUrl = "https://cdn.motor1.com/images/mgl/1LmeN/s1/kia-rio-2020-sedan.jpg"
        )
    )

    autos.forEach { autoDao.insertar(it) }
}
