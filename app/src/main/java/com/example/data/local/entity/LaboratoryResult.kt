package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "laboratory_results",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Sample::class,
            parentColumns = ["id"],
            childColumns = ["sampleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["patientId"]),
        Index(value = ["sampleId"])
    ]
)
data class LaboratoryResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val sampleId: Long,
    val testName: String,
    val resultValue: String,
    val unit: String = "",
    val referenceRange: String = "",
    val resultStatus: String = STATUS_NORMAL, // NORMAL, ABNORMAL, CRITICAL, PENDING
    val notes: String = "",
    val dateTime: Long = System.currentTimeMillis()
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_NORMAL = "NORMAL"
        const val STATUS_ABNORMAL = "ABNORMAL"
        const val STATUS_CRITICAL = "CRITICAL"
    }
}
