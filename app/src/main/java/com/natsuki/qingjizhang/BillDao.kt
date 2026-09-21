package com.natsuki.qingjizhang

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Query("SELECT * FROM bills ORDER BY id DESC")
    fun getAllBills(): Flow<List<BillEntity>>

    @Insert
    suspend fun insertBill(bill: BillEntity)

    @Update
    suspend fun updateBill(bill: BillEntity)

    @Delete
    suspend fun deleteBill(bill: BillEntity)

    @Query("DELETE FROM bills")
    suspend fun deleteAllBills()

    @Query("SELECT * FROM bills WHERE recurrenceType != 'none' AND recurringId = 0 ORDER BY id ASC")
    suspend fun getRecurringSourceBills(): List<BillEntity>

    @Query("SELECT * FROM bills WHERE recurringId = :sourceId AND date LIKE :datePrefix || '%'")
    suspend fun getRecurredBillsByDatePrefix(sourceId: Long, datePrefix: String): List<BillEntity>
}