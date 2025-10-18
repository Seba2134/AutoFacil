package com.project.autofacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.data.AppDatabase
import com.project.autofacil.navigation.appNavigation
import com.project.autofacil.ui.theme.AutoFacilTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 Creamos la base de datos pasando el CoroutineScope
        val db = AppDatabase.getDatabase(applicationContext, CoroutineScope(Dispatchers.IO))

        // 🔹 Obtenemos los DAO
        val usuarioDao = db.usuarioDao()
        val autoDao = db.autoDao()

        // 🔹 Creamos los ViewModels
        val usuarioViewModel = UsuarioViewModel(usuarioDao)
        val autoViewModel = AutoViewModel(autoDao)

        // 🔹 Renderizamos el contenido con Compose
        setContent {
            AutoFacilTheme {
                Scaffold { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        appNavigation(usuarioViewModel = usuarioViewModel, autoViewModel = autoViewModel)
                    }
                }
            }
        }
    }
}
