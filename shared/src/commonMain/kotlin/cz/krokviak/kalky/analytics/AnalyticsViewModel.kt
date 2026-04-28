package cz.krokviak.kalky.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.common.domain.BuildCaloriesBarsUseCase
import cz.krokviak.kalky.common.domain.GetWeightsInRangeUseCase
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AnalyticsViewModel(
    private val buildCaloriesBars: BuildCaloriesBarsUseCase,
    private val getWeightsInRange: GetWeightsInRangeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState

    private var loadJob: Job? = null

    init {
        loadData()
    }

    fun setStartDate(date: LocalDate) {
        _uiState.update { it.copy(startDate = date) }
        loadData()
    }

    fun setEndDate(date: LocalDate) {
        _uiState.update { it.copy(endDate = date) }
        loadData()
    }

    private fun loadData() {
        val state = _uiState.value
        val start = state.startDate
        val end = state.endDate

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val bars = buildCaloriesBars(start, end)
            val weights = getWeightsInRange(start, end)
            _uiState.update {
                it.copy(
                    weights = weights.toPersistentList(),
                    caloriesBars = bars,
                )
            }
        }
    }
}
