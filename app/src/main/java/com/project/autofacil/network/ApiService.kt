package com.project.autofacil.network

import com.project.autofacil.Model.AutoApiDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

interface ApiService {
    // Obtener lista de autos
    @GET("autos")
    suspend fun obtenerAutos(): Response<List<AutoApiDto>>

    // (Opcional) Guardar un auto - Para cumplir el requisito de POST
    @POST("autos")
    suspend fun crearAuto(@Body auto: AutoApiDto): Response<AutoApiDto>
}