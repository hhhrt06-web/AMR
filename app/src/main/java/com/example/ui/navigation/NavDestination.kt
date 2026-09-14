package com.example.ui.navigation

sealed class Screen(val route: String, val titleAr: String) {
    object Dashboard : Screen("dashboard", "لوحة التحكم")
    object Patients : Screen("patients", "سجل المرضى")
    object PatientProfile : Screen("patient_profile", "ملف المريض")
    object SampleRegistration : Screen("sample_registration", "تسجيل العينات")
    object LaboratoryTests : Screen("laboratory_tests", "دليل الفحوصات")
    object Results : Screen("results", "سجل النتائج")
    object Search : Screen("search", "البحث الشامل")
    object Settings : Screen("settings", "إعدادات المختبر")
}

enum class BottomNavTab(val route: String, val titleAr: String) {
    DASHBOARD("dashboard", "الرئيسية"),
    PATIENTS("patients", "المرضى"),
    SAMPLES("sample_registration", "العينات"),
    RESULTS("results", "النتائج"),
    TESTS("laboratory_tests", "الفحوصات"),
    SEARCH("search", "البحث"),
    SETTINGS("settings", "الإعدادات")
}
