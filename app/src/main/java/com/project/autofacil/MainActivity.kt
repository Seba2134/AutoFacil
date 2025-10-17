package com.project.autofacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.project.autofacil.ui.theme.AutoFacilTheme
import com.project.autofacil.navigation.appNavigation
import androidx.room.Room
import com.project.autofacil.data.AppDatabase
import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.ViewModels.UsuarioViewModelFactory
import androidx.lifecycle.ViewModelProvider


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "autofacil_db"
        ).build()
        val usuarioDao = db.usuarioDao()
        val factory = UsuarioViewModelFactory(usuarioDao)
        val usuarioViewModel = ViewModelProvider(this, factory)[UsuarioViewModel::class.java]

        setContent {
            AutoFacilTheme {
                Scaffold { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)){
                        appNavigation(usuarioViewModel)
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