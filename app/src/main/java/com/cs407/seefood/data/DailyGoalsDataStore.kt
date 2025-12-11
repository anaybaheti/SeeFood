package com.cs407.seefood.ui.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.preferencesDataStore
import com.cs407.seefood.ui.DailyGoals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Correct DataStore delegate
private val Context.dataStore by preferencesDataStore(name = "daily_goals_prefs")

class DailyGoalsDataStore(private val context: Context) {

    companion object {
        private val KEY_CALORIES = intPreferencesKey("calories")
        private val KEY_PROTEIN = intPreferencesKey("protein")
        private val KEY_CARBS = intPreferencesKey("carbs")
        private val KEY_FAT = intPreferencesKey("fat")
    }

    suspend fun saveGoals(goals: DailyGoals) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[KEY_CALORIES] = goals.calories
            prefs[KEY_PROTEIN] = goals.proteinGrams
            prefs[KEY_CARBS] = goals.carbsGrams
            prefs[KEY_FAT] = goals.fatGrams
        }
    }

    suspend fun loadGoals(): DailyGoals {
        return context.dataStore.data.map { prefs: Preferences ->
            DailyGoals(
                calories = prefs[KEY_CALORIES] ?: 2000,
                proteinGrams = prefs[KEY_PROTEIN] ?: 150,
                carbsGrams = prefs[KEY_CARBS] ?: 250,
                fatGrams = prefs[KEY_FAT] ?: 65
            )
        }.first()
    }
}
