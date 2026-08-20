package com.farmmanagement.app.di

import android.content.Context
import com.farmmanagement.app.data.db.AppDatabase
import com.farmmanagement.app.data.location.LocationHelper
import com.farmmanagement.app.data.photo.PhotoStorage
import com.farmmanagement.app.data.repository.FarmRepository
import com.farmmanagement.app.data.repository.UserProfileRepository

class AppContainer(context: Context) {
    private val db = AppDatabase.getInstance(context)

    val farmRepository: FarmRepository by lazy {
        FarmRepository(db.farmDao(), db.cropDao(), db.motorDao(), db.supervisorDao())
    }

    val userProfileRepository: UserProfileRepository by lazy {
        UserProfileRepository(context)
    }

    val locationHelper: LocationHelper by lazy { LocationHelper(context) }

    val photoStorage: PhotoStorage by lazy { PhotoStorage(context) }
}
