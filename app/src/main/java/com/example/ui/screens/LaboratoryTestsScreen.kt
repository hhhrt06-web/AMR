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
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TestDefinition
import com.example.ui.components.EmptyStateCard
import com.example.ui.theme.LabTealContainer
import com.example.ui.theme.LabTealOnContainer
import com.example.viewmodel.LabViewModel

@Composable
fun LaboratoryTestsScreen(
    viewModel: LabViewModel,
    innerPadding: PaddingValues
) {
    val tests by viewModel.tests.collectAsState()
    var selectedCategory by remember { mutableStateOf("ALL") }
    var searchInput by remember { mutableStateOf("") }

    val categories = listOf(
        "ALL" to "جميع الفحوصات",
        TestDefinition.CATEGORY_HEMATOLOGY to "أمراض الدم (Hematology)",
        TestDefinition.CATEGORY_BIOCHEMISTRY to "الكيمياء (Biochemistry)",
        TestDefinition.CATEGORY_IMMUNOLOGY to "المناعة (Immunology)",
        TestDefinition.CATEGORY_HORMONES to "الهرمونات (Hormones)",
        TestDefinition.CATEGORY_MICROBIOLOGY to "الأحياء الدقيقة (Microbiology)",
        TestDefinition.CATEGORY_URINALYSIS to "البول والسوائل (Urinalysis)"
    )

    val filteredTests = remember(tests, selectedCategory, searchInput) {
        tests.filter { test ->
            val matchCat = (selectedCategory == "ALL") || (test.category == selectedCategory)
            val matchSearch = if (searchInput.isBlank()) true else {
                val q = searchInput.trim().lowercase()
                test.testName.lowercase().contains(q) ||
                test.testCode.lowercase().contains(q) ||
                test.category.lowercase().contains(q)
            }
            matchCat && matchSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = "دليل الفحوصات المخبرية (${tests.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "الفحوصات الطبية المعتمدة، الوحدات، المعدلات الطبيعية والحدود الحرجة",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Search
        OutlinedTextField(
            value = searchInput,
            onValueChange = { searchInput = it },
            placeholder = { Text("بحث باسم الفحص، الرمز، أو القسم...") },
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

        // Category Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { (key, label) ->
                FilterChip(
                    selected = selectedCategory == key,
                    onClick = { selectedCategory = key },
                    label = { Text(label, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredTests.isEmpty()) {
            EmptyStateCard(
                title = "لا توجد فحوصات مطابقة",
                description = "لم يتم العثور على أي فحص يطابق الفلتر أو البحث المختار.",
                icon = Icons.Default.Biotech
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTests, key = { it.id }) { test ->
                    TestDefinitionCard(
                        test = test,
                        onEnterResult = {
                            viewModel.showAddResultDialog.value = true
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun TestDefinitionCard(
    test: TestDefinition,
    onEnterResult: () -> Unit
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = test.testName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${test.category} • الرمز: ${test.testCode}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LabTealContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = test.testCode,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = LabTealOnContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("المعدل الطبيعي (Ref Range):", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "${test.referenceRange} ${test.unit}".trim(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("العينة الافتراضية:", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = test.defaultSampleType,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (test.criticalMin != null || test.criticalMax != null) {
                Spacer(modifier = Modifier.height(4.dp))
                val critText = buildString {
                    append("القيم الحرجة: ")
                    if (test.criticalMin != null) append("< ${test.criticalMin} ")
                    if (test.criticalMax != null) append("> ${test.criticalMax} ")
                    append(test.unit)
                }
                Text(
                    text = critText,
                    fontSize = 11.sp,
                    color = Color(0xFFBA1A1A),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onEnterResult,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تسجيل نتيجة لهذا الفحص", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
