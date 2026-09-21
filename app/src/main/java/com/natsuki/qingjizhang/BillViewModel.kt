package com.natsuki.qingjizhang

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BillViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).billDao()

    private val _selectedYear = MutableStateFlow(LocalDate.now().year)
    private val _selectedMonth = MutableStateFlow(LocalDate.now().monthValue)

    val selectedYear: StateFlow<Int> = _selectedYear
    val selectedMonth: StateFlow<Int> = _selectedMonth

    val allBillsFlow: Flow<List<BillEntity>> = dao.getAllBills()

    val bills: Flow<List<BillEntity>> = combine(
        allBillsFlow, _selectedYear, _selectedMonth
    ) { list, year, month ->
        list.filter { bill ->
            val parts = bill.date.split(" ", "-", ":")
            parts.size >= 3 &&
                    parts[0].toIntOrNull() == year &&
                    parts[1].toIntOrNull() == month
        }
    }

    fun previousMonth() {
        if (_selectedMonth.value == 1) {
            _selectedMonth.value = 12
            _selectedYear.value -= 1
        } else {
            _selectedMonth.value -= 1
        }
    }

    fun nextMonth() {
        if (_selectedMonth.value == 12) {
            _selectedMonth.value = 1
            _selectedYear.value += 1
        } else {
            _selectedMonth.value += 1
        }
    }

    fun jumpTo(year: Int, month: Int) {
        _selectedYear.value = year
        _selectedMonth.value = month
    }

    fun isCurrentMonth(): Boolean {
        val now = LocalDate.now()
        return _selectedYear.value == now.year && _selectedMonth.value == now.monthValue
    }

    fun addBill(
        title: String,
        amount: Double,
        category: String,
        isExpense: Boolean,
        dateMillis: Long = System.currentTimeMillis(),
        recurrenceType: String = "none",
        tags: String = ""
    ) {
        viewModelScope.launch {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            dao.insertBill(
                BillEntity(
                    title = title,
                    amount = if (isExpense) -amount else amount,
                    category = category,
                    date = sdf.format(java.util.Date(dateMillis)),
                    isExpense = isExpense,
                    isRecurring = recurrenceType != "none",
                    recurringId = 0,
                    recurrenceType = recurrenceType,
                    tags = tags
                )
            )
        }
    }

    fun updateBill(bill: BillEntity) {
        viewModelScope.launch { dao.updateBill(bill) }
    }

    fun deleteBill(bill: BillEntity) {
        viewModelScope.launch { dao.deleteBill(bill) }
    }

    fun deleteAllBills() {
        viewModelScope.launch { dao.deleteAllBills() }
    }

    suspend fun getAllBillsOnce(): List<BillEntity> {
        return dao.getAllBills().first()
    }

    fun importBills(bills: List<BillEntity>) {
        viewModelScope.launch { bills.forEach { dao.insertBill(it) } }
    }

    //
    fun checkAndGenerateRecurring() {
        viewModelScope.launch {
            val sources = dao.getRecurringSourceBills()
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

            sources.forEach { source ->
                val sourceDateTime: LocalDateTime = try {
                    LocalDateTime.parse(source.date, formatter)
                } catch (e: Exception) {
                    return@forEach
                }

                val hour = sourceDateTime.hour
                val minute = sourceDateTime.minute
                val timeSuffix = String.format(" %02d:%02d", hour, minute)

                var cursor = sourceDateTime.toLocalDate()
                var generatedCount = 0
                val maxGenerate = 60

                while (generatedCount < maxGenerate) {
                    cursor = when (source.recurrenceType) {
                        "daily" -> cursor.plusDays(1)
                        "weekly" -> cursor.plusWeeks(1)
                        "monthly" -> cursor.plusMonths(1)
                        else -> return@forEach
                    }

                    if (cursor.isAfter(today)) break

                    val datePrefix = cursor.toString()
                    val fullDate = datePrefix + timeSuffix

                    val existing = dao.getRecurredBillsByDatePrefix(source.id, datePrefix)
                    if (existing.isEmpty()) {
                        dao.insertBill(
                            BillEntity(
                                title = source.title,
                                amount = source.amount,
                                category = source.category,
                                date = fullDate,
                                isExpense = source.isExpense,
                                isRecurring = false,
                                recurringId = source.id,
                                recurrenceType = "none",
                                tags = source.tags
                            )
                        )
                    }
                    generatedCount++
                }
            }
        }
    }
}