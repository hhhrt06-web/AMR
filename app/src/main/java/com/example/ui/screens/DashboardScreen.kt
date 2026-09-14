package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.LaboratoryResult
import com.example.ui.components.ResultStatusBadge
import com.example.ui.components.SampleStatusBadge
import com.example.ui.components.StatSummaryCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.LabCriticalRed
import com.example.ui.theme.LabCriticalRedContainer
import com.example.ui.theme.LabNormalGreen
import com.example.ui.theme.LabNormalGreenContainer
import com.example.ui.theme.LabTealContainer
import com.example.ui.theme.LabTealOnContainer
import com.example.ui.theme.LabTealPrimary
import com.example.ui.theme.LabWarningContainer
import com.example.ui.theme.LabWarningOrange
import com.example.viewmodel.LabViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: LabViewModel,
    innerPadding: PaddingValues
) {
    val patientCount by viewModel.patientCount.collectAsState()
    val pendingSamplesCount by viewModel.pendingSamplesCount.collectAsState()
    val completedResultsCount by viewModel.completedResultsCount.collectAsState()
    val testCount by viewModel.testCount.collectAsState()
    val samples by viewModel.samples.collectAsState()
    val results by viewModel.results.collectAsState()

    val pendingSamples = samples.filter { it.status != "COMPLETED" && it.status != "REJECTED" }.take(5)
    val criticalOrAbnormal = results.filter { it.resultStatus == LaboratoryResult.STATUS_CRITICAL || it.resultStatus == LaboratoryResult.STATUS_ABNORMAL }.take(5)

    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "نظام مختبر LABCORE الطبي",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "إدارة السجلات المخبرية والتحاليل الطبية أوفلاين",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(LabTealContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = LabTealOnContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Quick Stats Bento Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatSummaryCard(
                        title = "إجمالي المرضى",
                        count = patientCount,
                        subtitle = "سجلات محفوظة محلياً",
                        icon = Icons.Default.People,
                        containerColor = LabTealContainer,
                        contentColor = LabTealOnContainer,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Patients) }
                    )

                    StatSummaryCard(
                        title = "عينات قيد الانتظار",
                        count = pendingSamplesCount,
                        subtitle = "تتطلب المعالجة",
                        icon = Icons.Default.HourglassTop,
                        containerColor = LabWarningContainer,
                        contentColor = LabWarningOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.SampleRegistration) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatSummaryCard(
                        title = "النتائج المكتملة",
                        count = completedResultsCount,
                        subtitle = "فحوصات تم إنجازها",
                        icon = Icons.Default.AssignmentTurnedIn,
                        containerColor = LabNormalGreenContainer,
                        contentColor = LabNormalGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Results) }
                    )

                    StatSummaryCard(
                        title = "دليل الفحوصات",
                        count = testCount,
                        subtitle = "فحص معتمد ومصنف",
                        icon = Icons.Default.Biotech,
                        containerColor = Color(0xFFEDE9FE),
                        contentColor = Color(0xFF6B21A8),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.LaboratoryTests) }
                    )
                }
            }
        }

        // Quick Action Buttons (إجراءات سريعة)
        item {
            Text(
                text = "الإجراءات السريعة",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.PersonAdd,
                    label = "تسجيل مريض",
                    onClick = { viewModel.showAddPatientDialog.value = true },
                    modifier = Modifier.weight(1f)
                )

                QuickActionButton(
                    icon = Icons.Default.Science,
                    label = "سحب عينة",
                    onClick = { viewModel.showAddSampleDialog.value = true },
                    modifier = Modifier.weight(1f)
                )

                QuickActionButton(
                    icon = Icons.Default.PostAdd,
                    label = "إدخال نتيجة",
                    onClick = { viewModel.showAddResultDialog.value = true },
                    modifier = Modifier.weight(1f)
                )

                QuickActionButton(
                    icon = Icons.Default.Search,
                    label = "بحث شامل",
                    onClick = { viewModel.navigateTo(Screen.Search) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Pending Samples Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "العينات الجارية والمعلقة (${pendingSamples.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "عرض الكل ←",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.SampleRegistration) }
                )
            }
        }

        if (pendingSamples.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("لا توجد عينات معلقة حالياً - جميع العينات مكتملة التحليل", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        } else {
            items(pendingSamples) { sample ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openPatientProfile(sample.patientId) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(sample.sampleNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("• ${sample.patientName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(sample.sampleType, fontSize = 11.sp, color = Color.Gray)
                        }

                        SampleStatusBadge(status = sample.status)
                    }
                }
            }
        }

        // Critical / Abnormal Alerts Section
        if (criticalOrAbnormal.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = LabCriticalRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تنبيهات النتائج المخبرية الحرجة وغير الطبيعية",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = LabCriticalRed
                    )
                }
            }

            items(criticalOrAbnormal) { res ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openPatientProfile(res.patientId) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (res.resultStatus == LaboratoryResult.STATUS_CRITICAL) LabCriticalRedContainer else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (res.resultStatus == LaboratoryResult.STATUS_CRITICAL) LabCriticalRed else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(res.testName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = "المريض: ${res.patientName} (${res.patientNumber})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "القيمة: ${res.resultValue} ${res.unit} (المعدل: ${res.referenceRange})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (res.resultStatus == LaboratoryResult.STATUS_CRITICAL) LabCriticalRed else LabWarningOrange
                            )
                        }

                        ResultStatusBadge(status = res.resultStatus)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LabTealContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = LabTealOnContainer, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}
