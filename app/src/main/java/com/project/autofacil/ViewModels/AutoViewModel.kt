package com.project.autofacil.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.autofacil.Model.Auto
import com.project.autofacil.R
import com.project.autofacil.data.AutoDao
import com.project.autofacil.data.AutoEntity
import com.project.autofacil.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AutoViewModel(private val autoDao: AutoDao) : ViewModel() {

    // 1. CAMBIO PRINCIPAL: Conectamos la variable directamente a la Base de Datos
    // Ya no es un MutableStateFlow, es un Flow directo desde el DAO.
    val autos: Flow<List<AutoEntity>> = autoDao.obtenerTodos()

    private val _autoSeleccionado = MutableStateFlow<Auto?>(null)
    val autoSeleccionado: StateFlow<Auto?> = _autoSeleccionado

    // Nota: Hemos borrado la función 'cargarAutos()' porque ya no es necesaria.
    // El Flow de arriba mantiene la lista siempre actualizada.

    // 2. Función para descargar de la API y guardar en la BD
    fun cargarAutosDesdeApi() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("API", "Iniciando descarga desde MockAPI...")
                val respuesta = RetrofitClient.apiService.obtenerAutos()

                if (respuesta.isSuccessful) {
                    val listaApi = respuesta.body() ?: emptyList()

                    val listaParaGuardar = listaApi.map { autoDto ->

                        // Limpieza del Kilometraje (quitamos "km" y comas)
                        val kmsLimpiosString = autoDto.kilometraje.filter { it.isDigit() }
                        val kmsInt = kmsLimpiosString.toIntOrNull() ?: 0

                        AutoEntity(
                            id = autoDto.id.toIntOrNull() ?: 0,
                            marca = autoDto.marca,
                            modelo = autoDto.modelo,
                            // Convertimos el año a String y luego a Int para evitar errores
                            anio = autoDto.anio.toString().toIntOrNull() ?: 2024,
                            precio = autoDto.precio,
                            kilometraje = kmsInt,
                            // Ponemos una foto por defecto
                            fotoId = R.drawable.ic_launcher_background
                        )
                    }

                    // Guardamos en la Base de Datos (Esto disparará la actualización de la pantalla)
                    listaParaGuardar.forEach { auto ->
                        autoDao.insertarAuto(auto)
                    }
                    Log.d("API", "Datos guardados. La UI debería actualizarse ahora.")
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
            }
        }
    }

    // 3. Función actualizada para buscar un auto específico usando el Flow
    fun getAutoById(autoId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val idNumerico = autoId?.toIntOrNull() ?: return@launch

            // Tomamos una "foto" instantánea de la lista actual usando .first()
            val listaActual = autos.first()
            val autoEntity = listaActual.find { it.id == idNumerico }

            val autoModel = autoEntity?.let {
                Auto(
                    id = it.id,
                    marca = it.marca,
                    modelo = it.modelo,
                    anio = it.anio,
                    precio = it.precio,
                    fotoId = it.fotoId,
                    kilometraje = it.kilometraje
                )
            }
            _autoSeleccionado.value = autoModel
        }
    }
}