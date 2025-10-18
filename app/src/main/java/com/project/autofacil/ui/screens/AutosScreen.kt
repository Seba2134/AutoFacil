package com.project.autofacil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.project.autofacil.Model.Auto
import com.project.autofacil.R
import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//Añadimos NavController como parametro
fun AutosScreen(navController: NavController, autoViewModel: AutoViewModel) {
    val autos by autoViewModel.autos.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Llamamos a cargarAutos cuando se entra a la pantalla
    LaunchedEffect(Unit) {
        autoViewModel.cargarAutos()
    }

    // 2. Envolvemos todo en ModalNavigationDrawer, igual que en HomeScreen
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Reutilizamos el mismo contenido del menú
            ModalDrawerSheet {
                Text("Menú", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Home.route)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Registro") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Registro.route)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }
        },
        gesturesEnabled = true
    ) {
        // 3. Usamos el Scaffold con la TopAppBar que contiene el icono de hamburguesa
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Catálogo de Autos") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            // El contenido original de tu pantalla va aquí adentro
            if (autos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator() // Usamos un indicador de carga
                    Text(
                        "Cargando autos...",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 64.dp)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(8.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(autos) { autoEntity -> // 1. Cambiamos el nombre a 'autoEntity' para más claridad
                        // 2. Creamos un objeto 'Auto' a partir de la 'AutoEntity'
                        val autoModel = Auto(
                            id = autoEntity.id,
                            marca = autoEntity.marca,
                            modelo = autoEntity.modelo,
                            anio = autoEntity.anio,
                            precio = autoEntity.precio,
                            fotoUrl = autoEntity.fotoUrl,
                            kilometraje = autoEntity.kilometraje
                        )

                        // 3. Pasamos el objeto 'autoModel' (que es del tipo correcto) a la AutoCard
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
    }

}
@Composable
fun AutoCard(
    auto: Auto, // Recibe el objeto Auto completo (usando el path completo por si acaso)
    onVerClick: (String) -> Unit // Recibe una función para manejar el clic
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(), // La altura se ajusta al contenido
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = auto.fotoUrl,
                    placeholder = painterResource(id = R.drawable.placeholder_car),
                    error = painterResource(id = R.drawable.placeholder_car)
                ),
                contentDescription = "${auto.marca} ${auto.modelo}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Le damos una altura fija a la imagen
            )

            // Contenedor para el texto y el botón
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
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
                Spacer(modifier = Modifier.height(8.dp)) // Espacio antes del botón
                OutlinedButton(
                    onClick = {
                        // Al hacer clic, llamamos a la función onVerClick
                        // pasándole el ID del auto (convertido a String).
                        onVerClick(auto.id.toString())
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver")
                }
            }
        }
    }
}
