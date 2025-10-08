package com.triple7.healthshield254.ui.screens.reportmedicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.*
import com.triple7.healthshield254.ui.theme.tripleSeven

/* --------------------------------------------------------------------------
   🧩 Data Model (Reuse from ReportScreen)
-------------------------------------------------------------------------- */


/* --------------------------------------------------------------------------
   💡 ViewModel: Fetch reports from Firebase in real-time
-------------------------------------------------------------------------- */
class ViewReportViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("FakeMedicineReports")

    private val _reports = mutableStateListOf<FakeMedicineReport>()
    val reports: List<FakeMedicineReport> get() = _reports

    private val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            _reports.clear()
            for (child in snapshot.children) {
                val report = child.getValue(FakeMedicineReport::class.java)
                report?.let { _reports.add(it) }
            }
            // Sort latest first
            _reports.sortByDescending { it.timestamp }
        }

        override fun onCancelled(error: DatabaseError) {
            // Log or handle error
        }
    }

    init {
        database.addValueEventListener(listener)
    }

    override fun onCleared() {
        super.onCleared()
        database.removeEventListener(listener)
    }
}

/* --------------------------------------------------------------------------
   🧠 Composable: Screen that displays reports in a modern, clean list
-------------------------------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewReport(
    navController: NavController,
    viewModel: ViewReportViewModel = viewModel()
) {
    val reports by remember { mutableStateOf(viewModel.reports) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.List, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reported Distributors", color = Color.White)
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
                    selected = false,
                    onClick = { /* navController.navigate("report") */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "View Reports") },
                    selected = true,
                    onClick = {}
                )
            }
        }
    ) { paddingValues ->
        if (viewModel.reports.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No reports found.\nReports will appear here in real time.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.reports) { report ->
                    ReportCard(report)
                }
            }
        }
    }
}

/* --------------------------------------------------------------------------
   🧱 UI Card for Each Report Entry
-------------------------------------------------------------------------- */
@Composable
fun ReportCard(report: FakeMedicineReport) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = report.medicineName,
                fontWeight = FontWeight.Bold,
                color = tripleSeven,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Distributor: ${report.distributorName}",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )

            Text(
                text = "Location: ${report.location}",
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )

            Text(
                text = "Batch: ${report.batchNumber.ifEmpty { "N/A" }}",
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Severity: ${report.severity}",
                color = when (report.severity) {
                    "High" -> Color(0xFFFF7043)
                    "Critical" -> Color(0xFFD32F2F)
                    "Moderate" -> Color(0xFF1976D2)
                    else -> Color(0xFF388E3C)
                },
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = report.description,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )

            Spacer(modifier = Modifier.height(6.dp))
            Divider(thickness = 0.5.dp, color = Color.LightGray)

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Reported on: ${
                    java.text.SimpleDateFormat("dd MMM yyyy, HH:mm").format(report.timestamp)
                }",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

/* --------------------------------------------------------------------------
   🧪 Preview
-------------------------------------------------------------------------- */
@Preview(showBackground = true)
@Composable
fun ViewReportPreview() {
    ViewReport(rememberNavController())
}
