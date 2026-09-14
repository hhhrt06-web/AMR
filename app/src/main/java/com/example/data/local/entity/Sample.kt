package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "samples",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["patientId"]),
        Index(value = ["sampleNumber"], unique = true)
    ]
)
data class Sample(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val sampleNumber: String,
    val sampleType: String,
    val collectionDateTime: Long = System.currentTimeMillis(),
    val status: String = STATUS_PENDING,
    val notes: String = ""
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_RECEIVED = "RECEIVED"
        const val STATUS_IN_ANALYSIS = "IN_ANALYSIS"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_REJECTED = "REJECTED"
    }
}
