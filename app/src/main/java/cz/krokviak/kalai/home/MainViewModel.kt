package cz.krokviak.kalai.home

import androidx.lifecycle.ViewModel
import cz.krokviak.kalai.camera.entities.FoodItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    fun onBottomNavItemSelected(index: Int) {
        _uiState.update { it.copy(selectedBottomNavItem = index) }
    }
    // add on first position
    fun addFoodItem(foodItem: FoodItemEntity) {
        _uiState.update { current ->
            current.copy(
                recentlyAddedItems = listOf(foodItem) + current.recentlyAddedItems
            )
        }
    }
}
