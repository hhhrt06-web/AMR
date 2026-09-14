package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.LaboratoryResult
import com.example.data.local.entity.Patient
import com.example.data.local.entity.ResultDetail
import com.example.data.local.entity.Sample
import com.example.data.local.entity.SampleDetail
import com.example.data.local.entity.TestDefinition
import com.example.ui.theme.LabTealPrimary

@Composable
fun PatientDialog(
    initialPatient: Patient? = null,
    onDismiss: () -> Unit,
    onSave: (id: Long, name: String, age: Int, gender: String, phone: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf(initialPatient?.fullName ?: "") }
    var ageStr by remember { mutableStateOf(initialPatient?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(initialPatient?.gender ?: "ذكر") }
    var phone by remember { mutableStateOf(initialPatient?.phone ?: "") }
    var notes by remember { mutableStateOf(initialPatient?.notes ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialPatient == null) "تسجيل مريض جديد" else "تعديل بيانات المريض",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Full Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorText = null },
                    label = { Text("الاسم الكامل للمريض *") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Age & Gender
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it; errorText = null },
                        label = { Text("العمر (سنة) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Column(modifier = Modifier.weight(1.2f)) {
                        Text("الجنس:", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { gender = "ذكر" }
                            ) {
                                RadioButton(selected = gender == "ذكر", onClick = { gender = "ذكر" })
                                Text("ذكر", fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { gender = "أنثى" }
                            ) {
                                RadioButton(selected = gender == "أنثى", onClick = { gender = "أنثى" })
                                Text("أنثى", fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم الهاتف / الجوال") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات سريرية أو تشخيص أولي") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorText = "يرجى كتابة اسم المريض"
                                return@Button
                            }
                            val age = ageStr.toIntOrNull()
                            if (age == null || age <= 0) {
                                errorText = "يرجى إدخال عمر صحيح"
                                return@Button
                            }
                            onSave(initialPatient?.id ?: 0L, name, age, gender, phone, notes)
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (initialPatient == null) "تسجيل وحفظ" else "حفظ التعديلات", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SampleDialog(
    patients: List<Patient>,
    preselectedPatient: Patient? = null,
    initialSample: SampleDetail? = null,
    onDismiss: () -> Unit,
    onSave: (sampleId: Long, patientId: Long, sampleType: String, status: String, notes: String) -> Unit
) {
    var selectedPatient by remember {
        mutableStateOf(
            preselectedPatient ?: patients.firstOrNull { it.id == initialSample?.patientId } ?: patients.firstOrNull()
        )
    }

    val sampleTypes = listOf(
        "دم كامل (Whole Blood - EDTA)",
        "مصل دم (Serum - Gold/Red)",
        "بلازما دم (Plasma - Citrate/Heparin)",
        "عينة بول (Random Urine)",
        "عينة بول 24 ساعة (24h Urine)",
        "مسحة طبية (Swab)",
        "عينة براز (Stool)",
        "سائل نخاعي شوكي (CSF)"
    )

    val statuses = listOf(
        Sample.STATUS_PENDING to "معلقة (Pending)",
        Sample.STATUS_RECEIVED to "مستلمة (Received)",
        Sample.STATUS_IN_ANALYSIS to "قيد التحليل (In Analysis)",
        Sample.STATUS_COMPLETED to "مكتملة (Completed)",
        Sample.STATUS_REJECTED to "مرفوضة (Rejected)"
    )

    var sampleType by remember { mutableStateOf(initialSample?.sampleType ?: sampleTypes.first()) }
    var status by remember { mutableStateOf(initialSample?.status ?: Sample.STATUS_PENDING) }
    var notes by remember { mutableStateOf(initialSample?.sampleNotes ?: "") }

    var expandedPatientDropdown by remember { mutableStateOf(false) }
    var expandedTypeDropdown by remember { mutableStateOf(false) }
    var expandedStatusDropdown by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialSample == null) "تسجيل عينة مخبرية جديدة" else "تعديل بيانات العينة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Patient Selection
                Text("المريض صاحب العينة *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(enabled = preselectedPatient == null && initialSample == null) {
                            expandedPatientDropdown = true
                        }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedPatient?.fullName ?: "اختر المريض...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (selectedPatient != null) {
                                Text(
                                    text = "رقم المريض: ${selectedPatient!!.patientNumber} • ${selectedPatient!!.gender} (${selectedPatient!!.age} سنة)",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        if (preselectedPatient == null && initialSample == null) {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = expandedPatientDropdown,
                        onDismissRequest = { expandedPatientDropdown = false },
                        modifier = Modifier.heightIn(max = 280.dp)
                    ) {
                        patients.forEach { p ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(p.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${p.patientNumber} • ${p.phone}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    selectedPatient = p
                                    expandedPatientDropdown = false
                                    errorText = null
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sample Type Dropdown
                Text("نوع العينة وأنبوب السحب *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                        .clickable { expandedTypeDropdown = true }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(sampleType, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = expandedTypeDropdown,
                        onDismissRequest = { expandedTypeDropdown = false }
                    ) {
                        sampleTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type, fontSize = 13.sp) },
                                onClick = {
                                    sampleType = type
                                    expandedTypeDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Status
                Text("حالة العينة *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                        .clickable { expandedStatusDropdown = true }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statusLabel = statuses.firstOrNull { it.first == status }?.second ?: status
                        Text(statusLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = expandedStatusDropdown,
                        onDismissRequest = { expandedStatusDropdown = false }
                    ) {
                        statuses.forEach { (st, label) ->
                            DropdownMenuItem(
                                text = { Text(label, fontSize = 13.sp) },
                                onClick = {
                                    status = st
                                    expandedStatusDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات السحب (مثال: صائم 10 ساعات، درجة الحرارة، إلخ)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            if (selectedPatient == null) {
                                errorText = "يرجى تحديد المريض أولاً"
                                return@Button
                            }
                            onSave(
                                initialSample?.sampleId ?: 0L,
                                selectedPatient!!.id,
                                sampleType,
                                status,
                                notes
                            )
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (initialSample == null) "تسجيل العينة" else "حفظ التعديل", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ResultDialog(
    samples: List<SampleDetail>,
    testDefinitions: List<TestDefinition>,
    preselectedSample: SampleDetail? = null,
    initialResult: ResultDetail? = null,
    onDismiss: () -> Unit,
    onSave: (resultId: Long, patientId: Long, sampleId: Long, testName: String, value: String, unit: String, refRange: String, status: String, notes: String) -> Unit,
    evaluateStatus: (testName: String, valueStr: String) -> String
) {
    var selectedSample by remember {
        mutableStateOf(
            preselectedSample ?: samples.firstOrNull { it.sampleId == initialResult?.sampleId } ?: samples.firstOrNull()
        )
    }

    var selectedTestDef by remember {
        mutableStateOf(
            testDefinitions.firstOrNull { it.testName == initialResult?.testName } ?: testDefinitions.firstOrNull()
        )
    }

    var testName by remember { mutableStateOf(initialResult?.testName ?: selectedTestDef?.testName ?: "") }
    var resultValue by remember { mutableStateOf(initialResult?.resultValue ?: "") }
    var unit by remember { mutableStateOf(initialResult?.unit ?: selectedTestDef?.unit ?: "") }
    var referenceRange by remember { mutableStateOf(initialResult?.referenceRange ?: selectedTestDef?.referenceRange ?: "") }
    var resultStatus by remember { mutableStateOf(initialResult?.resultStatus ?: "") }
    var notes by remember { mutableStateOf(initialResult?.resultNotes ?: "") }

    var expandedSampleDropdown by remember { mutableStateOf(false) }
    var expandedTestDropdown by remember { mutableStateOf(false) }
    var expandedStatusDropdown by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val statusOptions = listOf(
        LaboratoryResult.STATUS_NORMAL to "طبيعي (Normal)",
        LaboratoryResult.STATUS_ABNORMAL to "غير طبيعي (Abnormal)",
        LaboratoryResult.STATUS_CRITICAL to "حرج (CRITICAL)",
        LaboratoryResult.STATUS_PENDING to "معلق (Pending)"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialResult == null) "إدخال نتيجة فحص مخبري" else "تعديل النتيجة المخبرية",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Sample Selection
                Text("العينة المخبرية المرتبطة *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(enabled = preselectedSample == null && initialResult == null) {
                            expandedSampleDropdown = true
                        }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedSample?.let { "${it.sampleNumber} - ${it.patientName}" } ?: "اختر عينة...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            if (selectedSample != null) {
                                Text(
                                    text = "${selectedSample!!.sampleType} • ${selectedSample!!.patientNumber}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        if (preselectedSample == null && initialResult == null) {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = expandedSampleDropdown,
                        onDismissRequest = { expandedSampleDropdown = false },
                        modifier = Modifier.heightIn(max = 280.dp)
                    ) {
                        samples.forEach { s ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("${s.sampleNumber} - ${s.patientName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${s.sampleType} (${s.status})", fontSize = 11.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    selectedSample = s
                                    expandedSampleDropdown = false
                                    errorText = null
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Test Definition Picker
                Text("اختر نوع الفحص المخبري *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                        .clickable { expandedTestDropdown = true }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(testName.ifBlank { "اختر الفحص من القائمة..." }, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            if (unit.isNotBlank() || referenceRange.isNotBlank()) {
                                Text("الوحدة: $unit | المعدل: $referenceRange", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = expandedTestDropdown,
                        onDismissRequest = { expandedTestDropdown = false },
                        modifier = Modifier.heightIn(max = 320.dp)
                    ) {
                        testDefinitions.forEach { def ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(def.testName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${def.category} • المدى: ${def.referenceRange} ${def.unit}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    selectedTestDef = def
                                    testName = def.testName
                                    unit = def.unit
                                    referenceRange = def.referenceRange
                                    // re-evaluate status if value already typed
                                    if (resultValue.isNotBlank()) {
                                        resultStatus = evaluateStatus(def.testName, resultValue)
                                    }
                                    expandedTestDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Result Value & Unit
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = resultValue,
                        onValueChange = {
                            resultValue = it
                            errorText = null
                            if (it.isNotBlank()) {
                                resultStatus = evaluateStatus(testName, it)
                            }
                        },
                        label = { Text("النتيجة المقروءة *") },
                        placeholder = { Text("مثال: 14.5") },
                        singleLine = true,
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("الوحدة") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Reference Range
                OutlinedTextField(
                    value = referenceRange,
                    onValueChange = { referenceRange = it },
                    label = { Text("المعدل الطبيعي (Reference Range)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Result Status (auto-evaluated or manual override)
                Text("حالة النتيجة المخبرية (تقييم آلي/يدوي) *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                        .clickable { expandedStatusDropdown = true }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val currentLabel = statusOptions.firstOrNull { it.first == resultStatus }?.second
                            ?: if (resultStatus.isNotBlank()) resultStatus else "طبيعي (Normal)"
                        Text(currentLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = expandedStatusDropdown,
                        onDismissRequest = { expandedStatusDropdown = false }
                    ) {
                        statusOptions.forEach { (st, label) ->
                            DropdownMenuItem(
                                text = { Text(label, fontSize = 13.sp) },
                                onClick = {
                                    resultStatus = st
                                    expandedStatusDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Result Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات فنية / تكرار الفحص") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            if (selectedSample == null) {
                                errorText = "يرجى تحديد العينة أولاً"
                                return@Button
                            }
                            if (testName.isBlank()) {
                                errorText = "يرجى اختيار اسم الفحص"
                                return@Button
                            }
                            if (resultValue.isBlank()) {
                                errorText = "يرجى إدخال قيمة النتيجة"
                                return@Button
                            }
                            val finalStatus = if (resultStatus.isBlank()) {
                                evaluateStatus(testName, resultValue)
                            } else {
                                resultStatus
                            }
                            onSave(
                                initialResult?.resultId ?: 0L,
                                selectedSample!!.patientId,
                                selectedSample!!.sampleId,
                                testName,
                                resultValue,
                                unit,
                                referenceRange,
                                finalStatus,
                                notes
                            )
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (initialResult == null) "حفظ النتيجة" else "تحديث النتيجة", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
