package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "patients",
    indices = [
        Index(value = ["patientNumber"], unique = true)
    ]
)
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientNumber: String,
    val fullName: String,
    val age: Int,
    val gender: String,
    val phone: String,
    val notes: String = "",
    val createdDate: Long = System.currentTimeMillis()
)
