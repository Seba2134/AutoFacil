package com.project.autofacil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.autofacil.Model.Auto
import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.navigation.Screen

@Composable
fun AutosScreen(
    navController: NavController,
    autoViewModel: AutoViewModel,
    paddingValues: PaddingValues // Recibimos el padding del menú
) {
    // 1. Escuchamos el Flow del ViewModel. Necesitamos un valor inicial (lista vacía)
    val autos by autoViewModel.autos.collectAsState(initial = emptyList())

    // 2. Solo llamamos a la API al iniciar. Ya no llamamos a cargarAutos()
    LaunchedEffect(Unit) {
        autoViewModel.cargarAutosDesdeApi()
    }

    if (autos.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            Text(
                "Cargando catálogo...",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 64.dp)
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Respetamos la barra superior
                .padding(8.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(autos) { autoEntity ->
                val autoModel = Auto(
                    id = autoEntity.id,
                    marca = autoEntity.marca,
                    modelo = autoEntity.modelo,
                    anio = autoEntity.anio,
                    precio = autoEntity.precio,
                    fotoId = autoEntity.fotoId,
                    kilometraje = autoEntity.kilometraje
                )

                AutoCard(
                    auto = autoModel,
                    onVerClick = { autoId ->
                        navController.navigate(Screen.AutoDetail.createRoute(autoId))
                    }
                )
            }
        }
    }
}

// La función AutoCard se mantiene igual, pero si la necesitas dímelo.
@Composable
fun AutoCard(
    auto: Auto,
    onVerClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = auto.fotoId),
                contentDescription = "${auto.marca} ${auto.modelo}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "${auto.marca} ${auto.modelo}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${"%,d".format(auto.precio)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onVerClick(auto.id.toString()) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver")
                }
            }
        }
    }
}