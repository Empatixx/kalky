package cz.krokviak.kalai.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalai.camera.entities.FoodItemEntity
import cz.krokviak.kalai.common.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

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
        recaculateMicros()
    }

    fun recaculateMicros() {
        _uiState.update { current ->
            val totalCalories = current.recentlyAddedItems.sumBy { it.calories }
            val totalProtein = current.recentlyAddedItems.sumBy { it.protein }
            val totalFats = current.recentlyAddedItems.sumBy { it.fat }
            val totalCarbs = current.recentlyAddedItems.sumBy { it.carbs }

            current.copy(
                currentCalories = totalCalories,
                currentProtein = totalProtein,
                currentFats = totalFats,
                currentCarbs = totalCarbs
            )
        }
    }

    fun loadFoodItemsForDate(date: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            val dao = DatabaseProvider.instance.foodItemDao()

            val itemsForDate = dao.getFoodItemsForDate(dateStr)

            val totalCalories = dao.getTotalCaloriesForDate(dateStr) ?: 0
            val totalFats = dao.getTotalFatsForDate(dateStr) ?: 0
            val totalCarbs = dao.getTotalCarbsForDate(dateStr) ?: 0
            val totalProtein = dao.getTotalProteinForDate(dateStr) ?: 0

            _uiState.update { current ->
                current.copy(
                    recentlyAddedItems = itemsForDate,
                    currentCalories = totalCalories,
                    currentFats = totalFats,
                    currentCarbs = totalCarbs,
                    currentProtein = totalProtein
                )
            }
        }
    }
}
