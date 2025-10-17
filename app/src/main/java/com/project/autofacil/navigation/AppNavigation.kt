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

@Composable
fun appNavigation(usuarioViewModel: UsuarioViewModel, autoViewModel: AutoViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            AutosScreen(autoViewModel = autoViewModel)
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
    }
}
