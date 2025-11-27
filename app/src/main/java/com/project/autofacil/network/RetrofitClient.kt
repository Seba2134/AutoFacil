package com.project.autofacil.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitClient {

    // 1. PEGA AQUÍ TU URL REAL DE MOCKAPI (La que empieza con https y termina en /)
    private const val BASE_URL = "https://692517b282b59600d7224d64.mockapi.io/api/v1/"

    // 2. Definimos un cliente "Inseguro" para que el emulador viejo no se queje del HTTPS
    private val unsafeOkHttpClient: OkHttpClient
        get() {
            return try {
                // Crea un gestor de confianza que no valida cadenas de certificados
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })

                // Instala el gestor de confianza
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())

                // Crea el socket factory
                val sslSocketFactory = sslContext.socketFactory

                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true } // Confía en cualquier nombre de host
                    .build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Aquí le enchufamos nuestro cliente "ciego"
            .client(unsafeOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}