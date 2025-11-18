package com.project.autofacil.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.autofacil.Model.Cliente
import com.project.autofacil.Model.UsuarioErrores
import com.project.autofacil.Model.UsuarioUiState
import com.project.autofacil.data.UsuarioDao
import com.project.autofacil.data.UsuarioEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsuarioViewModel(
    private val usuarioDao: UsuarioDao
) : ViewModel() {

    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado
    private val _cliente = MutableStateFlow<Cliente?>(null)
    private val _usuarioActual = MutableStateFlow<UsuarioEntity?>(null)
    val usuarioActual: StateFlow<UsuarioEntity?> = _usuarioActual.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

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
            // 1) Verificar si ya existe un usuario con ese correo
            val usuarioExistente = usuarioDao.obtenerPorCorreo(correo)

            if (usuarioExistente != null) {
                // Ya existe → actualizar errores en el estado para mostrar en la UI
                _estado.update { estadoActual ->
                    estadoActual.copy(
                        errores = estadoActual.errores.copy(
                            correo = "Ya existe un usuario registrado con este correo"
                        )
                    )
                }
                return@launch
            }

            // 2) Si no existe, creamos el usuario normalmente
            val nuevoUsuario = UsuarioEntity(
                nombre = nombre,
                correo = correo,
                contrasena = contrasena,
                direccion = direccion,
                rol = "cliente"
            )
            try {
                val nuevoId = usuarioDao.registrar(nuevoUsuario)

                _cliente.value = Cliente(
                    idCliente = nuevoId.toInt(),
                    nombre = nombre,
                    rut = "Sin RUT",
                    telefono = "Sin Teléfono",
                    email = correo,
                    password = contrasena,
                    rol = "cliente"
                )
            } catch (e: Exception) {
                // Por si hubo conflicto u otro error inesperado
                _estado.update { estadoActual ->
                    estadoActual.copy(
                        errores = estadoActual.errores.copy(
                            correo = "Error al registrar el usuario"
                        )
                    )
                }
            }
        }
    }
    fun iniciarSesion(correo: String, contrasena: String) {
        viewModelScope.launch {
            val usuario = usuarioDao.login(correo, contrasena)

            if (usuario != null) {
                _usuarioActual.value = usuario
                _loginError.value = null

                // También rellenamos Cliente para reutilizarlo donde ya se usa
                _cliente.value = Cliente(
                    idCliente = usuario.id,
                    nombre = usuario.nombre,
                    rut = "Sin RUT",
                    telefono = "Sin Teléfono",
                    email = usuario.correo,
                    password = usuario.contrasena,
                    rol = usuario.rol
                )
            } else {
                _usuarioActual.value = null
                _loginError.value = "Correo o contraseña incorrectos"
            }
        }
    }
    val todosLosUsuarios: StateFlow<List<UsuarioEntity>> = usuarioDao.obtenerTodos()
        .stateIn(
            scope = viewModelScope, // El ciclo de vida del ViewModel
            started = SharingStarted.WhileSubscribed(5000L), // Empieza a escuchar 5s después de que la UI deja de hacerlo
            initialValue = emptyList() // El valor inicial mientras se cargan los datos
        )

    fun cerrarSesion() {
        _cliente.value = null
        _usuarioActual.value = null
        _loginError.value = null
    }

}
