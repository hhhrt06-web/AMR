package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.Patient
import com.example.data.local.entity.PatientWithSamples
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients ORDER BY id DESC")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE id = :id")
    fun getPatientById(id: Long): Flow<Patient?>

    @Query("SELECT * FROM patients WHERE id = :id")
    suspend fun getPatientByIdDirect(id: Long): Patient?

    @Query("SELECT * FROM patients WHERE fullName LIKE '%' || :query || '%' OR patientNumber LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchPatients(query: String): Flow<List<Patient>>

    @Query("SELECT COUNT(*) FROM patients")
    fun getPatientCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCountDirect(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient): Long

    @Update
    suspend fun updatePatient(patient: Patient)

    @Delete
    suspend fun deletePatient(patient: Patient)

    @Transaction
    @Query("SELECT * FROM patients WHERE id = :patientId")
    fun getPatientWithSamples(patientId: Long): Flow<PatientWithSamples?>
}
