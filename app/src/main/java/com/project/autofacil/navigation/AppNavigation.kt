package com.project.autofacil.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.ui.screens.HomeScreen
import com.project.autofacil.ui.screens.registroScreen
import com.project.autofacil.ui.screens.resumenScreen


@Composable
fun appNavigation(){


    val navController = rememberNavController()

    val usuarioViewModel: UsuarioViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ){
        composable(Screen.Home.route){
            HomeScreen(navController)
        }
        composable(Screen.Registro.route){
            registroScreen(navController, usuarioViewModel)
        }
        composable(Screen.Resumen.route){
            resumenScreen(usuarioViewModel, navController)
        }
    }

}