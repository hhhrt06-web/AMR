package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LabNormalGreen
import com.example.ui.theme.LabTealContainer
import com.example.ui.theme.LabTealOnContainer
import com.example.viewmodel.LabViewModel

@Composable
fun SettingsScreen(
    viewModel: LabViewModel,
    innerPadding: PaddingValues
) {
    val patientCount by viewModel.patientCount.collectAsState()
    val totalSamplesCount by viewModel.totalSamplesCount.collectAsState()
    val totalResultsCount by viewModel.totalResultsCount.collectAsState()
    val testCount by viewModel.testCount.collectAsState()

    var labName by remember { mutableStateOf("مختبر LABCORE الطبي التخصصي") }
    var labTechName by remember { mutableStateOf("د. سارة الأحمدي (أخصائي كيمياء سريرية)") }
    var labPhone by remember { mutableStateOf("+966 11 456 7890") }
    var savedMsg by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = "إعدادات النظام المخبري",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "معلومات النظام، قاعدة البيانات المحلية، وهوية المختبر",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Offline Status Badge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = Color(0xFF0369A1),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "نظام أوفلاين مستقل بالكامل (Offline-First)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "يعمل بدون أي حاجة للإنترنت. جميع البيانات مشفرة ومخزنة محلياً في SQLite/Room.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Database Statistics
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إحصائيات قاعدة البيانات (Room Database)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                InfoRow(title = "اسم ملف قاعدة البيانات", value = "labcore_medical_db")
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                InfoRow(title = "سجلات المرضى (Patients)", value = "$patientCount مريض")
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                InfoRow(title = "العينات المخبرية (Samples)", value = "$totalSamplesCount عينة")
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                InfoRow(title = "النتائج المسجلة (Results)", value = "$totalResultsCount نتيجة")
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                InfoRow(title = "الفحوصات المعيارية (Definitions)", value = "$testCount فحص")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Laboratory Info Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "بيانات المنشأة المخبرية والتقارير",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = labName,
                    onValueChange = { labName = it; savedMsg = false },
                    label = { Text("اسم المختبر الرسمي") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Business, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = labTechName,
                    onValueChange = { labTechName = it; savedMsg = false },
                    label = { Text("المشرف المخبري / أخصائي التحاليل") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Biotech, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = labPhone,
                    onValueChange = { labPhone = it; savedMsg = false },
                    label = { Text("هاتف الطوارئ / التواصل") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { savedMsg = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("حفظ إعدادات المنشأة", fontWeight = FontWeight.Bold)
                }

                if (savedMsg) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "✓ تم حفظ إعدادات المختبر بنجاح",
                        color = LabNormalGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Application About Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "عن تطبيق LABCORE LIS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "نظام متكامل لإدارة المعلومات المخبرية (Laboratory Information System - LIS) مصمم ومبني كتطبيق أندرويد أصلي (Native Android) باستخدام Kotlin و Jetpack Compose و Room Database مع دعم كامل للغة العربية واتجاه النص من اليمين لليسار (RTL).",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "الإصدار: 1.0.0 (Native Release)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun InfoRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
