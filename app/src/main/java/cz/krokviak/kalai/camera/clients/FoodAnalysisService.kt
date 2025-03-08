package cz.krokviak.kalai.camera.clients

import cz.krokviak.kalai.camera.data.FoodAnalysisDto
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FoodAnalysisService {
    @POST("/cal")
    fun getAnalysis(@Body image: RequestBody): Call<FoodAnalysisDto>
}