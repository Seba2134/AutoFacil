package com.project.autofacil.network

import com.project.autofacil.Model.Auto // Asegúrate que importe tu clase Auto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // 1. Obtener lista de autos del servidor
    @GET("autos")
    suspend fun obtenerAutos(): Response<List<Auto>>

    // 2. Enviar un auto nuevo (Formulario)
    @POST("autos")
    suspend fun crearAuto(@Body auto: Auto): Response<Auto>
}