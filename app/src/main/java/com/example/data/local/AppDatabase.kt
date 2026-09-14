package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.LaboratoryResultDao
import com.example.data.local.dao.PatientDao
import com.example.data.local.dao.SampleDao
import com.example.data.local.dao.TestDefinitionDao
import com.example.data.local.entity.LaboratoryResult
import com.example.data.local.entity.Patient
import com.example.data.local.entity.Sample
import com.example.data.local.entity.TestDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Patient::class,
        Sample::class,
        LaboratoryResult::class,
        TestDefinition::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun sampleDao(): SampleDao
    abstract fun laboratoryResultDao(): LaboratoryResultDao
    abstract fun testDefinitionDao(): TestDefinitionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "labcore_medical_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).seedInitialData()
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun seedInitialData() {
        val testDao = testDefinitionDao()
        val patientDao = patientDao()
        val sampleDao = sampleDao()
        val resultDao = laboratoryResultDao()

        // Seed Standard Test Definitions across all requested categories
        val defaultTests = listOf(
            // Hematology (أمراض الدم)
            TestDefinition(
                testName = "Hemoglobin (Hb)",
                testCode = "HB",
                category = TestDefinition.CATEGORY_HEMATOLOGY,
                unit = "g/dL",
                referenceRange = "12.0 - 17.5",
                normalMin = 12.0,
                normalMax = 17.5,
                criticalMin = 7.0,
                criticalMax = 20.0,
                defaultSampleType = "دم كامل (EDTA Purple)"
            ),
            TestDefinition(
                testName = "White Blood Cells (WBC)",
                testCode = "WBC",
                category = TestDefinition.CATEGORY_HEMATOLOGY,
                unit = "x10^3/uL",
                referenceRange = "4.0 - 11.0",
                normalMin = 4.0,
                normalMax = 11.0,
                criticalMin = 2.0,
                criticalMax = 30.0,
                defaultSampleType = "دم كامل (EDTA Purple)"
            ),
            TestDefinition(
                testName = "Platelets Count (PLT)",
                testCode = "PLT",
                category = TestDefinition.CATEGORY_HEMATOLOGY,
                unit = "x10^3/uL",
                referenceRange = "150 - 450",
                normalMin = 150.0,
                normalMax = 450.0,
                criticalMin = 50.0,
                criticalMax = 1000.0,
                defaultSampleType = "دم كامل (EDTA Purple)"
            ),
            TestDefinition(
                testName = "Erythrocyte Sedimentation Rate (ESR)",
                testCode = "ESR",
                category = TestDefinition.CATEGORY_HEMATOLOGY,
                unit = "mm/hr",
                referenceRange = "0 - 20",
                normalMin = 0.0,
                normalMax = 20.0,
                defaultSampleType = "دم كامل (Citrate Black)"
            ),

            // Biochemistry (الكيمياء الحيوية)
            TestDefinition(
                testName = "Fasting Blood Glucose (FBS)",
                testCode = "FBS",
                category = TestDefinition.CATEGORY_BIOCHEMISTRY,
                unit = "mg/dL",
                referenceRange = "70 - 100",
                normalMin = 70.0,
                normalMax = 100.0,
                criticalMin = 45.0,
                criticalMax = 400.0,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),
            TestDefinition(
                testName = "Serum Creatinine",
                testCode = "CREAT",
                category = TestDefinition.CATEGORY_BIOCHEMISTRY,
                unit = "mg/dL",
                referenceRange = "0.7 - 1.3",
                normalMin = 0.7,
                normalMax = 1.3,
                criticalMin = 0.4,
                criticalMax = 4.0,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),
            TestDefinition(
                testName = "Blood Urea Nitrogen (BUN)",
                testCode = "BUN",
                category = TestDefinition.CATEGORY_BIOCHEMISTRY,
                unit = "mg/dL",
                referenceRange = "7 - 20",
                normalMin = 7.0,
                normalMax = 20.0,
                criticalMin = 3.0,
                criticalMax = 80.0,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),
            TestDefinition(
                testName = "Alanine Aminotransferase (ALT)",
                testCode = "ALT",
                category = TestDefinition.CATEGORY_BIOCHEMISTRY,
                unit = "U/L",
                referenceRange = "7 - 56",
                normalMin = 7.0,
                normalMax = 56.0,
                criticalMax = 200.0,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),
            TestDefinition(
                testName = "Total Cholesterol",
                testCode = "CHOL",
                category = TestDefinition.CATEGORY_BIOCHEMISTRY,
                unit = "mg/dL",
                referenceRange = "< 200",
                normalMax = 200.0,
                criticalMax = 300.0,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),
            TestDefinition(
                testName = "Serum Uric Acid",
                testCode = "URIC",
                category = TestDefinition.CATEGORY_BIOCHEMISTRY,
                unit = "mg/dL",
                referenceRange = "3.5 - 7.2",
                normalMin = 3.5,
                normalMax = 7.2,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),

            // Immunology (المناعة والمصليات)
            TestDefinition(
                testName = "C-Reactive Protein (CRP)",
                testCode = "CRP",
                category = TestDefinition.CATEGORY_IMMUNOLOGY,
                unit = "mg/L",
                referenceRange = "< 5.0",
                normalMax = 5.0,
                criticalMax = 50.0,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),
            TestDefinition(
                testName = "Rheumatoid Factor (RF)",
                testCode = "RF",
                category = TestDefinition.CATEGORY_IMMUNOLOGY,
                unit = "IU/mL",
                referenceRange = "< 14.0 (Negative)",
                normalMax = 14.0,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),
            TestDefinition(
                testName = "Anti-Streptolysin O (ASO)",
                testCode = "ASO",
                category = TestDefinition.CATEGORY_IMMUNOLOGY,
                unit = "IU/mL",
                referenceRange = "< 200",
                normalMax = 200.0,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),
            TestDefinition(
                testName = "Hepatitis B Surface Ag (HBsAg)",
                testCode = "HBsAg",
                category = TestDefinition.CATEGORY_IMMUNOLOGY,
                unit = "Index",
                referenceRange = "Negative (< 0.9)",
                normalMax = 0.9,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),

            // Hormones (الهرمونات)
            TestDefinition(
                testName = "Thyroid Stimulating Hormone (TSH)",
                testCode = "TSH",
                category = TestDefinition.CATEGORY_HORMONES,
                unit = "uIU/mL",
                referenceRange = "0.4 - 4.0",
                normalMin = 0.4,
                normalMax = 4.0,
                criticalMin = 0.1,
                criticalMax = 15.0,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),
            TestDefinition(
                testName = "Free Thyroxine (FT4)",
                testCode = "FT4",
                category = TestDefinition.CATEGORY_HORMONES,
                unit = "ng/dL",
                referenceRange = "0.8 - 1.8",
                normalMin = 0.8,
                normalMax = 1.8,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),
            TestDefinition(
                testName = "Serum Ferritin",
                testCode = "FERR",
                category = TestDefinition.CATEGORY_HORMONES,
                unit = "ng/mL",
                referenceRange = "20 - 250",
                normalMin = 20.0,
                normalMax = 250.0,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),
            TestDefinition(
                testName = "Vitamin D (25-OH)",
                testCode = "VITD",
                category = TestDefinition.CATEGORY_HORMONES,
                unit = "ng/mL",
                referenceRange = "30 - 100",
                normalMin = 30.0,
                normalMax = 100.0,
                defaultSampleType = "مصل دم (Serum Gold/Red)"
            ),

            // Microbiology (الأحياء الدقيقة)
            TestDefinition(
                testName = "Urine Culture & Sensitivity (Urine C/S)",
                testCode = "UC_S",
                category = TestDefinition.CATEGORY_MICROBIOLOGY,
                unit = "CFU/mL",
                referenceRange = "No Growth / Negative",
                defaultSampleType = "عينة بول نظيفة منتصف المجرى (Midstream Urine)"
            ),
            TestDefinition(
                testName = "Gram Stain Examination",
                testCode = "GRAM",
                category = TestDefinition.CATEGORY_MICROBIOLOGY,
                unit = "Microscopy",
                referenceRange = "No organisms seen",
                defaultSampleType = "مسحة أو سائل حيوي (Swab / Fluid)"
            ),
            TestDefinition(
                testName = "Acid Fast Bacilli (AFB)",
                testCode = "AFB",
                category = TestDefinition.CATEGORY_MICROBIOLOGY,
                unit = "Smear",
                referenceRange = "Negative",
                defaultSampleType = "بصاق (Sputum)"
            ),

            // Urinalysis (فحص البول وسوائل الجسم)
            TestDefinition(
                testName = "Urine Routine: Protein",
                testCode = "U_PROT",
                category = TestDefinition.CATEGORY_URINALYSIS,
                unit = "Dipstick",
                referenceRange = "Negative (Nil)",
                defaultSampleType = "عينة بول عشوائية (Random Urine)"
            ),
            TestDefinition(
                testName = "Urine Routine: Glucose",
                testCode = "U_GLUC",
                category = TestDefinition.CATEGORY_URINALYSIS,
                unit = "Dipstick",
                referenceRange = "Negative (Nil)",
                defaultSampleType = "عينة بول عشوائية (Random Urine)"
            ),
            TestDefinition(
                testName = "Urine Microscopy: Pus Cells (WBCs)",
                testCode = "U_PUS",
                category = TestDefinition.CATEGORY_URINALYSIS,
                unit = "/HPF",
                referenceRange = "0 - 5",
                normalMin = 0.0,
                normalMax = 5.0,
                defaultSampleType = "عينة بول عشوائية (Random Urine)"
            ),
            TestDefinition(
                testName = "Urine Microscopy: Red Blood Cells (RBCs)",
                testCode = "U_RBC",
                category = TestDefinition.CATEGORY_URINALYSIS,
                unit = "/HPF",
                referenceRange = "0 - 3",
                normalMin = 0.0,
                normalMax = 3.0,
                defaultSampleType = "عينة بول عشوائية (Random Urine)"
            )
        )

        testDao.insertTests(defaultTests)

        // Seed initial patients, sample and clinical results
        val patient1 = Patient(
            patientNumber = "P-1001",
            fullName = "أحمد خالد المنصور",
            age = 42,
            gender = "ذكر",
            phone = "0501234567",
            notes = "متابعة دورية لمريض سكري وضغط"
        )
        val p1Id = patientDao.insertPatient(patient1)

        val patient2 = Patient(
            patientNumber = "P-1002",
            fullName = "فاطمة إبراهيم الشهري",
            age = 29,
            gender = "أنثى",
            phone = "0559876543",
            notes = "فحص ما قبل الزواج وهرمونات"
        )
        val p2Id = patientDao.insertPatient(patient2)

        val patient3 = Patient(
            patientNumber = "P-1003",
            fullName = "سالم محمد الدوسري",
            age = 65,
            gender = "ذكر",
            phone = "0543219876",
            notes = "فحص وظائف الكلى الدورية"
        )
        val p3Id = patientDao.insertPatient(patient3)

        // Samples
        val sample1 = Sample(
            patientId = p1Id,
            sampleNumber = "SMP-2026-001",
            sampleType = "مصل دم (Serum Gold/Red)",
            status = Sample.STATUS_COMPLETED,
            notes = "صائم 10 ساعات"
        )
        val s1Id = sampleDao.insertSample(sample1)

        val sample2 = Sample(
            patientId = p1Id,
            sampleNumber = "SMP-2026-002",
            sampleType = "دم كامل (EDTA Purple)",
            status = Sample.STATUS_COMPLETED,
            notes = "فحص صورة الدم"
        )
        val s2Id = sampleDao.insertSample(sample2)

        val sample3 = Sample(
            patientId = p2Id,
            sampleNumber = "SMP-2026-003",
            sampleType = "مصل دم (Serum Gold/Red)",
            status = Sample.STATUS_IN_ANALYSIS,
            notes = "فحص هرمونات الغدة الدرقية"
        )
        val s3Id = sampleDao.insertSample(sample3)

        val sample4 = Sample(
            patientId = p3Id,
            sampleNumber = "SMP-2026-004",
            sampleType = "عينة بول عشوائية (Random Urine)",
            status = Sample.STATUS_PENDING,
            notes = "مستلمة حديثاً"
        )
        sampleDao.insertSample(sample4)

        // Results for Sample 1
        resultDao.insertResult(
            LaboratoryResult(
                patientId = p1Id,
                sampleId = s1Id,
                testName = "Fasting Blood Glucose (FBS)",
                resultValue = "118",
                unit = "mg/dL",
                referenceRange = "70 - 100",
                resultStatus = LaboratoryResult.STATUS_ABNORMAL,
                notes = "مرتفع قليلاً - سكري منضبط جزئياً"
            )
        )
        resultDao.insertResult(
            LaboratoryResult(
                patientId = p1Id,
                sampleId = s1Id,
                testName = "Serum Creatinine",
                resultValue = "1.0",
                unit = "mg/dL",
                referenceRange = "0.7 - 1.3",
                resultStatus = LaboratoryResult.STATUS_NORMAL,
                notes = "ضمن المعدل الطبيعي"
            )
        )

        // Results for Sample 2
        resultDao.insertResult(
            LaboratoryResult(
                patientId = p1Id,
                sampleId = s2Id,
                testName = "Hemoglobin (Hb)",
                resultValue = "14.2",
                unit = "g/dL",
                referenceRange = "12.0 - 17.5",
                resultStatus = LaboratoryResult.STATUS_NORMAL,
                notes = "طبيعي"
            )
        )
        resultDao.insertResult(
            LaboratoryResult(
                patientId = p1Id,
                sampleId = s2Id,
                testName = "Platelets Count (PLT)",
                resultValue = "240",
                unit = "x10^3/uL",
                referenceRange = "150 - 450",
                resultStatus = LaboratoryResult.STATUS_NORMAL,
                notes = "طبيعي"
            )
        )

        // Results for Sample 3
        resultDao.insertResult(
            LaboratoryResult(
                patientId = p2Id,
                sampleId = s3Id,
                testName = "Thyroid Stimulating Hormone (TSH)",
                resultValue = "2.35",
                unit = "uIU/mL",
                referenceRange = "0.4 - 4.0",
                resultStatus = LaboratoryResult.STATUS_NORMAL,
                notes = "طبيعي"
            )
        )
    }
}
