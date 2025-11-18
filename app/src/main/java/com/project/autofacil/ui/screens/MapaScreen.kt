package com.project.autofacil.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapaScreen() {
    // 1. Ubicación de ejemplo para una sucursal
    val sucursales = remember {
        listOf(
            Triple(LatLng(-33.45694, -70.64827), "AutoFácil Santiago Centro", "Nuestra sucursal principal"),
            // --- INICIO DE LAS NUEVAS SUCURSALES ---
            Triple(LatLng(-33.4316, -70.6112), "AutoFácil Providencia", "Sucursal cerca del Costanera Center"),
            Triple(LatLng(-33.4130, -70.5746), "AutoFácil Las Condes", "Sucursal en Av. Apoquindo")
            // --- FIN DE LAS NUEVAS SUCURSALES ---
        )
    }

    // 2. Estado para controlar la cámara del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-33.43, -70.61), 12f)
    }

    // 3. El Composable de GoogleMap
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        sucursales.forEach { (coordenadas, titulo, descripcion) ->
            Marker(
                state = MarkerState(position = coordenadas),
                title = titulo,
                snippet = descripcion
            )
        }
    }
}