package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.LaboratoryResult
import com.example.data.local.entity.ResultDetail
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ResultStatusBadge
import com.example.ui.theme.LabCriticalRed
import com.example.ui.theme.LabCriticalRedContainer
import com.example.ui.theme.LabWarningOrange
import com.example.viewmodel.LabViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResultsScreen(
    viewModel: LabViewModel,
    innerPadding: PaddingValues
) {
    val results by viewModel.results.collectAsState()
    var searchInput by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }

    val statusFilters = listOf(
        "ALL" to "الكل",
        LaboratoryResult.STATUS_NORMAL to "طبيعي (Normal)",
        LaboratoryResult.STATUS_ABNORMAL to "غير طبيعي (Abnormal)",
        LaboratoryResult.STATUS_CRITICAL to "حرج (Critical)"
    )

    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

    val filteredResults = remember(results, searchInput, selectedStatusFilter) {
        results.filter { item ->
            val matchStatus = (selectedStatusFilter == "ALL") || (item.resultStatus == selectedStatusFilter)
            val matchSearch = if (searchInput.isBlank()) true else {
                val q = searchInput.trim().lowercase()
                item.testName.lowercase().contains(q) ||
                item.patientName.lowercase().contains(q) ||
                item.patientNumber.lowercase().contains(q) ||
                item.sampleNumber.lowercase().contains(q)
            }
            matchStatus && matchSearch
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.resultToEdit.value = null
                    viewModel.preselectedSampleForResult.value = null
                    viewModel.showAddResultDialog.value = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.PostAdd, contentDescription = "إدخال نتيجة")
            }
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Screen Header
            Text(
                text = "سجل النتائج المخبرية (${results.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "استعراض النتائج، القيم الطبيعية، التنبيهات والوحدات المخبرية",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchInput,
                onValueChange = { searchInput = it },
                placeholder = { Text("بحث باسم الفحص، المريض، أو رقم العينة...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchInput.isNotEmpty()) {
                        IconButton(onClick = { searchInput = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(statusFilters) { (key, label) ->
                    FilterChip(
                        selected = selectedStatusFilter == key,
                        onClick = { selectedStatusFilter = key },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredResults.isEmpty()) {
                EmptyStateCard(
                    title = "لا توجد نتائج مطابقة",
                    description = "لم يتم العثور على أي نتائج مخبرية تطابق معايير البحث والفلترة.",
                    icon = Icons.Default.Assignment,
                    actionText = "إدخال نتيجة جديدة",
                    onAction = {
                        viewModel.resultToEdit.value = null
                        viewModel.showAddResultDialog.value = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredResults, key = { it.resultId }) { item ->
                        ResultCardItem(
                            result = item,
                            dateFormat = dateFormat,
                            onOpenPatient = { viewModel.openPatientProfile(item.patientId) },
                            onEdit = {
                                viewModel.resultToEdit.value = item
                                viewModel.showAddResultDialog.value = true
                            },
                            onDelete = { viewModel.resultToDelete.value = item }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ResultCardItem(
    result: ResultDetail,
    dateFormat: SimpleDateFormat,
    onOpenPatient: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isCritical = result.resultStatus == LaboratoryResult.STATUS_CRITICAL
    val isAbnormal = result.resultStatus == LaboratoryResult.STATUS_ABNORMAL

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCritical) LabCriticalRedContainer else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCritical) LabCriticalRed else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.testName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "العينة: ${result.sampleNumber} • ${dateFormat.format(Date(result.dateTime))}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                ResultStatusBadge(status = result.resultStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPatient() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المريض: ",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "${result.patientName} (${result.patientNumber})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("القيمة المقروءة:", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "${result.resultValue} ${result.unit}".trim(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isCritical -> LabCriticalRed
                            isAbnormal -> LabWarningOrange
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("المعدل الطبيعي:", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "${result.referenceRange} ${result.unit}".trim(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (result.resultNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ملاحظات: ${result.resultNotes}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "تعديل", modifier = Modifier.size(16.dp), tint = Color.Gray)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", modifier = Modifier.size(16.dp), tint = LabCriticalRed)
                }
            }
        }
    }
}
