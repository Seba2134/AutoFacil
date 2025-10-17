package com.project.autofacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.ViewModels.AutoViewModelFactory
import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.ViewModels.UsuarioViewModelFactory
import com.project.autofacil.data.AppDatabase
import com.project.autofacil.navigation.appNavigation
import com.project.autofacil.ui.theme.AutoFacilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Crear instancia de base de datos con pre-poblacion automatica
        val db = AppDatabase.getDatabase(applicationContext, lifecycleScope)

// UsuarioViewModel
        val usuarioDao = db.usuarioDao()
        val usuarioFactory = UsuarioViewModelFactory(usuarioDao)
        val usuarioViewModel = ViewModelProvider(this, usuarioFactory)[UsuarioViewModel::class.java]

// AutoViewModel
        val autoDao = db.autoDao()
        val autoFactory = AutoViewModelFactory(autoDao)
        val autoViewModel = ViewModelProvider(this, autoFactory)[AutoViewModel::class.java]

// Cargar UI principal
        setContent {
            AutoFacilTheme {
                Scaffold { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        appNavigation(usuarioViewModel, autoViewModel)
                    }
                }
            }
        }

    }
}
