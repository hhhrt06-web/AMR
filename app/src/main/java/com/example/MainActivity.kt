package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.PatientDialog
import com.example.ui.components.ResultDialog
import com.example.ui.components.SampleDialog
import com.example.ui.navigation.BottomNavTab
import com.example.ui.navigation.Screen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LaboratoryTestsScreen
import com.example.ui.screens.PatientProfileScreen
import com.example.ui.screens.PatientsScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.screens.SampleRegistrationScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LabViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LabViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Arabic-first RTL Layout Enforcement
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainAppContent(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: LabViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val patients by viewModel.patients.collectAsState()
    val samples by viewModel.samples.collectAsState()
    val tests by viewModel.tests.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val showAddPatient by viewModel.showAddPatientDialog.collectAsState()
    val editingPatient by viewModel.editingPatient.collectAsState()
    val patientToDelete by viewModel.patientToDelete.collectAsState()

    val showAddSample by viewModel.showAddSampleDialog.collectAsState()
    val sampleToEdit by viewModel.sampleToEdit.collectAsState()
    val sampleToDelete by viewModel.sampleToDelete.collectAsState()
    val preselectedPatientForSample by viewModel.preselectedPatientForSample.collectAsState()

    val showAddResult by viewModel.showAddResultDialog.collectAsState()
    val resultToEdit by viewModel.resultToEdit.collectAsState()
    val resultToDelete by viewModel.resultToDelete.collectAsState()
    val preselectedSampleForResult by viewModel.preselectedSampleForResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        topBar = {
            if (currentScreen != Screen.PatientProfile) {
                TopAppBar(
                    title = {
                        Text(
                            text = currentScreen.titleAr,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                tonalElevation = 4.dp
            ) {
                val currentTab = when (currentScreen) {
                    Screen.Dashboard -> BottomNavTab.DASHBOARD
                    Screen.Patients, Screen.PatientProfile -> BottomNavTab.PATIENTS
                    Screen.SampleRegistration -> BottomNavTab.SAMPLES
                    Screen.Results -> BottomNavTab.RESULTS
                    Screen.LaboratoryTests -> BottomNavTab.TESTS
                    Screen.Search -> BottomNavTab.SEARCH
                    Screen.Settings -> BottomNavTab.SETTINGS
                }

                NavigationBarItem(
                    selected = currentTab == BottomNavTab.DASHBOARD,
                    onClick = { viewModel.navigateTo(Screen.Dashboard) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "الرئيسية") },
                    label = { Text("الرئيسية", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentTab == BottomNavTab.PATIENTS,
                    onClick = { viewModel.navigateTo(Screen.Patients) },
                    icon = { Icon(Icons.Default.People, contentDescription = "المرضى") },
                    label = { Text("المرضى", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentTab == BottomNavTab.SAMPLES,
                    onClick = { viewModel.navigateTo(Screen.SampleRegistration) },
                    icon = { Icon(Icons.Default.Science, contentDescription = "العينات") },
                    label = { Text("العينات", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentTab == BottomNavTab.RESULTS,
                    onClick = { viewModel.navigateTo(Screen.Results) },
                    icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = "النتائج") },
                    label = { Text("النتائج", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentTab == BottomNavTab.TESTS,
                    onClick = { viewModel.navigateTo(Screen.LaboratoryTests) },
                    icon = { Icon(Icons.Default.Biotech, contentDescription = "الفحوصات") },
                    label = { Text("الفحوصات", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentTab == BottomNavTab.SEARCH,
                    onClick = { viewModel.navigateTo(Screen.Search) },
                    icon = { Icon(Icons.Default.Search, contentDescription = "البحث") },
                    label = { Text("البحث", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentTab == BottomNavTab.SETTINGS,
                    onClick = { viewModel.navigateTo(Screen.Settings) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "الإعدادات") },
                    label = { Text("الإعدادات", fontSize = 10.sp) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                Screen.Dashboard -> DashboardScreen(viewModel = viewModel, innerPadding = innerPadding)
                Screen.Patients -> PatientsScreen(viewModel = viewModel, innerPadding = innerPadding)
                Screen.PatientProfile -> PatientProfileScreen(viewModel = viewModel, innerPadding = innerPadding)
                Screen.SampleRegistration -> SampleRegistrationScreen(viewModel = viewModel, innerPadding = innerPadding)
                Screen.LaboratoryTests -> LaboratoryTestsScreen(viewModel = viewModel, innerPadding = innerPadding)
                Screen.Results -> ResultsScreen(viewModel = viewModel, innerPadding = innerPadding)
                Screen.Search -> SearchScreen(viewModel = viewModel, innerPadding = innerPadding)
                Screen.Settings -> SettingsScreen(viewModel = viewModel, innerPadding = innerPadding)
            }
        }
    }

    // Patient Dialog
    if (showAddPatient) {
        PatientDialog(
            initialPatient = editingPatient,
            onDismiss = {
                viewModel.showAddPatientDialog.value = false
                viewModel.editingPatient.value = null
            },
            onSave = { id, name, age, gender, phone, notes ->
                viewModel.savePatient(id, name, age, gender, phone, notes)
            }
        )
    }

    // Delete Patient Confirmation
    if (patientToDelete != null) {
        ConfirmDeleteDialog(
            title = "تأكيد حذف المريض",
            message = "هل أنت متأكد من رغبتك في حذف ملف المريض (${patientToDelete!!.fullName})؟ سيؤدي ذلك لحذف جميع عيناته ونتائجه المخبرية المرتبطة به نهائياً من قاعدة البيانات.",
            onConfirm = { viewModel.deletePatient(patientToDelete!!) },
            onDismiss = { viewModel.patientToDelete.value = null }
        )
    }

    // Sample Dialog
    if (showAddSample) {
        SampleDialog(
            patients = patients,
            preselectedPatient = preselectedPatientForSample,
            initialSample = sampleToEdit,
            onDismiss = {
                viewModel.showAddSampleDialog.value = false
                viewModel.sampleToEdit.value = null
                viewModel.preselectedPatientForSample.value = null
            },
            onSave = { sampleId, patientId, sampleType, status, notes ->
                viewModel.saveSample(sampleId, patientId, sampleType, status, notes)
            }
        )
    }

    // Delete Sample Confirmation
    if (sampleToDelete != null) {
        ConfirmDeleteDialog(
            title = "تأكيد حذف العينة",
            message = "هل أنت متأكد من حذف العينة (${sampleToDelete!!.sampleNumber})؟ سيتم حذف جميع الفحوصات والنتائج المدخلة لها.",
            onConfirm = { viewModel.deleteSample(sampleToDelete!!) },
            onDismiss = { viewModel.sampleToDelete.value = null }
        )
    }

    // Result Dialog
    if (showAddResult) {
        ResultDialog(
            samples = samples,
            testDefinitions = tests,
            preselectedSample = preselectedSampleForResult,
            initialResult = resultToEdit,
            onDismiss = {
                viewModel.showAddResultDialog.value = false
                viewModel.resultToEdit.value = null
                viewModel.preselectedSampleForResult.value = null
            },
            onSave = { resultId, patientId, sampleId, testName, value, unit, refRange, status, notes ->
                viewModel.saveResult(resultId, patientId, sampleId, testName, value, unit, refRange, status, notes)
            },
            evaluateStatus = { testName, valueStr ->
                viewModel.evaluateStatus(testName, valueStr)
            }
        )
    }

    // Delete Result Confirmation
    if (resultToDelete != null) {
        ConfirmDeleteDialog(
            title = "تأكيد حذف النتيجة",
            message = "هل أنت متأكد من حذف نتيجة فحص (${resultToDelete!!.testName}) للمريض (${resultToDelete!!.patientName})؟",
            onConfirm = { viewModel.deleteResult(resultToDelete!!) },
            onDismiss = { viewModel.resultToDelete.value = null }
        )
    }
}
