package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.LaboratoryResult
import com.example.data.local.entity.Patient
import com.example.data.local.entity.ResultDetail
import com.example.data.local.entity.Sample
import com.example.data.local.entity.SampleDetail
import com.example.data.local.entity.TestDefinition
import com.example.data.repository.LabRepository
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LabViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    val repository = LabRepository(database)

    // Current navigation state
    val currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val selectedPatientId = MutableStateFlow<Long?>(null)
    val selectedSampleId = MutableStateFlow<Long?>(null)

    // Global counts for Dashboard & Settings
    val patientCount: StateFlow<Int> = repository.getPatientCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingSamplesCount: StateFlow<Int> = repository.getPendingSamplesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedResultsCount: StateFlow<Int> = repository.getCompletedResultsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalSamplesCount: StateFlow<Int> = repository.getTotalSamplesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalResultsCount: StateFlow<Int> = repository.getTotalResultsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val testCount: StateFlow<Int> = repository.getTestCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Data lists
    val patients: StateFlow<List<Patient>> = repository.getAllPatients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val samples: StateFlow<List<SampleDetail>> = repository.getAllSampleDetails()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val results: StateFlow<List<ResultDetail>> = repository.getAllResultDetails()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tests: StateFlow<List<TestDefinition>> = repository.getAllTests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Patient details (for Profile screen)
    val selectedPatient = selectedPatientId.flatMapLatest { id ->
        if (id != null) repository.getPatientById(id) else MutableStateFlow(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedPatientSamples = selectedPatientId.flatMapLatest { id ->
        if (id != null) repository.getSamplesForPatient(id) else MutableStateFlow(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedPatientResults = selectedPatientId.flatMapLatest { id ->
        if (id != null) repository.getResultDetailsForPatient(id) else MutableStateFlow(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search
    val searchQuery = MutableStateFlow("")
    val searchCategory = MutableStateFlow("ALL") // ALL, PATIENTS, SAMPLES, RESULTS

    // Test category filter
    val selectedTestCategory = MutableStateFlow("ALL")

    // UI Dialog States
    val showAddPatientDialog = MutableStateFlow(false)
    val editingPatient = MutableStateFlow<Patient?>(null)
    val patientToDelete = MutableStateFlow<Patient?>(null)

    val showAddSampleDialog = MutableStateFlow(false)
    val sampleToEdit = MutableStateFlow<SampleDetail?>(null)
    val sampleToDelete = MutableStateFlow<SampleDetail?>(null)
    val preselectedPatientForSample = MutableStateFlow<Patient?>(null)

    val showAddResultDialog = MutableStateFlow(false)
    val resultToEdit = MutableStateFlow<ResultDetail?>(null)
    val resultToDelete = MutableStateFlow<ResultDetail?>(null)
    val preselectedSampleForResult = MutableStateFlow<SampleDetail?>(null)

    val userMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repository.seedDefaultDataIfEmpty()
        }
    }

    // Navigation helpers
    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
    }

    fun openPatientProfile(patientId: Long) {
        selectedPatientId.value = patientId
        currentScreen.value = Screen.PatientProfile
    }

    fun openAddSampleForPatient(patient: Patient) {
        preselectedPatientForSample.value = patient
        showAddSampleDialog.value = true
    }

    fun openAddResultForSample(sampleDetail: SampleDetail) {
        preselectedSampleForResult.value = sampleDetail
        showAddResultDialog.value = true
    }

    // Patient CRUD
    fun savePatient(
        id: Long,
        fullName: String,
        age: Int,
        gender: String,
        phone: String,
        notes: String
    ) {
        viewModelScope.launch {
            if (id == 0L) {
                // Generate Patient Number
                val count = patientCount.value + 1
                val patientNumber = "P-${1000 + count}"
                val newPatient = Patient(
                    patientNumber = patientNumber,
                    fullName = fullName.trim(),
                    age = age,
                    gender = gender,
                    phone = phone.trim(),
                    notes = notes.trim(),
                    createdDate = System.currentTimeMillis()
                )
                repository.insertPatient(newPatient)
                userMessage.value = "تم تسجيل المريض بنجاح ($patientNumber)"
            } else {
                val existing = repository.getPatientByIdDirect(id)
                if (existing != null) {
                    val updated = existing.copy(
                        fullName = fullName.trim(),
                        age = age,
                        gender = gender,
                        phone = phone.trim(),
                        notes = notes.trim()
                    )
                    repository.updatePatient(updated)
                    userMessage.value = "تم تحديث بيانات المريض بنجاح"
                }
            }
            showAddPatientDialog.value = false
            editingPatient.value = null
        }
    }

    fun deletePatient(patient: Patient) {
        viewModelScope.launch {
            repository.deletePatient(patient)
            patientToDelete.value = null
            userMessage.value = "تم حذف سجل المريض وجميع عيناته ونتائجه"
            if (selectedPatientId.value == patient.id) {
                selectedPatientId.value = null
                currentScreen.value = Screen.Patients
            }
        }
    }

    // Sample CRUD
    fun saveSample(
        sampleId: Long,
        patientId: Long,
        sampleType: String,
        status: String,
        notes: String
    ) {
        viewModelScope.launch {
            if (sampleId == 0L) {
                val count = totalSamplesCount.value + 1
                val year = 2026
                val sampleNumber = "SMP-$year-${String.format("%03d", count)}"
                val newSample = Sample(
                    patientId = patientId,
                    sampleNumber = sampleNumber,
                    sampleType = sampleType,
                    collectionDateTime = System.currentTimeMillis(),
                    status = status,
                    notes = notes.trim()
                )
                repository.insertSample(newSample)
                userMessage.value = "تم تسجيل العينة بنجاح برقم: $sampleNumber"
            } else {
                val existing = repository.getSampleByIdDirect(sampleId)
                if (existing != null) {
                    val updated = existing.copy(
                        sampleType = sampleType,
                        status = status,
                        notes = notes.trim()
                    )
                    repository.updateSample(updated)
                    userMessage.value = "تم تحديث بيانات العينة بنجاح"
                }
            }
            showAddSampleDialog.value = false
            sampleToEdit.value = null
            preselectedPatientForSample.value = null
        }
    }

    fun updateSampleStatus(sampleId: Long, newStatus: String) {
        viewModelScope.launch {
            val sample = repository.getSampleByIdDirect(sampleId)
            if (sample != null) {
                repository.updateSample(sample.copy(status = newStatus))
                userMessage.value = "تم تغيير حالة العينة إلى: $newStatus"
            }
        }
    }

    fun deleteSample(sampleDetail: SampleDetail) {
        viewModelScope.launch {
            val sample = repository.getSampleByIdDirect(sampleDetail.sampleId)
            if (sample != null) {
                repository.deleteSample(sample)
                userMessage.value = "تم حذف العينة وجميع نتائجها"
            }
            sampleToDelete.value = null
        }
    }

    // Result CRUD
    fun saveResult(
        resultId: Long,
        patientId: Long,
        sampleId: Long,
        testName: String,
        resultValue: String,
        unit: String,
        referenceRange: String,
        resultStatus: String,
        notes: String
    ) {
        viewModelScope.launch {
            val autoStatus = if (resultStatus.isBlank()) {
                evaluateStatus(testName, resultValue)
            } else {
                resultStatus
            }

            if (resultId == 0L) {
                val newResult = LaboratoryResult(
                    patientId = patientId,
                    sampleId = sampleId,
                    testName = testName.trim(),
                    resultValue = resultValue.trim(),
                    unit = unit.trim(),
                    referenceRange = referenceRange.trim(),
                    resultStatus = autoStatus,
                    notes = notes.trim(),
                    dateTime = System.currentTimeMillis()
                )
                repository.insertResult(newResult)
                // Automatically update sample status to COMPLETED if it was in analysis
                val sample = repository.getSampleByIdDirect(sampleId)
                if (sample != null && sample.status != Sample.STATUS_COMPLETED) {
                    repository.updateSample(sample.copy(status = Sample.STATUS_COMPLETED))
                }
                userMessage.value = "تم حفظ النتيجة المخبرية بنجاح"
            } else {
                val existing = repository.getAllResults() // we can query directly
                // fetch and update
                val updated = LaboratoryResult(
                    id = resultId,
                    patientId = patientId,
                    sampleId = sampleId,
                    testName = testName.trim(),
                    resultValue = resultValue.trim(),
                    unit = unit.trim(),
                    referenceRange = referenceRange.trim(),
                    resultStatus = autoStatus,
                    notes = notes.trim(),
                    dateTime = System.currentTimeMillis()
                )
                repository.updateResult(updated)
                userMessage.value = "تم تحديث النتيجة المخبرية بنجاح"
            }
            showAddResultDialog.value = false
            resultToEdit.value = null
            preselectedSampleForResult.value = null
        }
    }

    fun deleteResult(resultDetail: ResultDetail) {
        viewModelScope.launch {
            val result = LaboratoryResult(
                id = resultDetail.resultId,
                patientId = resultDetail.patientId,
                sampleId = resultDetail.sampleId,
                testName = resultDetail.testName,
                resultValue = resultDetail.resultValue,
                unit = resultDetail.unit,
                referenceRange = resultDetail.referenceRange,
                resultStatus = resultDetail.resultStatus,
                notes = resultDetail.resultNotes,
                dateTime = resultDetail.dateTime
            )
            repository.deleteResult(result)
            resultToDelete.value = null
            userMessage.value = "تم حذف النتيجة المخبرية"
        }
    }

    // Smart status evaluator based on test definitions
    fun evaluateStatus(testName: String, valueStr: String): String {
        val numValue = valueStr.toDoubleOrNull() ?: return LaboratoryResult.STATUS_NORMAL
        val def = tests.value.firstOrNull { it.testName.equals(testName, ignoreCase = true) }
            ?: return LaboratoryResult.STATUS_NORMAL

        // Check Critical Limits first
        if (def.criticalMin != null && numValue < def.criticalMin) return LaboratoryResult.STATUS_CRITICAL
        if (def.criticalMax != null && numValue > def.criticalMax) return LaboratoryResult.STATUS_CRITICAL

        // Check Normal Limits
        if (def.normalMin != null && numValue < def.normalMin) return LaboratoryResult.STATUS_ABNORMAL
        if (def.normalMax != null && numValue > def.normalMax) return LaboratoryResult.STATUS_ABNORMAL

        return LaboratoryResult.STATUS_NORMAL
    }

    fun clearUserMessage() {
        userMessage.value = null
    }
}
