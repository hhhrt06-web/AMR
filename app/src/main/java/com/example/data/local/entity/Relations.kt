package com.example.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PatientWithSamples(
    @Embedded val patient: Patient,
    @Relation(
        parentColumn = "id",
        entityColumn = "patientId"
    )
    val samples: List<Sample>
)

data class SampleWithResults(
    @Embedded val sample: Sample,
    @Relation(
        parentColumn = "id",
        entityColumn = "sampleId"
    )
    val results: List<LaboratoryResult>
)

data class SampleDetail(
    val sampleId: Long,
    val sampleNumber: String,
    val sampleType: String,
    val collectionDateTime: Long,
    val status: String,
    val sampleNotes: String,
    val patientId: Long,
    val patientName: String,
    val patientNumber: String
)

data class ResultDetail(
    val resultId: Long,
    val patientId: Long,
    val sampleId: Long,
    val testName: String,
    val resultValue: String,
    val unit: String,
    val referenceRange: String,
    val resultStatus: String,
    val resultNotes: String,
    val dateTime: Long,
    val patientName: String,
    val patientNumber: String,
    val sampleNumber: String,
    val sampleType: String
)
