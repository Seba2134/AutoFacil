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
import coil.compose.rememberAsyncImagePainter
import com.project.autofacil.R
import com.project.autofacil.ViewModels.AutoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutosScreen(autoViewModel: AutoViewModel) {
    val autos by autoViewModel.autos.collectAsState()

    // 🔹 Llamamos al cargar la pantalla
    LaunchedEffect(Unit) {
        autoViewModel.cargarAutos()
    }

    // 🔹 Si no hay autos aún, muestra texto temporal
    if (autos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando autos...", style = MaterialTheme.typography.titleMedium)
        }
    } else {
        // 🔹 Pantalla con barra y grilla
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Catálogo de Autos", fontWeight = FontWeight.Bold) }
                )
            }
        ) { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(8.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(autos) { auto ->
                    AutoCard(
                        marca = auto.marca,
                        modelo = auto.modelo,
                        anio = auto.anio,
                        precio = auto.precio,
                        fotoUrl = auto.fotoUrl
                    )
                }
            }
        }
    }
}

@Composable
fun AutoCard(
    marca: String,
    modelo: String,
    anio: Int,
    precio: Int,
    fotoUrl: String?
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = fotoUrl,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.placeholder_car),
                        error = painterResource(id = R.drawable.placeholder_car)
                    ),
                    contentDescription = "$marca $modelo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "$marca $modelo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Año: $anio",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Precio: $${"%,d".format(precio)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
