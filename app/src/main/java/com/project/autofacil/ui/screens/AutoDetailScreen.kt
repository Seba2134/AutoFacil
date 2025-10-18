// en: ui/screens/AutoDetailScreen.kt
package com.project.autofacil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.project.autofacil.Model.Auto
import com.project.autofacil.R
import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoDetailScreen(
    navController: NavController,
    autoViewModel: AutoViewModel,
    autoId: String? // Recibimos el ID desde la navegación
) {
    // 1. Pedimos al ViewModel que cargue el auto específico
    LaunchedEffect(autoId) {
        if (autoId != null) {
            autoViewModel.getAutoById(autoId)
        }
    }

    // 2. Observamos el auto seleccionado en el ViewModel
    val autoSeleccionado by autoViewModel.autoSeleccionado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(autoSeleccionado?.let { "${it.marca} ${it.modelo}" } ?: "Detalle del Auto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Botón para volver atrás
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        // 3. Mostramos un indicador de carga o los detalles del auto
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
            if (autoSeleccionado == null || autoSeleccionado?.id != autoId?.toIntOrNull()) {
                CircularProgressIndicator()
            } else {
                autoSeleccionado?.let { auto ->
                    DetalleAutoContent(auto = auto,
                        navController = navController,
                        autoId = auto.id.toString())
                }
            }
        }
    }
}

@Composable
fun DetalleAutoContent(auto: Auto, navController: NavController, autoId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = auto.fotoUrl,
                placeholder = painterResource(id = R.drawable.placeholder_car),
                error = painterResource(id = R.drawable.placeholder_car)
            ),
            contentDescription = "Foto de ${auto.marca} ${auto.modelo}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${auto.marca} ${auto.modelo}",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Especificaciones
        InfoRow(label = "Año:", value = auto.anio.toString())
        InfoRow(label = "Precio:", value = "$${"%,d".format(auto.precio)}")
        InfoRow(label = "Kilometraje:", value = "${"%,d".format(auto.kilometraje)} km")
        Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

        Button(
            onClick = {
                navController.navigate(Screen.CotizacionScreen.createRoute(autoId))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("COTIZAR", fontSize = 16.sp)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
        Text(text = value)
    }
}
