package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_definitions")
data class TestDefinition(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val testName: String,
    val testCode: String,
    val category: String, // Hematology, Biochemistry, Immunology, Hormones, Microbiology, Urinalysis
    val unit: String = "",
    val referenceRange: String = "",
    val normalMin: Double? = null,
    val normalMax: Double? = null,
    val criticalMin: Double? = null,
    val criticalMax: Double? = null,
    val defaultSampleType: String = "دم كامل (Whole Blood)",
    val notes: String = ""
) {
    companion object {
        const val CATEGORY_HEMATOLOGY = "Hematology"
        const val CATEGORY_BIOCHEMISTRY = "Biochemistry"
        const val CATEGORY_IMMUNOLOGY = "Immunology"
        const val CATEGORY_HORMONES = "Hormones"
        const val CATEGORY_MICROBIOLOGY = "Microbiology"
        const val CATEGORY_URINALYSIS = "Urinalysis"
    }
}
