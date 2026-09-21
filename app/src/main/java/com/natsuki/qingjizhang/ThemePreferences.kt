package com.natsuki.qingjizhang

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

class ThemePreferences(private val context: Context) {
    private val THEME_MODE_KEY = intPreferencesKey("theme_mode")
    private val HAS_SEEN_ONBOARDING_KEY = booleanPreferencesKey("has_seen_onboarding")
    private val HAS_AGREED_TO_TERMS_KEY = booleanPreferencesKey("has_agreed_to_terms")
    private val MONTHLY_BUDGET_KEY = doublePreferencesKey("monthly_budget")
    private val CUSTOM_KEY_COLOR_KEY = intPreferencesKey("custom_key_color")

    private val REMINDER_ENABLED_KEY = booleanPreferencesKey("reminder_enabled")
    private val REMINDER_HOUR_KEY = intPreferencesKey("reminder_hour")
    private val REMINDER_MINUTE_KEY = intPreferencesKey("reminder_minute")

    private val LEARNED_MAPPINGS_KEY = stringSetPreferencesKey("learned_mappings")
    private val CATEGORY_COLORS_KEY = stringSetPreferencesKey("category_colors")
    private val BUDGET_ALERT_DISMISSED_KEY = stringPreferencesKey("budget_alert_dismissed")
    private val CELEBRATED_MILESTONES_KEY = stringSetPreferencesKey("celebrated_streak_milestones")
    private val CATEGORY_BUDGETS_KEY = stringSetPreferencesKey("category_budgets")


    private val SAVINGS_GOAL_NAME_KEY = stringPreferencesKey("savings_goal_name")
    private val SAVINGS_GOAL_AMOUNT_KEY = doublePreferencesKey("savings_goal_amount")
    private val SAVINGS_GOAL_CREATED_AT_KEY = longPreferencesKey("savings_goal_created_at")


    private val UPDATE_CHECK_ENABLED_KEY = booleanPreferencesKey("update_check_enabled")
    private val GITHUB_REPO_KEY = stringPreferencesKey("github_repo")
    private val LAST_UPDATE_CHECK_KEY = longPreferencesKey("last_update_check")



