package com.project.autofacil.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Representa la tabla 'cotizaciones' en la base de datos.
 *
 * @property idCotizacion La clave primaria de la tabla, se autogenera.
 * @property idCliente La clave foránea que vincula esta cotización con un usuario en la tabla 'usuarios'.
 * @property marcaVehiculo La marca del vehículo cotizado.
 * @property modeloVehiculo El modelo del vehículo cotizado.
 * @property anioVehiculo El año de fabricación del vehículo.
 * @property tipoMantenimiento El tipo de servicio o mantenimiento solicitado (ej: "Cambio de Aceite").
 * @property fecha La fecha en que se realizó la cotización (guardada como texto).
 * @property costoEstimado El valor calculado para la cotización.
 */
@Entity(
    tableName = "cotizaciones",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["idCliente"],
            onDelete = ForeignKey.CASCADE // Si se borra un usuario, se borran sus cotizaciones.
        )
    ]
)
data class CotizacionEntity(
    @PrimaryKey(autoGenerate = true)
    val idCotizacion: Int = 0,
    val idCliente: Int,
    val marcaVehiculo: String,
    val modeloVehiculo: String,
    val anioVehiculo: Int,
    val tipoMantenimiento: String,
    val fecha: String,
    val costoEstimado: Double
)
