package com.project.autofacil.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.ui.screens.AutoDetailScreen
import com.project.autofacil.ui.screens.AutosScreen
import com.project.autofacil.ui.screens.CotizacionScreen
import com.project.autofacil.ui.screens.HomeScreen
import com.project.autofacil.ui.screens.MainLayout // <--- Importante: El layout nuevo
import com.project.autofacil.ui.screens.MapaScreen
import com.project.autofacil.ui.screens.ProfileScreen
import com.project.autofacil.ui.screens.registroScreen
import com.project.autofacil.ui.screens.resumenScreen

@Composable
fun appNavigation(usuarioViewModel: UsuarioViewModel, autoViewModel: AutoViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // --- PANTALLAS PRINCIPALES (CON MENÚ SÁNDWICH) ---

        // 1. Catálogo (Inicio)
        composable(Screen.Home.route) {
            MainLayout(navController = navController, title = "Catálogo de Autos") { paddingValues ->
                // Aquí pasamos el padding directamente porque modificamos AutosScreen para recibirlo
                AutosScreen(navController = navController, autoViewModel = autoViewModel, paddingValues = paddingValues)
            }
        }

        // 2. Registro
        composable(Screen.Registro.route) {
            MainLayout(navController = navController, title = "Crear Cuenta") { paddingValues ->
                // Usamos Box para aplicar el padding sin modificar registroScreen por dentro
                Box(modifier = Modifier.padding(paddingValues)) {
                    registroScreen(navController, usuarioViewModel)
                }
            }
        }

        // 3. Perfil
        composable(Screen.Profile.route) {
            MainLayout(navController = navController, title = "Mi Perfil") { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    ProfileScreen(navController = navController, viewModel = usuarioViewModel)
                }
            }
        }

        // 4. Mapa (Sucursales)
        composable(Screen.Mapa.route) {
            MainLayout(navController = navController, title = "Sucursales") { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    MapaScreen()
                }
            }
        }

        // --- PANTALLAS SECUNDARIAS (SIN MENÚ, CON FLECHA ATRÁS O FINAL DE FLUJO) ---

        composable(Screen.Resumen.route) {
            resumenScreen(usuarioViewModel, navController)
        }

        composable(
            route = Screen.AutoDetail.route,
            arguments = listOf(navArgument("autoId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val autoId = backStackEntry.arguments?.getString("autoId")
            AutoDetailScreen(
                navController = navController,
                autoViewModel = autoViewModel,
                autoId = autoId
            )
        }

        composable(
            route = Screen.CotizacionScreen.route,
            arguments = listOf(navArgument("autoId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val autoId = backStackEntry.arguments?.getString("autoId") ?: "0"
            CotizacionScreen(
                navController = navController,
                autoViewModel = autoViewModel,
                usuarioViewModel = usuarioViewModel,
                autoId = autoId
            )
        }
    }
}