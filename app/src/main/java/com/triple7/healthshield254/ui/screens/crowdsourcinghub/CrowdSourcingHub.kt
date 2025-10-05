package com.triple7.healthshield254.ui.screens.crowdsourcinghub
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.triple7.digconnectke254.ui.screens.home.HomeScreen
import com.triple7.healthshield254.ui.theme.triple777
import com.triple7.healthshield254.ui.theme.tripleSeven

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrowdsourcingHubScreen(navController: NavController) {
    val reports = remember {
        listOf(
            CommunityReport("Paracetamol", "Suspected counterfeit", "Nairobi", 23),
            CommunityReport("Amoxicillin", "Expired batch", "Kisumu", 15),
            CommunityReport("Ibuprofen", "Unlabeled packaging", "Mombasa", 12)
        )
    }

    val contributors = remember {
        listOf(
            Contributor("Alice", 42),
            Contributor("Brian", 33),
            Contributor("Cynthia", 28)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text(
                    text = "Crowdsourcing Hub",
                    fontWeight = FontWeight.ExtraBold,
                ) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            item {
                Text(
                    "Community Reports",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(reports) { report ->
                ReportCard(report)
            }

            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Top Contributors",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(contributors) { contributor ->
                ContributorCard(contributor)
            }

            item {
                Spacer(Modifier.height(24.dp))
                LocalStatsSection()
            }
        }
    }
}

@Composable
fun ReportCard(report: CommunityReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = tripleSeven,       // main background color
            contentColor = Color.White
        )
        ) {
        Column(Modifier.padding(16.dp)) {
            Text(report.medicine, fontWeight = FontWeight.Bold)
            Text(report.issue, color = Color.White)
            Text("Location: ${report.location}")
            Text("Reports: ${report.count}")
        }
    }
}

@Composable
fun ContributorCard(contributor: Contributor) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = tripleSeven,
            contentColor = Color.White
        )
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(contributor.name, fontWeight = FontWeight.Medium)
            Text("Reports: ${contributor.points}", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LocalStatsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Local Stats",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(8.dp))
        Text("Verified Reports: 120+")
        Text("Active Alerts: 5")
        Text("Regions Covered: 15")
        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = 0.8f,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )
        Text("Community Engagement: 80%", fontWeight = FontWeight.Medium)
    }
}

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

@Composable
@Preview(showBackground = true)

fun CrowdsourcingHubScreenPreview(){

    CrowdsourcingHubScreen(rememberNavController())

}
