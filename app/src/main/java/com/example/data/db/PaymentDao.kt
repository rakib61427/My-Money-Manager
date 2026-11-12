package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE personId = :personId ORDER BY datetime DESC, id DESC")
    fun getPaymentsForPerson(personId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments ORDER BY datetime DESC, id DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE id = :id LIMIT 1")
    suspend fun getPaymentByIdSync(id: Long): PaymentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(payments: List<PaymentEntity>)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)

    @Query("DELETE FROM payments WHERE id = :id")
    suspend fun deletePaymentById(id: Long)

    @Query("DELETE FROM payments WHERE personId = :personId")
    suspend fun deletePaymentsByPersonId(personId: Long)

    @Query("DELETE FROM payments")
    suspend fun deleteAll()

    @Query("SELECT * FROM payments ORDER BY id ASC")
    suspend fun getAllPaymentsSync(): List<PaymentEntity>
}
