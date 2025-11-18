package com.project.autofacil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.autofacil.ui.screens.AutosScreen
import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.ui.screens.HomeScreen
import com.project.autofacil.ui.screens.ProfileScreen
import com.project.autofacil.ui.screens.registroScreen
import com.project.autofacil.ui.screens.resumenScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.project.autofacil.ui.screens.AgregarAutoScreen
import com.project.autofacil.ui.screens.AutoDetailScreen
import com.project.autofacil.ui.screens.CotizacionScreen
import com.project.autofacil.ui.screens.ListaUsuariosScreen
import com.project.autofacil.ui.screens.LoginScreen
import com.project.autofacil.ui.screens.MapaScreen

@Composable
fun appNavigation(usuarioViewModel: UsuarioViewModel, autoViewModel: AutoViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            AutosScreen(navController = navController, autoViewModel = autoViewModel, usuarioViewModel = usuarioViewModel)
        }
        composable(Screen.Registro.route) {
            registroScreen(navController, usuarioViewModel)
        }
        composable(Screen.Resumen.route) {
            resumenScreen(usuarioViewModel, navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController, viewModel = usuarioViewModel)
        }
        composable(
            route = Screen.AutoDetail.route, // Ruta: "auto_detail_screen/{autoId}"
            arguments = listOf(navArgument("autoId") { // Definimos que esperamos un argumento
                type = NavType.StringType // El argumento es de tipo String
                nullable = true // Opcional: puede ser nulo, aunque no debería
            })
        ) { backStackEntry ->
            // Obtenemos el argumento 'autoId' que nos llega por la URL
            val autoId = backStackEntry.arguments?.getString("autoId")

            // Llamamos a la pantalla de detalles, pasándole los datos necesarios
            AutoDetailScreen(
                navController = navController,
                autoViewModel = autoViewModel,
                autoId = autoId
            )
        }
        composable(
            route = Screen.CotizacionScreen.route, // Ruta: "cotizacion_screen/{autoId}"
            arguments = listOf(navArgument("autoId") { // Definimos el argumento 'autoId'
                type = NavType.StringType
            })
        ) { backStackEntry ->
            // Obtenemos el argumento 'autoId' de la URL
            val autoId = backStackEntry.arguments?.getString("autoId") ?: "0"
            // Llamamos al Composable de CotizacionScreen, pasándole los ViewModels y el ID.
            CotizacionScreen(
                navController = navController,
                autoViewModel = autoViewModel,
                usuarioViewModel = usuarioViewModel,
                autoId = autoId
            )
        }
        composable(Screen.Mapa.route) {
            MapaScreen()
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, usuarioViewModel = usuarioViewModel)
        }
        composable(Screen.ListaUsuarios.route) {
            ListaUsuariosScreen(
                navController = navController,
                usuarioViewModel = usuarioViewModel
            )
        }
        composable(Screen.AgregarAuto.route) {
            AgregarAutoScreen(
                navController = navController,
                autoViewModel = autoViewModel,
                usuarioViewModel = usuarioViewModel
            )
        }
    }
}
