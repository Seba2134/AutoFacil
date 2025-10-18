package com.project.autofacil.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.autofacil.data.AutoDao
import com.project.autofacil.data.AutoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AutoViewModel(private val autoDao: AutoDao) : ViewModel() {

    private val _autos = MutableStateFlow<List<AutoEntity>>(emptyList())
    val autos: StateFlow<List<AutoEntity>> = _autos

    fun cargarAutos() {
        viewModelScope.launch {
            val lista = autoDao.obtenerTodos()
            _autos.value = lista
        }
    }
}
