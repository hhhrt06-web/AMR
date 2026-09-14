package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.LaboratoryResult
import com.example.data.local.entity.Patient
import com.example.data.local.entity.PatientWithSamples
import com.example.data.local.entity.ResultDetail
import com.example.data.local.entity.Sample
import com.example.data.local.entity.SampleDetail
import com.example.data.local.entity.SampleWithResults
import com.example.data.local.entity.TestDefinition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LabRepository(private val db: AppDatabase) {

    private val patientDao = db.patientDao()
    private val sampleDao = db.sampleDao()
    private val resultDao = db.laboratoryResultDao()
    private val testDao = db.testDefinitionDao()

    // Patients
    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()
    fun getPatientById(id: Long): Flow<Patient?> = patientDao.getPatientById(id)
    suspend fun getPatientByIdDirect(id: Long): Patient? = patientDao.getPatientByIdDirect(id)
    fun searchPatients(query: String): Flow<List<Patient>> = patientDao.searchPatients(query)
    fun getPatientCount(): Flow<Int> = patientDao.getPatientCount()
    fun getPatientWithSamples(patientId: Long): Flow<PatientWithSamples?> = patientDao.getPatientWithSamples(patientId)

    suspend fun insertPatient(patient: Patient): Long = withContext(Dispatchers.IO) {
        patientDao.insertPatient(patient)
    }

    suspend fun updatePatient(patient: Patient) = withContext(Dispatchers.IO) {
        patientDao.updatePatient(patient)
    }

    suspend fun deletePatient(patient: Patient) = withContext(Dispatchers.IO) {
        patientDao.deletePatient(patient)
    }

    // Samples
    fun getAllSamples(): Flow<List<Sample>> = sampleDao.getAllSamples()
    fun getAllSampleDetails(): Flow<List<SampleDetail>> = sampleDao.getAllSampleDetails()
    fun getSamplesForPatient(patientId: Long): Flow<List<Sample>> = sampleDao.getSamplesForPatient(patientId)
    fun getSampleById(id: Long): Flow<Sample?> = sampleDao.getSampleById(id)
    suspend fun getSampleByIdDirect(id: Long): Sample? = sampleDao.getSampleByIdDirect(id)
    fun getPendingSamplesCount(): Flow<Int> = sampleDao.getPendingSamplesCount()
    fun searchSamples(query: String): Flow<List<SampleDetail>> = sampleDao.searchSamples(query)
    fun getSampleWithResults(sampleId: Long): Flow<SampleWithResults?> = sampleDao.getSampleWithResults(sampleId)
    fun getTotalSamplesCount(): Flow<Int> = sampleDao.getTotalSamplesCount()

    suspend fun insertSample(sample: Sample): Long = withContext(Dispatchers.IO) {
        sampleDao.insertSample(sample)
    }

    suspend fun updateSample(sample: Sample) = withContext(Dispatchers.IO) {
        sampleDao.updateSample(sample)
    }

    suspend fun deleteSample(sample: Sample) = withContext(Dispatchers.IO) {
        sampleDao.deleteSample(sample)
    }

    // Results
    fun getAllResults(): Flow<List<LaboratoryResult>> = resultDao.getAllResults()
    fun getAllResultDetails(): Flow<List<ResultDetail>> = resultDao.getAllResultDetails()
    fun getResultsForPatient(patientId: Long): Flow<List<LaboratoryResult>> = resultDao.getResultsForPatient(patientId)
    fun getResultsForSample(sampleId: Long): Flow<List<LaboratoryResult>> = resultDao.getResultsForSample(sampleId)
    fun getResultDetailsForSample(sampleId: Long): Flow<List<ResultDetail>> = resultDao.getResultDetailsForSample(sampleId)
    fun getResultDetailsForPatient(patientId: Long): Flow<List<ResultDetail>> = resultDao.getResultDetailsForPatient(patientId)
    fun getResultById(id: Long): Flow<LaboratoryResult?> = resultDao.getResultById(id)
    fun getCompletedResultsCount(): Flow<Int> = resultDao.getCompletedResultsCount()
    fun searchResultDetails(query: String): Flow<List<ResultDetail>> = resultDao.searchResultDetails(query)
    fun getTotalResultsCount(): Flow<Int> = resultDao.getTotalResultsCount()

    suspend fun insertResult(result: LaboratoryResult): Long = withContext(Dispatchers.IO) {
        resultDao.insertResult(result)
    }

    suspend fun insertResults(results: List<LaboratoryResult>): List<Long> = withContext(Dispatchers.IO) {
        resultDao.insertResults(results)
    }

    suspend fun updateResult(result: LaboratoryResult) = withContext(Dispatchers.IO) {
        resultDao.updateResult(result)
    }

    suspend fun deleteResult(result: LaboratoryResult) = withContext(Dispatchers.IO) {
        resultDao.deleteResult(result)
    }

    // Tests Definition
    fun getAllTests(): Flow<List<TestDefinition>> = testDao.getAllTests()
    fun getTestsByCategory(category: String): Flow<List<TestDefinition>> = testDao.getTestsByCategory(category)
    fun searchTests(query: String): Flow<List<TestDefinition>> = testDao.searchTests(query)
    fun getTestCount(): Flow<Int> = testDao.getTestCount()

    suspend fun insertTest(test: TestDefinition): Long = withContext(Dispatchers.IO) {
        testDao.insertTest(test)
    }

    suspend fun updateTest(test: TestDefinition) = withContext(Dispatchers.IO) {
        testDao.updateTest(test)
    }

    suspend fun deleteTest(test: TestDefinition) = withContext(Dispatchers.IO) {
        testDao.deleteTest(test)
    }

    suspend fun seedDefaultDataIfEmpty() = withContext(Dispatchers.IO) {
        val testCount = testDao.getTestCountDirect()
        val patientCount = patientDao.getPatientCountDirect()
        if (testCount == 0 && patientCount == 0) {
            db.seedInitialData()
        }
    }
}
