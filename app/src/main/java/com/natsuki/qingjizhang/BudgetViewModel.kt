package com.natsuki.qingjizhang

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = ThemePreferences(application)

    //
    private val _budget = MutableStateFlow(0.0)
    val budget: StateFlow<Double> = _budget

    //
    private val _categoryBudgets = MutableStateFlow<Map<String, Double>>(emptyMap())
    val categoryBudgets: StateFlow<Map<String, Double>> = _categoryBudgets

    //
    private val _savingsGoal = MutableStateFlow<SavingsGoal?>(null)
    val savingsGoal: StateFlow<SavingsGoal?> = _savingsGoal

    init {
        viewModelScope.launch {
            prefs.monthlyBudgetFlow.collectLatest { _budget.value = it }
        }
        viewModelScope.launch {
            prefs.categoryBudgetsFlow.collectLatest { _categoryBudgets.value = it }
        }
        viewModelScope.launch {
            prefs.savingsGoalFlow.collectLatest { _savingsGoal.value = it }
        }
    }

    fun setBudget(value: Double) {
        viewModelScope.launch {
            prefs.saveMonthlyBudget(value)
            _budget.value = value
        }
    }

    fun setCategoryBudget(category: String, value: Double) {
        viewModelScope.launch {
            if (value <= 0) {
                prefs.resetCategoryBudget(category)
            } else {
                prefs.saveCategoryBudget(category, value)
            }
        }
    }

    fun resetCategoryBudget(category: String) {
        viewModelScope.launch {
            prefs.resetCategoryBudget(category)
        }
    }

    fun setSavingsGoal(name: String, amount: Double) {
        viewModelScope.launch {
            prefs.saveSavingsGoal(name, amount)
        }
    }

    fun clearSavingsGoal() {
        viewModelScope.launch {
            prefs.clearSavingsGoal()
        }
    }
}