package com.project.autofacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.autofacil.ViewModels.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaUsuariosScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    val usuarioActual by usuarioViewModel.usuarioActual.collectAsState()
    val usuarios by usuarioViewModel.todosLosUsuarios.collectAsState()

    // Si no hay usuario o no es admin -> volver atrás
    LaunchedEffect(usuarioActual) {
        if (usuarioActual == null || usuarioActual?.rol != "admin") {
            navController.popBackStack()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Usuarios registrados") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(usuarios) { usuario ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Nombre: ${usuario.nombre}")
                        Text("Correo: ${usuario.correo}")
                        Text("Rol: ${usuario.rol}")
                    }
                }
            }
        }
    }
}