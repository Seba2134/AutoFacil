package com.project.autofacil.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.project.autofacil.R
import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.data.AutoEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarAutoScreen(
    navController: NavController,
    autoViewModel: AutoViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    // Solo admin: si no es admin, lo echamos para atrás
    val cliente by usuarioViewModel.cliente.collectAsState()

    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(cliente) {
        if (cliente?.rol != "admin") {
            navController.popBackStack()
        }
    }

    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var kilometraje by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar auto") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = marca,
                onValueChange = { marca = it },
                label = { Text("Marca") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = modelo,
                onValueChange = { modelo = it },
                label = { Text("Modelo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = anio,
                onValueChange = { anio = it },
                label = { Text("Año") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = kilometraje,
                onValueChange = { kilometraje = it },
                label = { Text("Kilometraje") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            if (error != null) {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
            }
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleccionar foto")
            }

            Spacer(Modifier.height(12.dp))

            selectedImageUri?.let { uri ->
                // Vista previa simple
                AsyncImage(
                    model = uri,
                    contentDescription = "Foto seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    val anioInt = anio.toIntOrNull()
                    val kmInt = kilometraje.toIntOrNull() ?: 0
                    val precioInt = precio.toIntOrNull()

                    if (marca.isBlank() || modelo.isBlank() || anioInt == null || precioInt == null) {
                        error = "Completa todos los campos numéricos correctamente"
                        return@Button
                    }

                    val nuevoAuto = AutoEntity(
                        marca = marca,
                        modelo = modelo,
                        anio = anioInt,
                        kilometraje = kmInt,
                        precio = precioInt,
                        disponible = true,
                        fotoId = R.drawable.placeholder_car,
                        fotoUri = selectedImageUri?.toString()
                    )
                    autoViewModel.agregarAuto(nuevoAuto)
                    navController.popBackStack()  // volver a AutosScreen
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar auto")
            }
        }
    }
}