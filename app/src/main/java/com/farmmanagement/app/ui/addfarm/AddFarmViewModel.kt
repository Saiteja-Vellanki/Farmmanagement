package com.farmmanagement.app.ui.addfarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmmanagement.app.data.repository.FarmRepository
import com.farmmanagement.app.data.repository.NewFarmInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SaveResult {
    data class Success(val farmId: String) : SaveResult()
    object ValidationFailed : SaveResult()
}

class AddFarmViewModel(private val repository: FarmRepository) : ViewModel() {

    private val _draft = MutableStateFlow(AddFarmDraft())
    val draft: StateFlow<AddFarmDraft> = _draft.asStateFlow()

    private val _errors = MutableStateFlow(AddFarmErrors())
    val errors: StateFlow<AddFarmErrors> = _errors.asStateFlow()

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    fun update(transform: (AddFarmDraft) -> AddFarmDraft) {
        _draft.value = transform(_draft.value)
    }

    private fun validate(d: AddFarmDraft): AddFarmErrors {
        val cropErrors = mutableMapOf<Int, String>()
        d.crops.forEachIndexed { i, row ->
            val hasAny = row.plantName.isNotBlank() || row.numberOfPlants.isNotBlank()
            if (hasAny) {
                if (row.plantName.isBlank()) cropErrors[i] = "Plant name required"
                else {
                    val n = row.numberOfPlants.toIntOrNull()
                    if (n == null || n < 0) cropErrors[i] = "Enter a valid, non-negative number"
                }
            }
        }
        val motorErrors = mutableMapOf<Int, String>()
        d.motors.forEachIndexed { i, row ->
            val hasAny = row.motorType.isNotBlank() || row.motorHp.isNotBlank()
            if (hasAny && (row.motorType.isBlank() || row.motorHp.isBlank())) {
                motorErrors[i] = "Select both motor type and HP"
            }
        }
        val extentError = when {
            d.totalExtent.isBlank() -> null // optional per spec — only format is validated if provided
            d.totalExtent.toDoubleOrNull() == null -> "Enter a valid number"
            (d.totalExtent.toDoubleOrNull() ?: 0.0) < 0 -> "Must not be negative"
            else -> null
        }
        val phoneError = if (!d.noSupervisor && d.supervisorPhone.isNotBlank() && !d.supervisorPhone.matches(Regex("^[6-9]\\d{9}$"))) {
            "Enter a valid 10-digit mobile number"
        } else null

        return AddFarmErrors(
            farmName = if (d.farmName.isBlank()) "Farm name is required" else null,
            totalExtent = extentError,
            supervisorPhone = phoneError,
            cropRows = cropErrors,
            motorRows = motorErrors,
        )
    }

    fun save(onResult: (SaveResult) -> Unit) {
        val d = _draft.value
        val validation = validate(d)
        _errors.value = validation
        if (validation.hasAny) {
            onResult(SaveResult.ValidationFailed)
            return
        }
        _saving.value = true
        viewModelScope.launch {
            val farmId = repository.saveNewFarm(
                NewFarmInput(
                    farmName = d.farmName.trim(),
                    farmAddress = d.farmAddress.trim(),
                    latitude = d.latitude,
                    longitude = d.longitude,
                    totalExtent = d.totalExtent.toDoubleOrNull() ?: 0.0,
                    extentUnit = d.extentUnit,
                    farmPhotoPath = d.farmPhotoPath,
                    crops = d.crops.filter { it.plantName.isNotBlank() }
                        .map { it.plantName.trim() to (it.numberOfPlants.toIntOrNull() ?: 0) },
                    motors = d.motors.filter { it.motorType.isNotBlank() && it.motorHp.isNotBlank() }
                        .map { it.motorType to it.motorHp },
                    noSupervisor = d.noSupervisor,
                    supervisorName = d.supervisorName.trim(),
                    supervisorPhone = d.supervisorPhone.trim(),
                    supervisorSalary = d.supervisorSalary.toDoubleOrNull() ?: 0.0,
                ),
            )
            _saving.value = false
            onResult(SaveResult.Success(farmId))
        }
    }

    class Factory(private val repository: FarmRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AddFarmViewModel(repository) as T
    }
}
