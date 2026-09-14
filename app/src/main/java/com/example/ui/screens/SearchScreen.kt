package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ResultStatusBadge
import com.example.ui.components.SampleStatusBadge
import com.example.viewmodel.LabViewModel

@Composable
fun SearchScreen(
    viewModel: LabViewModel,
    innerPadding: PaddingValues
) {
    val patients by viewModel.patients.collectAsState()
    val samples by viewModel.samples.collectAsState()
    val results by viewModel.results.collectAsState()

    var query by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("الكل", "المرضى", "العينات", "النتائج")

    val q = query.trim().lowercase()

    val matchedPatients = remember(patients, q) {
        if (q.isBlank()) emptyList() else patients.filter {
            it.fullName.lowercase().contains(q) ||
            it.patientNumber.lowercase().contains(q) ||
            it.phone.contains(q)
        }
    }

    val matchedSamples = remember(samples, q) {
        if (q.isBlank()) emptyList() else samples.filter {
            it.sampleNumber.lowercase().contains(q) ||
            it.patientName.lowercase().contains(q) ||
            it.patientNumber.lowercase().contains(q) ||
            it.sampleType.lowercase().contains(q)
        }
    }

    val matchedResults = remember(results, q) {
        if (q.isBlank()) emptyList() else results.filter {
            it.testName.lowercase().contains(q) ||
            it.patientName.lowercase().contains(q) ||
            it.patientNumber.lowercase().contains(q) ||
            it.sampleNumber.lowercase().contains(q)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Screen Header
        Text(
            text = "البحث الشامل في قاعدة البيانات",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "ابحث بالاسم، رقم المريض (P-XXXX)، رقم العينة (SMP-XXXX)، أو اسم الفحص",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("أدخل كلمة البحث هنا...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Row
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (q.isBlank()) {
            EmptyStateCard(
                title = "ابدأ البحث السريع",
                description = "اكتب اسم مريض أو رقم العينة أو اسم الفحص المخبري لعرض كافة النتائج فورياً من قاعدة البيانات المحلية.",
                icon = Icons.Default.Search
            )
        } else {
            val hasAny = matchedPatients.isNotEmpty() || matchedSamples.isNotEmpty() || matchedResults.isNotEmpty()

            if (!hasAny) {
                EmptyStateCard(
                    title = "لا توجد نتائج مطابقة لـ \"$query\"",
                    description = "تأكد من صحة الكلمة المدخلة أو جرّب البحث برقم السجل الطبي.",
                    icon = Icons.Default.Search
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Patients Results
                    if (selectedTabIndex == 0 || selectedTabIndex == 1) {
                        if (matchedPatients.isNotEmpty()) {
                            item {
                                Text(
                                    text = "سجلات المرضى المطابقة (${matchedPatients.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(matchedPatients) { p ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.openPatientProfile(p.id) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(p.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("${p.patientNumber} • ${p.gender} (${p.age} سنة)", fontSize = 11.sp, color = Color.Gray)
                                        }
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }

                    // Samples Results
                    if (selectedTabIndex == 0 || selectedTabIndex == 2) {
                        if (matchedSamples.isNotEmpty()) {
                            item {
                                Text(
                                    text = "العينات المخبرية المطابقة (${matchedSamples.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(matchedSamples) { s ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.openPatientProfile(s.patientId) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(s.sampleNumber, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("المريض: ${s.patientName} (${s.patientNumber})", fontSize = 12.sp)
                                            Text(s.sampleType, fontSize = 11.sp, color = Color.Gray)
                                        }
                                        SampleStatusBadge(status = s.status)
                                    }
                                }
                            }
                        }
                    }

                    // Results Details
                    if (selectedTabIndex == 0 || selectedTabIndex == 3) {
                        if (matchedResults.isNotEmpty()) {
                            item {
                                Text(
                                    text = "النتائج المخبرية المطابقة (${matchedResults.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(matchedResults) { r ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.openPatientProfile(r.patientId) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(r.testName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("المريض: ${r.patientName} • العينة: ${r.sampleNumber}", fontSize = 11.sp, color = Color.Gray)
                                            Text("النتيجة: ${r.resultValue} ${r.unit}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                        ResultStatusBadge(status = r.resultStatus)
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
    }
}
