package com.project.autofacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.Model.Auto
import com.project.autofacil.Model.Cliente
import java.text.NumberFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CotizacionScreen(
    navController: NavController,
    autoViewModel: AutoViewModel,
    usuarioViewModel: UsuarioViewModel,
    autoId: String?
) {
    // 2. OBSERVAMOS EL AUTO Y EL CLIENTE ACTIVO
    val auto by autoViewModel.autoSeleccionado.collectAsState()
    // Usamos 'cliente' porque así parece estar definido en tu ViewModel
    val cliente by usuarioViewModel.cliente.collectAsState()

    // Nos aseguramos de tener la información del auto
    LaunchedEffect(autoId) {
        if (autoId != null) {
            autoViewModel.getAutoById(autoId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cotización") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Mostramos un indicador de carga hasta que tengamos el auto y el cliente
            if (auto == null || cliente == null || auto?.id.toString() != autoId) {
                CircularProgressIndicator()
            } else {
                // Una vez que tenemos todo, mostramos el contenido
                auto?.let { autoActual ->
                    cliente?.let { clienteActual ->
                        // 3. PASAMOS EL CLIENTE AL CONTENIDO
                        ContenidoCotizacion(auto = autoActual, cliente = clienteActual)
                    }
                }
            }
        }
    }
}

@Composable
// 4. CAMBIAMOS EL PARÁMETRO DE 'usuario' A 'cliente'
fun ContenidoCotizacion(auto: Auto, cliente: Cliente) {
    val precioFinanciado = auto.precio * 0.90 // Descuento del 10%

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    currencyFormat.maximumFractionDigits = 0

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Usamos los datos del cliente
        Text("¡Felicitaciones, ${cliente.nombre}!", style = MaterialTheme.typography.headlineSmall)
        Text("Has cotizado el siguiente vehículo:", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                // Usamos las propiedades de tu modelo Cliente
                InfoLinea("Cliente:", cliente.nombre) // Suponiendo que el cliente tiene 'nombre'
                InfoLinea("Email:", cliente.email)   // Suponiendo que el cliente tiene 'correo'
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                InfoLinea("Vehículo:", "${auto.marca} ${auto.modelo}")
                InfoLinea("Año:", auto.anio.toString())
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                InfoLinea("Precio Lista:", currencyFormat.format(auto.precio))
                InfoLinea(
                    texto = "Precio con Financiamiento:",
                    valor = currencyFormat.format(precioFinanciado),
                    isHighlight = true
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Un ejecutivo se pondrá en contacto contigo.",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

// Esta función auxiliar no necesita cambios
@Composable
fun InfoLinea(texto: String, valor: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else LocalContentColor.current
        )
    }
}
