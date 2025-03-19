package cz.krokviak.kalai.analytics

import androidx.lifecycle.ViewModel
import cz.krokviak.kalai.detail.FoodDetailState
import cz.krokviak.kalai.home.repo.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AnalyticsViewModel(
    private val FoodRepository: FoodRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState
}