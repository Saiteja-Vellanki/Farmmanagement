package com.farmmanagement.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userProfileDataStore by preferencesDataStore(name = "user_profile")
private val KEY_USER_NAME = stringPreferencesKey("user_name")

/**
 * Backs the "Welcome to {name}" header on Screen 1. Deliberately not
 * hardcoded per spec section 3 — this reads from local DataStore now and is
 * the natural place to source from login/profile setup once that's built.
 */
class UserProfileRepository(private val context: Context) {
    fun observeUserName(): Flow<String?> =
        context.userProfileDataStore.data.map { it[KEY_USER_NAME] }

    suspend fun setUserName(name: String) {
        context.userProfileDataStore.edit { it[KEY_USER_NAME] = name }
    }
}
