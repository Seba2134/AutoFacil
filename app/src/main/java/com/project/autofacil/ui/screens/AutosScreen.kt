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
import coil.compose.AsyncImage
import com.project.autofacil.Model.Auto
import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutosScreen(
    navController: NavController,
    autoViewModel: AutoViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    // ESTADOS
    val autos by autoViewModel.autos.collectAsState()
    // Si isLoading te da error es porque falta en el ViewModel, avísame y lo quitamos
    // Pero asumo que ya lo tienes o puedes usar 'false' temporalmente
    val isLoading by autoViewModel.isLoading.collectAsState()
    val cliente by usuarioViewModel.cliente.collectAsState()

    // ESTADOS UI
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // EFECTO DE CARGA
    LaunchedEffect(Unit) {
        autoViewModel.cargarAutos()
    }

    // 1. ESTRUCTURA PRINCIPAL: El Drawer envuelve a TODO
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true, // Permite deslizar para abrir
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú AutoFácil", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()

                // --- ITEMS DEL MENÚ ---
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Home.route) }
                )
                NavigationDrawerItem(
                    label = { Text("Registro") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Registro.route) }
                )
                NavigationDrawerItem(
                    label = { Text("Sucursales") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Mapa.route) }
                )

                // Lógica de usuario logueado/no logueado
                if (cliente == null) {
                    NavigationDrawerItem(
                        label = { Text("Iniciar sesión") },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Login.route) }
                    )
                } else {
                    NavigationDrawerItem(
                        label = { Text("Perfil") },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Profile.route) }
                    )
                    NavigationDrawerItem(
                        label = { Text("Cerrar sesión") },
                        selected = false,
                        onClick = {
                            usuarioViewModel.cerrarSesion()
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Home.route)
                        }
                    )
                    // Opciones de Admin
                    if (cliente?.rol == "admin") {
                        NavigationDrawerItem(
                            label = { Text("Usuarios (Admin)") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.ListaUsuarios.route) }
                        )
                        NavigationDrawerItem(
                            label = { Text("Agregar Auto") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.AgregarAuto.route) }
                        )
                    }
                }
            }
        }
    ) {
        // 2. CONTENIDO DE LA PANTALLA (Scaffold)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Catálogo de Autos") },
                    navigationIcon = {
                        IconButton(onClick = {
                            // AQUÍ ESTÁ LA MAGIA DEL MENÚ
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            }
        ) { innerPadding ->

            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

                // LÓGICA VISUAL (Loading vs Empty vs Lista)
                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    autos.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No hay autos disponibles", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { autoViewModel.cargarAutos() }) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                    else -> {
                        // LA LISTA DE AUTOS
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(autos) { autoEntity ->
                                // Conversión segura Entity -> Model
                                val autoModel = Auto(
                                    id = autoEntity.id,
                                    marca = autoEntity.marca,
                                    modelo = autoEntity.modelo,
                                    anio = autoEntity.anio,
                                    precio = autoEntity.precio,
                                    fotoId = autoEntity.fotoId,
                                    fotoUri = autoEntity.fotoUri,
                                    kilometraje = autoEntity.kilometraje,
                                    disponible = autoEntity.disponible
                                )

                                AutoCard(
                                    auto = autoModel,
                                    onVerClick = { autoId ->
                                        // Convertimos a String de forma segura
                                        navController.navigate(Screen.AutoDetail.createRoute(autoId.toString()))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// TU COMPONENTE AUTOCARD (Lo dejo igual, solo asegurando imports)
@Composable
fun AutoCard(
    auto: Auto,
    onVerClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            // Lógica de Imagen: Prioridad URI (Internet) -> Fallback ID (Local)
            if (!auto.fotoUri.isNullOrEmpty()) {
                AsyncImage(
                    model = auto.fotoUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentScale = ContentScale.Crop,
                    // Si falla la carga de internet, muestra un placeholder local si quieres
                    error = painterResource(id = com.project.autofacil.R.drawable.placeholder_car)
                )
            } else {
                // Asegúrate de que auto.fotoId sea un recurso válido, sino usa uno por defecto
                val imageRes = if (auto.fotoId != 0) auto.fotoId else com.project.autofacil.R.drawable.placeholder_car
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "${auto.marca} ${auto.modelo}", fontWeight = FontWeight.Bold)
                Text(text = "$${auto.precio}", color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onVerClick(auto.id.toString()) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver Detalles")
                }
            }
        }
    }
}