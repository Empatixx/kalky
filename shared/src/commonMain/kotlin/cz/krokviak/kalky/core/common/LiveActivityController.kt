package cz.krokviak.kalky.core.common

interface LiveActivityController {
    fun startFoodAnalysis(id: Long)
    fun completeFoodAnalysis(id: Long, name: String, calories: Int)
    fun failFoodAnalysis(id: Long)
}

class NoOpLiveActivityController : LiveActivityController {
    override fun startFoodAnalysis(id: Long) {}
    override fun completeFoodAnalysis(id: Long, name: String, calories: Int) {}
    override fun failFoodAnalysis(id: Long) {}
}
