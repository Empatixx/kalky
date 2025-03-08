package cz.krokviak.kalai.camera.clients

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080"

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    val instance: FoodAnalysisService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build()
            .create(FoodAnalysisService::class.java)
    }
}

