// AnalyticsViewModel.kt
package cz.krokviak.kalai.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalai.common.repo.FoodRepository
import cz.krokviak.kalai.common.repo.PersonalInfoRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnalyticsViewModel(
    private val foodRepository: FoodRepository,
    private val personalInfoRepo: PersonalInfoRepo
) : ViewModel() {
    fun updateRange(analyticsRange: AnalyticsRange) {
        _uiState.update {
            it.copy(selectedRange = analyticsRange)
        }
    }

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState

    init {
        // Load both the nutrient bars & the weights in parallel
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val bars = foodRepository.getLast7DaysBars()
                val weights = personalInfoRepo.getWeightsForLast7Days()

                _uiState.update {
                    it.copy(
                        weights = weights,
                        caloriesBars = bars
                    )
                }
            }
        }
    }
}
