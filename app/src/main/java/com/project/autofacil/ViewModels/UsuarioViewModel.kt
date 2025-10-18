package com.project.autofacil.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.autofacil.Model.Cliente
import com.project.autofacil.Model.UsuarioErrores
import com.project.autofacil.Model.UsuarioUiState
import com.project.autofacil.data.UsuarioDao
import com.project.autofacil.data.UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsuarioViewModel(
    private val usuarioDao: UsuarioDao
) : ViewModel() {

    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado
    private val _cliente = MutableStateFlow<Cliente?>(null)

    // Propiedad pública de solo lectura para que las pantallas (Vistas) la observen.
    // Esta es la propiedad 'cliente' que resuelve tu error.
    val cliente: StateFlow<Cliente?> = _cliente.asStateFlow()

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onAceptacionChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            nombre = if (estadoActual.nombre.isBlank()) "Campo Obligatorio" else null,
            correo = if (!estadoActual.correo.contains("@") || !estadoActual.correo.contains(".")) "Correo Invalido" else null,
            clave = if (estadoActual.clave.length < 6) "Debe tener al menos 6 caracteres" else null,
            direccion = if (estadoActual.direccion.isBlank()) "Campo obligatorio" else null
        )
        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.clave,
            errores.direccion
        ).isNotEmpty()
        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }

    fun registrarUsuario(nombre: String, correo: String, contrasena: String, direccion: String) {
        viewModelScope.launch {
            val nuevoUsuario = UsuarioEntity(
                nombre = nombre,
                correo = correo,
                contrasena = contrasena,
                direccion = direccion
            )
            // Inserta en la base de datos y obtiene el ID generado
            val nuevoId = usuarioDao.registrar(nuevoUsuario)

            _cliente.value = Cliente(
                idCliente = nuevoId.toInt(),
                nombre = nombre,
                rut = "Sin RUT",      // Usa el nombre de parámetro correcto
                telefono = "Sin Teléfono", // Usa el nombre de parámetro correcto
                email = correo,       // Usa el nombre de parámetro correcto
                password = contrasena,// Usa el nombre de parámetro correcto
                rol = "cliente"
            )
        }
    }
}
