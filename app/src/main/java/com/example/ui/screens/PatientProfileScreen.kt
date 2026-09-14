package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.LaboratoryResult
import com.example.data.local.entity.ResultDetail
import com.example.data.local.entity.Sample
import com.example.data.local.entity.SampleDetail
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ResultStatusBadge
import com.example.ui.components.SampleStatusBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.LabCriticalRed
import com.example.ui.theme.LabNormalGreen
import com.example.ui.theme.LabTealContainer
import com.example.ui.theme.LabTealOnContainer
import com.example.ui.theme.LabTealPrimary
import com.example.viewmodel.LabViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    viewModel: LabViewModel,
    innerPadding: PaddingValues
) {
    val patient by viewModel.selectedPatient.collectAsState()
    val samples by viewModel.selectedPatientSamples.collectAsState()
    val results by viewModel.selectedPatientResults.collectAsState()

    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

    if (patient == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("لم يتم العثور على سجل المريض", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { viewModel.navigateTo(Screen.Patients) }) {
                    Text("العودة لسجل المرضى")
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(patient!!.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("ملف المريض المخبري (${patient!!.patientNumber})", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Patients) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.editingPatient.value = patient
                        viewModel.showAddPatientDialog.value = true
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "تعديل المريض")
                    }
                    IconButton(onClick = { viewModel.patientToDelete.value = patient }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف المريض", tint = LabCriticalRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Patient Demographics Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "البيانات الديموغرافية والسريرية",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(LabTealContainer)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = patient!!.patientNumber,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = LabTealOnContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("العمر / الجنس:", fontSize = 11.sp, color = Color.Gray)
                                Text("${patient!!.age} سنة / ${patient!!.gender}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("رقم الجوال:", fontSize = 11.sp, color = Color.Gray)
                                Text(patient!!.phone.ifBlank { "غير مسجل" }, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }

                        if (patient!!.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("ملاحظات / تشخيص سريري:", fontSize = 11.sp, color = Color.Gray)
                            Text(patient!!.notes, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.openAddSampleForPatient(patient!!) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LabTealPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("سحب عينة مخبرية جديدة لهذا المريض", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Samples Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "العينات المخبرية المسجلة (${samples.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            if (samples.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("لا توجد عينات مسجلة لهذا المريض حالياً.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                items(samples, key = { it.id }) { sample ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = sample.sampleNumber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${dateFormat.format(Date(sample.collectionDateTime))}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                SampleStatusBadge(status = sample.status)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "نوع العينة: ${sample.sampleType}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            if (sample.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ملاحظات: ${sample.notes}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val sampleDetail = SampleDetail(
                                            sampleId = sample.id,
                                            sampleNumber = sample.sampleNumber,
                                            sampleType = sample.sampleType,
                                            collectionDateTime = sample.collectionDateTime,
                                            status = sample.status,
                                            sampleNotes = sample.notes,
                                            patientId = patient!!.id,
                                            patientName = patient!!.fullName,
                                            patientNumber = patient!!.patientNumber
                                        )
                                        viewModel.openAddResultForSample(sampleDetail)
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إدخال نتائج لهذه العينة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Row {
                                    IconButton(
                                        onClick = {
                                            val sampleDetail = SampleDetail(
                                                sampleId = sample.id,
                                                sampleNumber = sample.sampleNumber,
                                                sampleType = sample.sampleType,
                                                collectionDateTime = sample.collectionDateTime,
                                                status = sample.status,
                                                sampleNotes = sample.notes,
                                                patientId = patient!!.id,
                                                patientName = patient!!.fullName,
                                                patientNumber = patient!!.patientNumber
                                            )
                                            viewModel.sampleToEdit.value = sampleDetail
                                            viewModel.showAddSampleDialog.value = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "تعديل", modifier = Modifier.size(16.dp), tint = Color.Gray)
                                    }

                                    IconButton(
                                        onClick = {
                                            val sampleDetail = SampleDetail(
                                                sampleId = sample.id,
                                                sampleNumber = sample.sampleNumber,
                                                sampleType = sample.sampleType,
                                                collectionDateTime = sample.collectionDateTime,
                                                status = sample.status,
                                                sampleNotes = sample.notes,
                                                patientId = patient!!.id,
                                                patientName = patient!!.fullName,
                                                patientNumber = patient!!.patientNumber
                                            )
                                            viewModel.sampleToDelete.value = sampleDetail
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", modifier = Modifier.size(16.dp), tint = LabCriticalRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Results Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "النتائج والتقارير المخبرية (${results.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            if (results.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("لا توجد نتائج مسجلة حتى الآن. استخدم زر إدخال النتائج أعلاه.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                items(results, key = { it.resultId }) { res ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(res.testName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("العينة: ${res.sampleNumber} • ${dateFormat.format(Date(res.dateTime))}", fontSize = 11.sp, color = Color.Gray)
                                }

                                ResultStatusBadge(status = res.resultStatus)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "النتيجة: ${res.resultValue} ${res.unit}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = "المعدل: ${res.referenceRange}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            if (res.resultNotes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("ملاحظات: ${res.resultNotes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.resultToEdit.value = res
                                        viewModel.showAddResultDialog.value = true
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "تعديل", modifier = Modifier.size(16.dp), tint = Color.Gray)
                                }

                                IconButton(
                                    onClick = { viewModel.resultToDelete.value = res },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", modifier = Modifier.size(16.dp), tint = LabCriticalRed)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
