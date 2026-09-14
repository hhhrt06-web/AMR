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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.Sample
import com.example.data.local.entity.SampleDetail
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.SampleStatusBadge
import com.example.ui.theme.LabCriticalRed
import com.example.viewmodel.LabViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SampleRegistrationScreen(
    viewModel: LabViewModel,
    innerPadding: PaddingValues
) {
    val samples by viewModel.samples.collectAsState()
    var searchInput by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }

    val statusFilters = listOf(
        "ALL" to "الكل",
        Sample.STATUS_PENDING to "معلقة",
        Sample.STATUS_RECEIVED to "مستلمة",
        Sample.STATUS_IN_ANALYSIS to "قيد التحليل",
        Sample.STATUS_COMPLETED to "مكتملة",
        Sample.STATUS_REJECTED to "مرفوضة"
    )

    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

    val filteredSamples = remember(samples, searchInput, selectedStatusFilter) {
        samples.filter { sample ->
            val matchStatus = (selectedStatusFilter == "ALL") || (sample.status == selectedStatusFilter)
            val matchSearch = if (searchInput.isBlank()) true else {
                val q = searchInput.trim().lowercase()
                sample.sampleNumber.lowercase().contains(q) ||
                sample.patientName.lowercase().contains(q) ||
                sample.patientNumber.lowercase().contains(q) ||
                sample.sampleType.lowercase().contains(q)
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
                    viewModel.sampleToEdit.value = null
                    viewModel.preselectedPatientForSample.value = null
                    viewModel.showAddSampleDialog.value = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Science, contentDescription = "تسجيل عينة جديدة")
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
                text = "تسجيل العينات المخبرية (${samples.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "تتبع العينات، الباركودات، ربطها بالمرضى وتحديث حالتها",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchInput,
                onValueChange = { searchInput = it },
                placeholder = { Text("بحث برقم العينة، المريض، أو نوع العينة...") },
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

            if (filteredSamples.isEmpty()) {
                EmptyStateCard(
                    title = "لا توجد عينات مطابقة",
                    description = "لم يتم العثور على أي عينات تطابق الفلتر أو البحث الحالي.",
                    icon = Icons.Default.Science,
                    actionText = "تسجيل عينة جديدة",
                    onAction = {
                        viewModel.sampleToEdit.value = null
                        viewModel.showAddSampleDialog.value = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredSamples, key = { it.sampleId }) { item ->
                        SampleCardItem(
                            sample = item,
                            dateFormat = dateFormat,
                            onOpenPatient = { viewModel.openPatientProfile(item.patientId) },
                            onEdit = {
                                viewModel.sampleToEdit.value = item
                                viewModel.showAddSampleDialog.value = true
                            },
                            onDelete = { viewModel.sampleToDelete.value = item },
                            onAddResult = { viewModel.openAddResultForSample(item) },
                            onStatusChange = { newStatus ->
                                viewModel.updateSampleStatus(item.sampleId, newStatus)
                            }
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
fun SampleCardItem(
    sample: SampleDetail,
    dateFormat: SimpleDateFormat,
    onOpenPatient: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddResult: () -> Unit,
    onStatusChange: (String) -> Unit
) {
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
                Column {
                    Text(
                        text = sample.sampleNumber,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = dateFormat.format(Date(sample.collectionDateTime)),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                SampleStatusBadge(status = sample.status)
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
                    text = "${sample.patientName} (${sample.patientNumber})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "نوع العينة: ${sample.sampleType}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            if (sample.sampleNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ملاحظات السحب: ${sample.sampleNotes}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Status Actions and Add Result
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onAddResult,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إدخال نتائج", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Row {
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
}
