package com.farmmanagement.app.data.repository

import com.farmmanagement.app.data.db.dao.CropDao
import com.farmmanagement.app.data.db.dao.FarmDao
import com.farmmanagement.app.data.db.dao.MotorDao
import com.farmmanagement.app.data.db.dao.SupervisorDao
import com.farmmanagement.app.data.db.entity.Crop
import com.farmmanagement.app.data.db.entity.Farm
import com.farmmanagement.app.data.db.entity.Motor
import com.farmmanagement.app.data.db.entity.Supervisor
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class NewFarmInput(
    val farmName: String,
    val farmAddress: String,
    val latitude: Double?,
    val longitude: Double?,
    val totalExtent: Double,
    val extentUnit: String,
    val farmPhotoPath: String?,
    val crops: List<Pair<String, Int>>, // plantName to numberOfPlants
    val motors: List<Pair<String, String>>, // motorType to motorHp
    val noSupervisor: Boolean,
    val supervisorName: String,
    val supervisorPhone: String,
    val supervisorSalary: Double,
)

class FarmRepository(
    private val farmDao: FarmDao,
    private val cropDao: CropDao,
    private val motorDao: MotorDao,
    private val supervisorDao: SupervisorDao,
) {
    fun observeFarms(): Flow<List<Farm>> = farmDao.observeAll()
    fun observeFarm(farmId: String): Flow<Farm?> = farmDao.observeByFarmId(farmId)
    fun observeCrops(farmId: String): Flow<List<Crop>> = cropDao.observeForFarm(farmId)
    fun observeMotors(farmId: String): Flow<List<Motor>> = motorDao.observeForFarm(farmId)
    fun observeSupervisor(farmId: String): Flow<Supervisor?> = supervisorDao.observeForFarm(farmId)
    fun observeCropCount(farmId: String): Flow<Int> = cropDao.observeCountForFarm(farmId)
    fun observeMotorCount(farmId: String): Flow<Int> = motorDao.observeCountForFarm(farmId)

    /** Saves Farm + Crops + Motors + Supervisor as one logical unit, scoped by a freshly generated farmId. */
    suspend fun saveNewFarm(input: NewFarmInput): String {
        val farmId = UUID.randomUUID().toString()
        farmDao.insert(
            Farm(
                farmId = farmId,
                farmName = input.farmName,
                farmAddress = input.farmAddress,
                latitude = input.latitude,
                longitude = input.longitude,
                totalExtent = input.totalExtent,
                extentUnit = input.extentUnit,
                farmPhotoPath = input.farmPhotoPath,
            ),
        )
        if (input.crops.isNotEmpty()) {
            cropDao.insertAll(
                input.crops.map { (name, count) -> Crop(farmId = farmId, plantName = name, numberOfPlants = count) },
            )
        }
        if (input.motors.isNotEmpty()) {
            motorDao.insertAll(
                input.motors.map { (type, hp) -> Motor(farmId = farmId, motorType = type, motorHp = hp) },
            )
        }
        if (!input.noSupervisor && input.supervisorName.isNotBlank()) {
            supervisorDao.insert(
                Supervisor(
                    farmId = farmId,
                    supervisorName = input.supervisorName,
                    phoneNumber = input.supervisorPhone,
                    monthlySalary = input.supervisorSalary,
                    noSupervisor = false,
                ),
            )
        }
        return farmId
    }
}
