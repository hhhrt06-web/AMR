package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.Sample
import com.example.data.local.entity.SampleDetail
import com.example.data.local.entity.SampleWithResults
import kotlinx.coroutines.flow.Flow

@Dao
interface SampleDao {
    @Query("SELECT * FROM samples ORDER BY id DESC")
    fun getAllSamples(): Flow<List<Sample>>

    @Query("""
        SELECT 
            s.id AS sampleId,
            s.sampleNumber AS sampleNumber,
            s.sampleType AS sampleType,
            s.collectionDateTime AS collectionDateTime,
            s.status AS status,
            s.notes AS sampleNotes,
            p.id AS patientId,
            p.fullName AS patientName,
            p.patientNumber AS patientNumber
        FROM samples s
        INNER JOIN patients p ON s.patientId = p.id
        ORDER BY s.id DESC
    """)
    fun getAllSampleDetails(): Flow<List<SampleDetail>>

    @Query("SELECT * FROM samples WHERE patientId = :patientId ORDER BY id DESC")
    fun getSamplesForPatient(patientId: Long): Flow<List<Sample>>

    @Query("SELECT * FROM samples WHERE id = :id")
    fun getSampleById(id: Long): Flow<Sample?>

    @Query("SELECT * FROM samples WHERE id = :id")
    suspend fun getSampleByIdDirect(id: Long): Sample?

    @Query("SELECT COUNT(*) FROM samples WHERE status != 'COMPLETED' AND status != 'REJECTED'")
    fun getPendingSamplesCount(): Flow<Int>

    @Query("""
        SELECT 
            s.id AS sampleId,
            s.sampleNumber AS sampleNumber,
            s.sampleType AS sampleType,
            s.collectionDateTime AS collectionDateTime,
            s.status AS status,
            s.notes AS sampleNotes,
            p.id AS patientId,
            p.fullName AS patientName,
            p.patientNumber AS patientNumber
        FROM samples s
        INNER JOIN patients p ON s.patientId = p.id
        WHERE s.sampleNumber LIKE '%' || :query || '%' 
           OR p.fullName LIKE '%' || :query || '%'
           OR p.patientNumber LIKE '%' || :query || '%'
           OR s.sampleType LIKE '%' || :query || '%'
        ORDER BY s.id DESC
    """)
    fun searchSamples(query: String): Flow<List<SampleDetail>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSample(sample: Sample): Long

    @Update
    suspend fun updateSample(sample: Sample)

    @Delete
    suspend fun deleteSample(sample: Sample)

    @Transaction
    @Query("SELECT * FROM samples WHERE id = :sampleId")
    fun getSampleWithResults(sampleId: Long): Flow<SampleWithResults?>

    @Query("SELECT COUNT(*) FROM samples")
    fun getTotalSamplesCount(): Flow<Int>
}
