package com.project.autofacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.autofacil.Model.Auto
import com.project.autofacil.Model.Cliente
import com.project.autofacil.Model.Cotizacion
import com.project.autofacil.ViewModels.MainViewModel
import com.project.autofacil.navigation.NavigationEvent
import com.project.autofacil.navigation.Screen
import com.project.autofacil.ui.screens.HomeScreen
import com.project.autofacil.ui.screens.ProfileScreen
import com.project.autofacil.ui.screens.SettingsScreen
import com.project.autofacil.ui.theme.AutoFacilTheme
import kotlinx.coroutines.flow.collectLatest
import com.project.autofacil.navigation.appNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoFacilTheme {
                Scaffold { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)){
                        appNavigation()
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AutoFacilTheme {
        Greeting("Android")
    }
}