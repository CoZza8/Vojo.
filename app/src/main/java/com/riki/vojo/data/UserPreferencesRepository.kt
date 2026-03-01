package com.riki.vojo.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val IS_PRO_USER = booleanPreferencesKey("is_pro_user")
        private val SELECTED_AVATAR_INDEX = intPreferencesKey("selected_avatar_index")
    }

    val isProUser: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_PRO_USER] ?: false
        }

    val selectedAvatarIndex: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_AVATAR_INDEX] ?: 0
        }

    suspend fun setProUser(isPro: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_PRO_USER] = isPro
        }
    }

    suspend fun setSelectedAvatarIndex(index: Int) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_AVATAR_INDEX] = index
        }
    }
}

