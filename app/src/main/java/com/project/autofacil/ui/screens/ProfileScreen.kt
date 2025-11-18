package com.project.autofacil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.autofacil.R
import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// 1. Recibimos UsuarioViewModel en lugar de MainViewModel
fun ProfileScreen(
    navController: NavController,
    viewModel: UsuarioViewModel
) {
    // Obtenemos el estado del usuario desde el ViewModel
    val estadoUsuario by viewModel.estado.collectAsState()
    val cliente by viewModel.cliente.collectAsState()

    val nombreMostrado = cliente?.nombre ?: estadoUsuario.nombre
    val correoMostrado = cliente?.email ?: estadoUsuario.correo

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp), // Añade un padding general
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Ícono de Perfil grande
            Image(
                // Usa un ícono genérico de tus recursos drawable.
                // Si no tienes uno, puedes usar Icons.Default.Person
                painter = painterResource(id = R.drawable.ic_profile_user), // Reemplaza con tu ícono
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del Usuario
            Text(
                text = nombreMostrado,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Correo del Usuario
            Text(
                text = correoMostrado,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            HorizontalDivider() // Un separador visual

            // Sección de información
            InfoRow(icon = Icons.Default.Person, text = "Nombre: $nombreMostrado")
            InfoRow(icon = Icons.Default.Email, text = "Correo: $correoMostrado")


            Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

            // Botón para volver a la pantalla de inicio
            Button(
                onClick = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ir a Inicio")
            }
        }
    }
}

// Composable auxiliar para mostrar filas de información con íconos
@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 16.sp)
    }
}
