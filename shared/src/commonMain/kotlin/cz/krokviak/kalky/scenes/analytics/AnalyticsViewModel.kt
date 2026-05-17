package cz.krokviak.kalky.scenes.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.core.common.domain.BuildCaloriesBarsUseCase
import cz.krokviak.kalky.core.common.domain.GetWeightsInRangeUseCase
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AnalyticsViewModel(
    private val buildCaloriesBars: BuildCaloriesBarsUseCase,
    private val getWeightsInRange: GetWeightsInRangeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState

    init {
        observeData()
    }

    fun setStartDate(date: LocalDate) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun setEndDate(date: LocalDate) {
        _uiState.update { it.copy(endDate = date) }
    }

    private fun observeData() {
        viewModelScope.launch {
            _uiState
                .map { it.startDate to it.endDate }
                .distinctUntilChanged()
                .collectLatest { (start, end) -> collectRange(start, end) }
        }
    }

    private suspend fun collectRange(start: LocalDate, end: LocalDate) {
        combine(
            buildCaloriesBars.observe(start, end),
            getWeightsInRange.observe(start, end),
        ) { bars, weights -> bars to weights }
            .collect { (bars, weights) ->
                _uiState.update {
                    it.copy(
                        caloriesBars = bars,
                        weights = weights.toPersistentList(),
                    )
                }
            }
    }
}
