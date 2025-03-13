package cz.krokviak.kalai.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import cz.krokviak.kalai.camera.clients.FoodAnalysisService
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.115:8080"

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    val instance: FoodAnalysisService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build()
            .create(FoodAnalysisService::class.java)
    }
}

