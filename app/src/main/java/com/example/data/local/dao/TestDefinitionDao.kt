package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TestDefinition
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDefinitionDao {
    @Query("SELECT * FROM test_definitions ORDER BY category, testName")
    fun getAllTests(): Flow<List<TestDefinition>>

    @Query("SELECT * FROM test_definitions WHERE category = :category ORDER BY testName")
    fun getTestsByCategory(category: String): Flow<List<TestDefinition>>

    @Query("SELECT * FROM test_definitions WHERE id = :id")
    fun getTestById(id: Long): Flow<TestDefinition?>

    @Query("SELECT * FROM test_definitions WHERE testName LIKE '%' || :query || '%' OR testCode LIKE '%' || :query || '%'")
    fun searchTests(query: String): Flow<List<TestDefinition>>

    @Query("SELECT COUNT(*) FROM test_definitions")
    fun getTestCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM test_definitions")
    suspend fun getTestCountDirect(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestDefinition): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTests(tests: List<TestDefinition>)

    @Update
    suspend fun updateTest(test: TestDefinition)

    @Delete
    suspend fun deleteTest(test: TestDefinition)
}
