package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.LaboratoryResult
import com.example.data.local.entity.ResultDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface LaboratoryResultDao {
    @Query("SELECT * FROM laboratory_results ORDER BY id DESC")
    fun getAllResults(): Flow<List<LaboratoryResult>>

    @Query("""
        SELECT 
            r.id AS resultId,
            r.patientId AS patientId,
            r.sampleId AS sampleId,
            r.testName AS testName,
            r.resultValue AS resultValue,
            r.unit AS unit,
            r.referenceRange AS referenceRange,
            r.resultStatus AS resultStatus,
            r.notes AS resultNotes,
            r.dateTime AS dateTime,
            p.fullName AS patientName,
            p.patientNumber AS patientNumber,
            s.sampleNumber AS sampleNumber,
            s.sampleType AS sampleType
        FROM laboratory_results r
        INNER JOIN patients p ON r.patientId = p.id
        INNER JOIN samples s ON r.sampleId = s.id
        ORDER BY r.id DESC
    """)
    fun getAllResultDetails(): Flow<List<ResultDetail>>

    @Query("SELECT * FROM laboratory_results WHERE patientId = :patientId ORDER BY id DESC")
    fun getResultsForPatient(patientId: Long): Flow<List<LaboratoryResult>>

    @Query("SELECT * FROM laboratory_results WHERE sampleId = :sampleId ORDER BY id ASC")
    fun getResultsForSample(sampleId: Long): Flow<List<LaboratoryResult>>

    @Query("""
        SELECT 
            r.id AS resultId,
            r.patientId AS patientId,
            r.sampleId AS sampleId,
            r.testName AS testName,
            r.resultValue AS resultValue,
            r.unit AS unit,
            r.referenceRange AS referenceRange,
            r.resultStatus AS resultStatus,
            r.notes AS resultNotes,
            r.dateTime AS dateTime,
            p.fullName AS patientName,
            p.patientNumber AS patientNumber,
            s.sampleNumber AS sampleNumber,
            s.sampleType AS sampleType
        FROM laboratory_results r
        INNER JOIN patients p ON r.patientId = p.id
        INNER JOIN samples s ON r.sampleId = s.id
        WHERE r.sampleId = :sampleId
        ORDER BY r.id ASC
    """)
    fun getResultDetailsForSample(sampleId: Long): Flow<List<ResultDetail>>

    @Query("""
        SELECT 
            r.id AS resultId,
            r.patientId AS patientId,
            r.sampleId AS sampleId,
            r.testName AS testName,
            r.resultValue AS resultValue,
            r.unit AS unit,
            r.referenceRange AS referenceRange,
            r.resultStatus AS resultStatus,
            r.notes AS resultNotes,
            r.dateTime AS dateTime,
            p.fullName AS patientName,
            p.patientNumber AS patientNumber,
            s.sampleNumber AS sampleNumber,
            s.sampleType AS sampleType
        FROM laboratory_results r
        INNER JOIN patients p ON r.patientId = p.id
        INNER JOIN samples s ON r.sampleId = s.id
        WHERE r.patientId = :patientId
        ORDER BY r.id DESC
    """)
    fun getResultDetailsForPatient(patientId: Long): Flow<List<ResultDetail>>

    @Query("SELECT * FROM laboratory_results WHERE id = :id")
    fun getResultById(id: Long): Flow<LaboratoryResult?>

    @Query("SELECT COUNT(*) FROM laboratory_results WHERE resultStatus != 'PENDING' AND resultValue != ''")
    fun getCompletedResultsCount(): Flow<Int>

    @Query("""
        SELECT 
            r.id AS resultId,
            r.patientId AS patientId,
            r.sampleId AS sampleId,
            r.testName AS testName,
            r.resultValue AS resultValue,
            r.unit AS unit,
            r.referenceRange AS referenceRange,
            r.resultStatus AS resultStatus,
            r.notes AS resultNotes,
            r.dateTime AS dateTime,
            p.fullName AS patientName,
            p.patientNumber AS patientNumber,
            s.sampleNumber AS sampleNumber,
            s.sampleType AS sampleType
        FROM laboratory_results r
        INNER JOIN patients p ON r.patientId = p.id
        INNER JOIN samples s ON r.sampleId = s.id
        WHERE r.testName LIKE '%' || :query || '%'
           OR p.fullName LIKE '%' || :query || '%'
           OR p.patientNumber LIKE '%' || :query || '%'
           OR s.sampleNumber LIKE '%' || :query || '%'
        ORDER BY r.id DESC
    """)
    fun searchResultDetails(query: String): Flow<List<ResultDetail>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: LaboratoryResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<LaboratoryResult>): List<Long>

    @Update
    suspend fun updateResult(result: LaboratoryResult)

    @Delete
    suspend fun deleteResult(result: LaboratoryResult)

    @Query("SELECT COUNT(*) FROM laboratory_results")
    fun getTotalResultsCount(): Flow<Int>
}