    val themeModeFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[THEME_MODE_KEY] ?: 0 }

    suspend fun saveThemeMode(mode: Int) {
        context.dataStore.edit { preferences -> preferences[THEME_MODE_KEY] = mode }
    }



    val hasSeenOnboardingFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[HAS_SEEN_ONBOARDING_KEY] ?: false }

    suspend fun setHasSeenOnboarding(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[HAS_SEEN_ONBOARDING_KEY] = value }
    }



    val hasAgreedToTermsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[HAS_AGREED_TO_TERMS_KEY] ?: false }

    suspend fun setHasAgreedToTerms(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[HAS_AGREED_TO_TERMS_KEY] = value }
    }



    val monthlyBudgetFlow: Flow<Double> = context.dataStore.data
        .map { preferences -> preferences[MONTHLY_BUDGET_KEY] ?: 0.0 }

    suspend fun saveMonthlyBudget(value: Double) {
        context.dataStore.edit { preferences -> preferences[MONTHLY_BUDGET_KEY] = value }
    }



    val reminderFlow: Flow<Triple<Boolean, Int, Int>> = context.dataStore.data
        .map { prefs ->
            Triple(
                prefs[REMINDER_ENABLED_KEY] ?: false,
                prefs[REMINDER_HOUR_KEY] ?: 21,
                prefs[REMINDER_MINUTE_KEY] ?: 0
            )
        }

    suspend fun saveReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit { prefs ->
            prefs[REMINDER_ENABLED_KEY] = enabled
            prefs[REMINDER_HOUR_KEY] = hour
            prefs[REMINDER_MINUTE_KEY] = minute
        }
    }


    val customKeyColorFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[CUSTOM_KEY_COLOR_KEY] ?: -1 }

    suspend fun saveCustomKeyColor(argb: Int) {
        context.dataStore.edit { prefs ->
            prefs[CUSTOM_KEY_COLOR_KEY] = argb
        }
    }


    val learnedMappingsFlow: Flow<Map<String, String>> = context.dataStore.data
        .map { prefs ->
            prefs[LEARNED_MAPPINGS_KEY]
                ?.mapNotNull { entry ->
                    val parts = entry.split("|", limit = 2)
                    if (parts.size == 2 && parts[0].isNotBlank() && parts[1].isNotBlank())
                        parts[0] to parts[1]
                    else null
                }
                ?.toMap()
                ?: emptyMap()
        }

    suspend fun saveLearnedMapping(keyword: String, category: String) {
        val kw = keyword.trim()
        val cat = category.trim()
        if (kw.isBlank() || cat.isBlank()) return
        if (kw.length > 20) return
        context.dataStore.edit { prefs ->
            val existing = prefs[LEARNED_MAPPINGS_KEY] ?: emptySet()
            val filtered = existing.filterNot { it.substringBefore("|") == kw }.toSet()
            prefs[LEARNED_MAPPINGS_KEY] = filtered + "$kw|$cat"
        }
    }

    suspend fun clearLearnedMappings() {
        context.dataStore.edit { it.remove(LEARNED_MAPPINGS_KEY) }
    }


    val categoryColorsFlow: Flow<Map<String, Int>> = context.dataStore.data
        .map { prefs ->
            prefs[CATEGORY_COLORS_KEY]
                ?.mapNotNull { entry ->
                    val parts = entry.split("|", limit = 2)
                    if (parts.size == 2 && parts[0].isNotBlank()) {
                        val argb = parts[1].toLongOrNull()?.toInt()
                        if (argb != null) parts[0] to argb else null
                    } else null
                }
                ?.toMap()
                ?: emptyMap()
        }

    suspend fun saveCategoryColor(category: String, argb: Int) {
        if (category.isBlank()) return
        context.dataStore.edit { prefs ->
            val existing = prefs[CATEGORY_COLORS_KEY] ?: emptySet()
            val filtered = existing.filterNot { it.substringBefore("|") == category }.toSet()
            prefs[CATEGORY_COLORS_KEY] = filtered + "$category|$argb"
        }
    }

    suspend fun resetCategoryColor(category: String) {
        if (category.isBlank()) return
        context.dataStore.edit { prefs ->
            val existing = prefs[CATEGORY_COLORS_KEY] ?: return@edit
            prefs[CATEGORY_COLORS_KEY] = existing.filterNot { it.substringBefore("|") == category }.toSet()
        }
    }


    val budgetAlertDismissedFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[BUDGET_ALERT_DISMISSED_KEY] ?: "" }

    suspend fun dismissBudgetAlert(monthKey: String) {
        context.dataStore.edit { prefs -> prefs[BUDGET_ALERT_DISMISSED_KEY] = monthKey }
    }

    suspend fun clearBudgetAlertDismiss() {
        context.dataStore.edit { prefs -> prefs.remove(BUDGET_ALERT_DISMISSED_KEY) }
    }


    val celebratedStreakMilestonesFlow: Flow<Set<Int>> = context.dataStore.data
        .map { prefs ->
            prefs[CELEBRATED_MILESTONES_KEY]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                ?: emptySet()
        }

    suspend fun markStreakMilestoneCelebrated(milestone: Int) {
        context.dataStore.edit { prefs ->
            val existing = prefs[CELEBRATED_MILESTONES_KEY] ?: emptySet()
            prefs[CELEBRATED_MILESTONES_KEY] = existing + milestone.toString()
        }
    }


    val categoryBudgetsFlow: Flow<Map<String, Double>> = context.dataStore.data
        .map { prefs ->
            prefs[CATEGORY_BUDGETS_KEY]
                ?.mapNotNull { entry ->
                    val parts = entry.split("|", limit = 2)
                    if (parts.size == 2 && parts[0].isNotBlank()) {
                        val v = parts[1].toDoubleOrNull()
                        if (v != null && v > 0) parts[0] to v else null
                    } else null
                }
                ?.toMap()
                ?: emptyMap()
        }

    suspend fun saveCategoryBudget(category: String, amount: Double) {
        if (category.isBlank()) return
        if (amount <= 0) {
            resetCategoryBudget(category)
            return
        }
        context.dataStore.edit { prefs ->
            val existing = prefs[CATEGORY_BUDGETS_KEY] ?: emptySet()
            val filtered = existing.filterNot { it.substringBefore("|") == category }.toSet()
            prefs[CATEGORY_BUDGETS_KEY] = filtered + "$category|$amount"
        }
    }

    suspend fun resetCategoryBudget(category: String) {
        if (category.isBlank()) return
        context.dataStore.edit { prefs ->
            val existing = prefs[CATEGORY_BUDGETS_KEY] ?: return@edit
            prefs[CATEGORY_BUDGETS_KEY] = existing.filterNot { it.substringBefore("|") == category }.toSet()
        }
    }


    val savingsGoalFlow: Flow<SavingsGoal?> = context.dataStore.data
        .map { prefs ->
            val name = prefs[SAVINGS_GOAL_NAME_KEY] ?: ""
            val amount = prefs[SAVINGS_GOAL_AMOUNT_KEY] ?: 0.0
            val createdAt = prefs[SAVINGS_GOAL_CREATED_AT_KEY] ?: 0L
            if (amount > 0) SavingsGoal(name, amount, createdAt) else null
        }

    suspend fun saveSavingsGoal(name: String, amount: Double) {
        if (amount <= 0) {
            clearSavingsGoal()
            return
        }
        context.dataStore.edit { prefs ->
            prefs[SAVINGS_GOAL_NAME_KEY] = name.trim()
            prefs[SAVINGS_GOAL_AMOUNT_KEY] = amount
            prefs[SAVINGS_GOAL_CREATED_AT_KEY] = System.currentTimeMillis()
        }
    }

    suspend fun clearSavingsGoal() {
        context.dataStore.edit { prefs ->
            prefs.remove(SAVINGS_GOAL_NAME_KEY)
            prefs.remove(SAVINGS_GOAL_AMOUNT_KEY)
            prefs.remove(SAVINGS_GOAL_CREATED_AT_KEY)
        }
    }

    val updateCheckEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[UPDATE_CHECK_ENABLED_KEY] ?: true }

    suspend fun setUpdateCheckEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[UPDATE_CHECK_ENABLED_KEY] = enabled }
    }

    val githubRepoFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[GITHUB_REPO_KEY] ?: "P1nku9tsuki/qingjizhang" }

    suspend fun setGithubRepo(repo: String) {
        context.dataStore.edit { prefs -> prefs[GITHUB_REPO_KEY] = repo.trim() }
    }

    val lastUpdateCheckFlow: Flow<Long> = context.dataStore.data
        .map { prefs -> prefs[LAST_UPDATE_CHECK_KEY] ?: 0L }

    suspend fun setLastUpdateCheck(time: Long) {
        context.dataStore.edit { prefs -> prefs[LAST_UPDATE_CHECK_KEY] = time }
    }
}