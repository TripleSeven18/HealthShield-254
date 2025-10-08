package com.triple7.healthshield254.ui.screens.reportmedicine

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.FirebaseDatabase
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.launch

/* --------------------------------------------------------------------------
   🧩 Data Model: Represents a fake medicine report entry for Firebase
-------------------------------------------------------------------------- */
data class FakeMedicineReport(
    val distributorName: String = "",
    val medicineName: String = "",
    val batchNumber: String = "",
    val location: String = "",
    val severity: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/* --------------------------------------------------------------------------
   💡 ViewModel: Handles data submission to Firebase
-------------------------------------------------------------------------- */
class ReportViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("FakeMedicineReports")

    fun submitReport(
        report: FakeMedicineReport,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            val newEntry = database.push()
            newEntry.setValue(report)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        }
    }
}

/* --------------------------------------------------------------------------
   🧠 Composable: The pharmacist report screen
-------------------------------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    viewModel: ReportViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    var distributorName by remember { mutableStateOf("") }
    var medicineName by remember { mutableStateOf("") }
    var batchNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("Moderate") }
    var isSubmitted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scale by animateFloatAsState(if (isSubmitted) 1.1f else 1f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Report Fake Distributor", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = tripleSeven) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    selected = false,
                    onClick = { /* navController.navigate("home") */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Report") },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    selected = false,
                    onClick = { /* navController.navigate("profile") */ }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Help keep our medicine supply safe. Please provide as many details as possible.",
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Distributor Name
            OutlinedTextField(
                value = distributorName,
                onValueChange = { distributorName = it },
                label = { Text("Distributor / Supplier Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // Medicine Name
            OutlinedTextField(
                value = medicineName,
                onValueChange = { medicineName = it },
                label = { Text("Medicine Name") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // Batch Number
            OutlinedTextField(
                value = batchNumber,
                onValueChange = { batchNumber = it },
                label = { Text("Batch Number (if available)") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(12.dp))

            // Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location / Pharmacy Name") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // Severity Dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = severity,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Severity Level") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Low", "Moderate", "High", "Critical").forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level) },
                            onClick = {
                                severity = level
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Describe the issue (wrong details, packaging, etc.)") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    if (distributorName.isBlank() || medicineName.isBlank() || location.isBlank()) {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    isLoading = true
                    val report = FakeMedicineReport(
                        distributorName = distributorName,
                        medicineName = medicineName,
                        batchNumber = batchNumber,
                        location = location,
                        severity = severity,
                        description = description
                    )

                    viewModel.submitReport(report,
                        onSuccess = {
                            isSubmitted = true
                            isLoading = false
                            Toast.makeText(
                                context,
                                "✅ Report submitted successfully!",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Reset form
                            distributorName = ""
                            medicineName = ""
                            batchNumber = ""
                            location = ""
                            description = ""
                            severity = "Moderate"
                        },
                        onFailure = {
                            isLoading = false
                            Toast.makeText(
                                context,
                                "❌ Failed: ${it.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .scale(scale),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = tripleSeven)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Submit Report", fontSize = 16.sp)
                }
            }

            if (isSubmitted) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Report saved successfully in Firebase Database!",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

/* --------------------------------------------------------------------------
   🧪 Preview
-------------------------------------------------------------------------- */
@Preview(showBackground = true)
@Composable
fun ReportScreenPreview() {
    ReportScreen(rememberNavController())
}
