package com.project.autofacil.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.autofacil.Model.Auto
import com.project.autofacil.data.AutoDao
import com.project.autofacil.data.AutoEntity
import com.project.autofacil.network.RetrofitClient // IMPORTANTE: Importar tu cliente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AutoViewModel(private val autoDao: AutoDao) : ViewModel() {

    private val _autos = MutableStateFlow<List<AutoEntity>>(emptyList())
    val autos: StateFlow<List<AutoEntity>> = _autos

    private val _autoSeleccionado = MutableStateFlow<Auto?>(null)
    val autoSeleccionado: StateFlow<Auto?> = _autoSeleccionado

    // Estado para mostrar carga en la UI (Opcional pero recomendado)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        cargarAutos() // Cargar datos al iniciar
    }

    // --- MODIFICADO: LÓGICA DE SINCRONIZACIÓN ---
    fun cargarAutos() {
        viewModelScope.launch {
            _isLoading.value = true

            // 1. Cargar lo que ya existe en LOCAL (Room) para mostrar algo rápido
            val listaLocal = autoDao.obtenerTodos()
            _autos.value = listaLocal

            try {
                // 2. Intentar descargar datos frescos de MOCKAPI
                val response = RetrofitClient.apiService.obtenerAutos()

                if (response.isSuccessful) {
                    val listaRemota = response.body() ?: emptyList()

                    // 3. Si hay internet, actualizamos la base de datos local
                    // Convertimos de Auto (API) a AutoEntity (BD)
                    val entidades = listaRemota.map { autoApi ->
                        AutoEntity(
                            id = autoApi.id.toString().toIntOrNull() ?: 0, // Ojo: Si MockAPI manda String y Entity es Int, aquí habrá que convertir
                            marca = autoApi.marca,
                            modelo = autoApi.modelo,
                            anio = autoApi.anio,
                            precio = autoApi.precio,
                            kilometraje = autoApi.kilometraje,
                            fotoId = autoApi.fotoId,
                            fotoUri = autoApi.fotoUri // Asegúrate que AutoEntity tenga este campo
                        )
                    }

                    // Guardamos en Room (necesitas un método insertAll o un loop)
                    for (entidad in entidades) {
                        autoDao.insertar(entidad) // Esto actualizará los datos si hay conflicto (OnConflictStrategy.REPLACE)
                    }

                    // 4. Refrescamos la UI con la nueva verdad de la BD
                    _autos.value = autoDao.obtenerTodos()
                }
            } catch (e: Exception) {
                // Si falla internet, no pasa nada, el usuario sigue viendo los datos locales
                println("Error de conexión: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAutoById(autoId: String?) {
        viewModelScope.launch {
            try {
                val idNumerico = autoId?.toIntOrNull() ?: return@launch
                val autoEntity = _autos.value.find { it.id == idNumerico }

                val autoModel = autoEntity?.let {
                    Auto(
                        id = it.id,
                        marca = it.marca,
                        modelo = it.modelo,
                        anio = it.anio,
                        precio = it.precio,
                        fotoId = it.fotoId,
                        kilometraje = it.kilometraje,
                        fotoUri = it.fotoUri // No olvides mapear esto si existe
                    )
                }
                _autoSeleccionado.value = autoModel
            } catch (e: Exception) {
                _autoSeleccionado.value = null
            }
        }
    }

    // --- MODIFICADO: POST A API ---
    fun agregarAuto(autoEntity: AutoEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Convertir Entity a Modelo API
                val nuevoAutoApi = Auto(
                    marca = autoEntity.marca,
                    modelo = autoEntity.modelo,
                    anio = autoEntity.anio,
                    precio = autoEntity.precio,
                    kilometraje = autoEntity.kilometraje,
                    fotoId = autoEntity.fotoId,
                    fotoUri = autoEntity.fotoUri
                    // No mandamos ID porque MockAPI lo crea
                )

                // 2. Enviar a MockAPI
                val response = RetrofitClient.apiService.crearAuto(nuevoAutoApi)

                if (response.isSuccessful) {
                    // 3. Si el servidor respondió OK, guardamos en local
                    // Usamos el objeto que nos devolvió el servidor (que ya tiene el ID real)
                    val autoCreado = response.body()

                    if (autoCreado != null) {
                        val entidadGuardar = autoEntity.copy(
                            id = autoCreado.id // Usamos el ID del servidor si es posible sincronizar tipos
                        )
                        autoDao.insertar(entidadGuardar)
                    } else {
                        // Fallback por si body es null
                        autoDao.insertar(autoEntity)
                    }

                    // Actualizamos lista
                    cargarAutos()
                } else {
                    // Si el servidor falla, ¿guardamos local solo? (Decisión de diseño)
                    // Para el examen, podrías guardar localmente igual.
                    autoDao.insertar(autoEntity)
                    _autos.value = autoDao.obtenerTodos()
                }

            } catch (e: Exception) {
                println("Error al subir auto: ${e.message}")
                // Sin internet: Guardamos local para que no se pierda
                autoDao.insertar(autoEntity)
                _autos.value = autoDao.obtenerTodos()
            } finally {
                _isLoading.value = false
            }
        }
    }
}