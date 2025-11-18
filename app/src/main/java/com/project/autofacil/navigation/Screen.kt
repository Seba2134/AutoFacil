package com.project.autofacil.navigation

sealed class Screen(val route: String) {

    data object Home: Screen("home_page")

    data object Profile : Screen("profile_page")

    data object Settings : Screen("settings_page")
    data object Registro: Screen("Registro")
    data object Resumen: Screen("Resumen")

    data object AutosScreen: Screen("AutosScreen")
    data object AutoDetail : Screen("auto_detail_screen/{autoId}") {
        fun createRoute(autoId: String) = "auto_detail_screen/$autoId"
    }
    data object CotizacionScreen: Screen("cotizacion_screen/{autoId}") {
        fun createRoute(autoId: String) = "cotizacion_screen/$autoId"
    }

    data object Mapa : Screen("mapa_screen")
    data object Login : Screen("login_page")
    data object ListaUsuarios : Screen("lista_usuarios")
    data object AgregarAuto : Screen("agregar_auto")

}