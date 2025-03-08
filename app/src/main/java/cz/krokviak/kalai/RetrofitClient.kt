package cz.krokviak.kalai

import FoodAnalysisService
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://localhost:8080"

    val instance: FoodAnalysisService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
            .create(FoodAnalysisService::class.java)
    }
}
