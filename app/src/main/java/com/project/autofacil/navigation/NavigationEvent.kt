package com.project.autofacil.navigation

import android.health.connect.datatypes.ExerciseRoute

sealed class NavigationEvent {

    data class NavigateTo(
        val route: Screen,
        val popUpToRoute : Screen? = null,
        val inclusive : Boolean = false,
        val singleTop : Boolean = false
    ): NavigationEvent()

    //Volver a la pantalla anterior
    object PopBackStack : NavigationEvent()

    //navegar hacia arriba en la jerarquia de la aplicacion
    object NavigateUp : NavigationEvent()
}