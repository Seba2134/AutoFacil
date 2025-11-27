package com.project.autofacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.data.AppDatabase
import com.project.autofacil.navigation.appNavigation
import com.project.autofacil.ui.theme.AutoFacilTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Instanciamos la Base de Datos (usando lazy o directo aquí está bien)
        val db = AppDatabase.getDatabase(applicationContext, CoroutineScope(Dispatchers.IO))

        // 2. CREACIÓN CORRECTA DE VIEWMODELS (Con Factory)
        // Esto asegura que el ViewModel sobreviva a cambios de configuración (rotación)

        val usuarioViewModel: UsuarioViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UsuarioViewModel(db.usuarioDao()) as T
                }
            }
        }

        val autoViewModel: AutoViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AutoViewModel(db.autoDao()) as T
                }
            }
        }

        // 3. Renderizamos la UI
        setContent {
            AutoFacilTheme {
                Scaffold { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // Pasamos los ViewModels "inteligentes" a la navegación
                        appNavigation(
                            usuarioViewModel = usuarioViewModel,
                            autoViewModel = autoViewModel
                        )
                    }
                }
            }
        }
    }
}