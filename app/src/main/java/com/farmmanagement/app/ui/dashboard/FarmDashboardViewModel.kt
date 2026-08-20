package com.farmmanagement.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmmanagement.app.data.db.entity.Crop
import com.farmmanagement.app.data.db.entity.Farm
import com.farmmanagement.app.data.repository.FarmRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class FarmDashboardUiState(
    val loading: Boolean = true,
    val farm: Farm? = null,
    val crops: List<Crop> = emptyList(),
)

/** Management module keys shown as placeholder cards — real functionality lands in later phases. */
val MANAGEMENT_MODULES = listOf(
    "Workers", "Fertilizer", "Spraying", "Irrigation", "Machinery",
    "Purchase", "Harvest", "Store", "Expenses",
)

class FarmDashboardViewModel(
    repository: FarmRepository,
    farmId: String,
) : ViewModel() {

    val uiState: StateFlow<FarmDashboardUiState> =
        combine(repository.observeFarm(farmId), repository.observeCrops(farmId)) { farm, crops ->
            FarmDashboardUiState(loading = false, farm = farm, crops = crops)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FarmDashboardUiState())

    class Factory(private val repository: FarmRepository, private val farmId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FarmDashboardViewModel(repository, farmId) as T
    }
}
