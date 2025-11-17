package com.project.autofacil.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.autofacil.Model.Auto
import com.project.autofacil.data.AutoDao
import com.project.autofacil.data.AutoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AutoViewModel(private val autoDao: AutoDao) : ViewModel() {

    private val _autos = MutableStateFlow<List<AutoEntity>>(emptyList())
    val autos: StateFlow<List<AutoEntity>> = _autos

    private val _autoSeleccionado = MutableStateFlow<Auto?>(null)
    val autoSeleccionado: StateFlow<Auto?> = _autoSeleccionado
    fun cargarAutos() {
        viewModelScope.launch {
            val lista = autoDao.obtenerTodos()
            _autos.value = lista
        }
    }
    fun getAutoById(autoId: String?) {
        viewModelScope.launch {
            try {
                val idNumerico = autoId?.toIntOrNull() ?: return@launch
                val autoEntity = _autos.value.find { it.id == idNumerico }

                val autoModel = autoEntity?.let {
                    // --- INICIO DE LA CORRECCIÓN ---
                    Auto(
                        // Pasa el 'id' (Int) directamente, sin convertirlo a String
                        id = it.id,
                        marca = it.marca,
                        modelo = it.modelo,
                        anio = it.anio,
                        precio = it.precio,
                        fotoId = it.fotoId,
                        kilometraje = it.kilometraje
                    )
                    // --- FIN DE LA CORRECCIÓN ---
                }
                _autoSeleccionado.value = autoModel
            } catch (e: Exception) {
                _autoSeleccionado.value = null
            }
        }
    }

}
