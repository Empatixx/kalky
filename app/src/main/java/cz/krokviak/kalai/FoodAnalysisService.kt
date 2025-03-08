import cz.krokviak.kalai.FoodAnalysisDto
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FoodAnalysisService {
    @POST("/cal")
    fun getAnalysis(@Body image: RequestBody): Call<FoodAnalysisDto>
}