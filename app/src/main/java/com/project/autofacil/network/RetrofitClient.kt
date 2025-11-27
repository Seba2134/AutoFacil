package com.project.autofacil.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {

    // TU URL DE MOCKAPI (Verifica que sea la correcta)
    private const val BASE_URL = "https://692517b282b59600d7224d64.mockapi.io/api/v1/"

    // Cliente HTTP "Inseguro" que ignora errores de certificado SSL
    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            // Crea un gestor de confianza que no valida nada
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // Instala el gestor de confianza
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Crea la fábrica de sockets SSL
            val sslSocketFactory = sslContext.socketFactory

            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true } // Confía en cualquier nombre de host
                .addInterceptor(logging)
                .build()

        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getUnsafeOkHttpClient()) // <--- AQUÍ USAMOS EL CLIENTE HACKEADO
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}