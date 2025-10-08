package com.triple7.healthshield254.ui.screens.crowdsourcinghub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Data Models
data class CommunityReport(
    val medicine: String,
    val issue: String,
    val location: String,
    val count: Int
)

data class Contributor(
    val name: String,
    val points: Int
)

// Simulated Backend
suspend fun fetchReportFromBackend(report: CommunityReport): CommunityReport {
    delay(1000) // Simulate network call
    return report.copy(
        issue = report.issue + " (Updated from backend)",
        count = report.count + 20
    )
}

// ---------------------- Main Screen ----------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrowdsourcingHubScreen(navController: NavController) {

    // Mutable state for reports

    var reports by remember {
        mutableStateOf(
            listOf(
                CommunityReport("Paracetamol", "Suspected counterfeit", "Nairobi", 23),
                CommunityReport("Amoxicillin", "Expired batch", "Kisumu", 15),
                CommunityReport("Ibuprofen", "Unlabeled packaging", "Mombasa", 12)
            )
        )
    }

    val contributors = remember {
        listOf(
            Contributor("Vincent Malului", 42),
            Contributor("Brian Kimanthi", 33),
            Contributor("Cynthia Were", 28)
        )
    }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text(text = "Crowdsourcing Hub", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        }
    ) { paddingValues ->

        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Community Reports Title

            item {
                TextButton(onClick = {}) {
                    Text(
                        text = "Community Reports",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Report Cards

            items(reports.indices.toList()) { index ->
                val report = reports[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = tripleSeven, contentColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Report Icon",
                            tint = Color.Yellow,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {

                                    // Launch coroutine to fetch updated report

                                    coroutineScope.launch {
                                        val updatedReport = fetchReportFromBackend(report)
                                        reports = reports.toMutableList().also { it[index] = updatedReport }
                                    }
                                }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(report.medicine, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(report.issue)
                            Text("Location: ${report.location}")
                            Text("Reports: ${report.count}")
                        }
                    }
                }
            }

            // Top Contributors Card

            item { TopContributorsCard(contributors) }

            // Local Stats

            item { LocalStatsCard() }
        }
    }
}

// Top Contributors Card

@Composable
fun TopContributorsCard(contributors: List<Contributor>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = tripleSeven, contentColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Top Contributors", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            contributors.forEach { contributor ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { println("Clicked on ${contributor.name}") },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(contributor.name, fontWeight = FontWeight.Medium)
                    Text("Reports: ${contributor.points}", fontWeight = FontWeight.Bold)
                }
                Divider(color = Color.Gray.copy(alpha = 0.3f))
            }
        }
    }
}

// Local Stats

@Composable
fun LocalStatsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = tripleSeven, contentColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text("Local Stats", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(progress = 0.8f, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Community Engagement: 80%", fontWeight = FontWeight.Medium)
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun CrowdsourcingHubScreenPreview() {
    CrowdsourcingHubScreen(rememberNavController())
}
