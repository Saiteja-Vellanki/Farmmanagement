package com.farmmanagement.app.ui.addfarm

data class CropRow(val plantName: String = "", val numberOfPlants: String = "")
data class MotorRow(val motorType: String = "", val motorHp: String = "")

data class AddFarmDraft(
    val farmName: String = "",
    val farmAddress: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val totalExtent: String = "",
    val extentUnit: String = "Acres",
    val crops: List<CropRow> = emptyList(),
    val motors: List<MotorRow> = emptyList(),
    val farmPhotoPath: String? = null,
    val noSupervisor: Boolean = false,
    val supervisorName: String = "",
    val supervisorPhone: String = "",
    val supervisorSalary: String = "",
)

data class AddFarmErrors(
    val farmName: String? = null,
    val totalExtent: String? = null,
    val supervisorPhone: String? = null,
    val cropRows: Map<Int, String> = emptyMap(),
    val motorRows: Map<Int, String> = emptyMap(),
) {
    val hasAny: Boolean
        get() = farmName != null || totalExtent != null || supervisorPhone != null ||
            cropRows.isNotEmpty() || motorRows.isNotEmpty()
}

val EXTENT_UNITS = listOf("Acres", "Cents", "Gajalu / Sq. Yards", "Sq. Feet", "Guntas", "Hectares")

val MOTOR_TYPES = listOf("Submersible Motor", "Bore Well Motor", "Pond Motor", "Fuel Motor", "Lift Irrigation")

val MOTOR_HP_OPTIONS = listOf("1 HP", "2 HP", "3 HP", "5 HP", "7.5 HP", "10 HP", "12.5 HP", "15 HP", "20 HP", "25 HP", "30 HP")
