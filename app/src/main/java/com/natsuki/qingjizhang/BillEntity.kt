package com.natsuki.qingjizhang

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
    val isExpense: Boolean,
    val isRecurring: Boolean = false,
    val recurringId: Long = 0,
    val recurrenceType: String = "none",
    val tags: String = ""
)