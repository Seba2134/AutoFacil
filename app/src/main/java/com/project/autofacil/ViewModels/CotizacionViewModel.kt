package com.project.autofacil.ViewModels


import com.project.autofacil.data.CotizacionDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.project.autofacil.data.CotizacionEntity
import com.project.autofacil.data.UsuarioEntity // Asumiendo que el cliente logueado se representa con UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel para gestionar la lógica de negocio de la pantalla de cotizaciones.
 *
 * @param cotizacionDao El objeto de acceso a datos para las cotizaciones.
 * @param usuarioLogueado El usuario que está realizando la cotización.
 */
class CotizacionViewModel(
    private val cotizacionDao: CotizacionDao,
    private val usuarioLogueado: UsuarioEntity // Se inyecta el usuario actual
) : ViewModel() {

    // Estado para la UI: marca, modelo y año del vehículo
    private val _marca = MutableStateFlow("")
    val marca = _marca.asStateFlow()

    private val _modelo = MutableStateFlow("")
    val modelo = _modelo.asStateFlow()

    private val _anio = MutableStateFlow("")
    val anio = _anio.asStateFlow()

    private val _tipoMantenimiento = MutableStateFlow("")
    val tipoMantenimiento = _tipoMantenimiento.asStateFlow()

    // Estado para comunicar el resultado a la pantalla
    private val _estadoGuardado = MutableStateFlow<EstadoCotizacion>(EstadoCotizacion.Inactivo)
    val estadoGuardado = _estadoGuardado.asStateFlow()

    // Funciones para que la UI actualice los estados
    fun onMarcaChange(nuevaMarca: String) {
        _marca.value = nuevaMarca
    }

    fun onModeloChange(nuevoModelo: String) {
        _modelo.value = nuevoModelo
    }

    fun onAnioChange(nuevoAnio: String) {
        // Permitir solo números y máximo 4 dígitos
        if (nuevoAnio.all { it.isDigit() } && nuevoAnio.length <= 4) {
            _anio.value = nuevoAnio
        }
    }

    fun onMantenimientoChange(nuevoMantenimiento: String) {
        _tipoMantenimiento.value = nuevoMantenimiento
    }

    /**
     * Valida los datos, calcula el costo y guarda la cotización en la base de datos.
     */
    fun guardarCotizacion() {
        // Validaciones básicas
        if (_marca.value.isBlank() || _modelo.value.isBlank() || _anio.value.length != 4) {
            _estadoGuardado.value = EstadoCotizacion.Error("Por favor, complete todos los campos correctamente.")
            return
        }

        viewModelScope.launch {
            try {
                val costo = calcularCosto(_tipoMantenimiento.value)
                val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                val nuevaCotizacion = CotizacionEntity(
                    // idCotizacion es autogenerado por Room
                    idCliente = usuarioLogueado.id,
                    marcaVehiculo = _marca.value,
                    modeloVehiculo = _modelo.value,
                    anioVehiculo = _anio.value.toInt(),
                    tipoMantenimiento = _tipoMantenimiento.value,
                    fecha = fechaActual,
                    costoEstimado = costo
                )

                cotizacionDao.guardarCotizacion(nuevaCotizacion)
                _estadoGuardado.value = EstadoCotizacion.Exito(costo) // Se guardó con éxito

            } catch (e: Exception) {
                // Captura de cualquier error durante la inserción en la BD
                _estadoGuardado.value = EstadoCotizacion.Error("Ocurrió un error al guardar: ${e.message}")
            }
        }
    }

    /**
     * Calcula el costo estimado basado en el tipo de mantenimiento.
     */
    private fun calcularCosto(tipoMantenimiento: String): Double {
        return when (tipoMantenimiento) {
            "Cambio de Aceite" -> 55000.0
            "Revisión de Frenos" -> 80000.0
            "Alineación y Balanceo" -> 65000.0
            "Revisión General" -> 120000.0
            else -> 40000.0 // Un costo por defecto si no coincide
        }
    }

    /**
     * Reinicia el estado a Inactivo, para poder realizar otra cotización
     * o cerrar un diálogo de alerta.
     */
    fun reiniciarEstado() {
        _estadoGuardado.value = EstadoCotizacion.Inactivo
    }
}

/**
 * Clase sellada para representar los diferentes estados del proceso de guardado.
 * Esto es más robusto que un simple Boolean.
 */
sealed class EstadoCotizacion {
    data object Inactivo : EstadoCotizacion()
    data class Exito(val costo: Double) : EstadoCotizacion()
    data class Error(val mensaje: String) : EstadoCotizacion()
}
