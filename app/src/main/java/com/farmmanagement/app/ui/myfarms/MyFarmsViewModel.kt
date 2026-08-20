package com.farmmanagement.app.ui.myfarms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmmanagement.app.data.db.entity.Farm
import com.farmmanagement.app.data.repository.FarmRepository
import com.farmmanagement.app.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MyFarmsUiState(
    val loading: Boolean = true,
    val userName: String? = null,
    val farms: List<Farm> = emptyList(),
)

class MyFarmsViewModel(
    farmRepository: FarmRepository,
    userProfileRepository: UserProfileRepository,
) : ViewModel() {

    val uiState: StateFlow<MyFarmsUiState> =
        combine(farmRepository.observeFarms(), userProfileRepository.observeUserName()) { farms, name ->
            MyFarmsUiState(loading = false, userName = name, farms = farms)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MyFarmsUiState())

    class Factory(
        private val farmRepository: FarmRepository,
        private val userProfileRepository: UserProfileRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MyFarmsViewModel(farmRepository, userProfileRepository) as T
    }
}
