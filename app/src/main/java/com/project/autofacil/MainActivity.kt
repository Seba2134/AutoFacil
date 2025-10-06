package com.project.autofacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.project.autofacil.Model.Auto
import com.project.autofacil.Model.Cliente
import com.project.autofacil.Model.Cotizacion
import com.project.autofacil.ui.theme.AutoFacilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cliente = Cliente(
            idCliente = 1,
            nombre = "Juan Pérez",
            rut = "12.345.678-9",
            telefono = "987654321",
            email = "juanperez@mail.com",
            password = "1234",
            rol = "cliente"
        )

        val auto = Auto(
            id = 1,
            marca = "Toyota",
            modelo = "Corolla",
            anio = 2021,
            precio = 12500000,
            kilometraje = 15000,
            disponible = true,
            fotoUrl = null
        )

        val cotizacion = Cotizacion(
            idCotizacion = 1,
            rutCliente = cliente.rut,
            idAuto = auto.id,
            fecha = System.currentTimeMillis(),
            marcaAuto = "Toyota",
            modeloAuto = "Rav4",
            valoresAuto = "$7.890.000"
        )
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