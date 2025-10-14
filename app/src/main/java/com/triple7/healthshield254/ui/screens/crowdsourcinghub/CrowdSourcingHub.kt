package com.triple7.healthshield254.ui.screens.crowdsourcinghub

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.*
import com.google.firebase.database.*
import com.triple7.healthshield254.R
import com.triple7.healthshield254.ui.theme.tripleSeven

// ---------------------- Data Models ----------------------
data class CommunityReport(
    val medicine: String = "",
    val issue: String = "",
    val location: String = "",
    val count: Int = 0
)

data class Contributor(
    val name: String = "",
    val points: Int = 0
)

data class LocalStats(
    val communityEngagement: Float = 0.0f
)

// ---------------------- Main Screen ----------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrowdsourcingHubScreen(navController: NavController) {

    var reports by remember { mutableStateOf(listOf<CommunityReport>()) }
    var contributors by remember { mutableStateOf(listOf<Contributor>()) }
    var localStats by remember { mutableStateOf(LocalStats()) }

    val isInPreview = LocalInspectionMode.current

    // Firebase logic only runs on device, not in preview
    if (!isInPreview) {
        val dbRef = FirebaseDatabase.getInstance().reference

        LaunchedEffect(Unit) {
            // Fetch Community Reports
            dbRef.child("communityReports").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<CommunityReport>()
                    snapshot.children.forEach { child ->
                        val report = child.getValue(CommunityReport::class.java)
                        report?.let { list.add(it) }
                    }

                    // ✅ Fallback data if none found
                    reports = if (list.isEmpty()) {
                        listOf(
                            CommunityReport("Paracetamol", "Expired Batch", "Nairobi"),
                            CommunityReport("Amoxicillin", "Counterfeit", "Mombasa"),
                            CommunityReport("Ibuprofen", "Fake Label", "Kisumu")
                        )
                    } else list
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            // Fetch Top Contributors
            dbRef.child("topContributors").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Contributor>()
                    snapshot.children.forEach { child ->
                        val username = child.child("username").getValue(String::class.java) ?: ""
                        val points = child.child("reportsSubmitted").getValue(Int::class.java) ?: 0
                        list.add(Contributor(name = username, points = points))
                    }

                    // ✅ Fallback contributors if none
                    contributors = if (list.isEmpty()) {
                        listOf(
                            Contributor("UserAlpha", 25),
                            Contributor("HealthHero", 19)
                        )
                    } else list
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            // Fetch Local Stats
            dbRef.child("localStats").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val totalReports =
                        snapshot.children.sumOf { it.child("activeReports").getValue(Int::class.java) ?: 0 }
                    val verifiedReports =
                        snapshot.children.sumOf { it.child("verifiedReports").getValue(Int::class.java) ?: 0 }
                    val engagement = if (totalReports + verifiedReports > 0)
                        verifiedReports.toFloat() / (totalReports + verifiedReports) else 0f
                    localStats = LocalStats(communityEngagement = engagement)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    } else {
        // ✅ Fallback preview data for Compose preview
        reports = listOf(
            CommunityReport("Paracetamol", "Expired Batch", "Nairobi", 12),
            CommunityReport("Amoxicillin", "Counterfeit", "Mombasa", 7),
            CommunityReport("Ibuprofen", "Fake Label", "Kisumu", 5)
        )
        contributors = listOf(
            Contributor("UserAlpha", 25),
            Contributor("HealthHero", 19)
        )
        localStats = LocalStats(communityEngagement = 0.75f)
    }

    // ---------------------- UI ----------------------
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = "Crowdsourcing Hub",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven),
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.medicalinsurance),
                        contentDescription = "Top Right Icon",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = 12.dp)
                            .clickable { println("Top-right icon clicked!") }
                    )
                }
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

            // ✅ Lottie Animation
            item {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.medicarepositivity)
                )
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = progress,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }

            // Community Reports title
            item {
                TextButton(onClick = {}) {
                    Text(
                        text = "Community Reports",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Reports list
            items(reports) { report ->
                ReportCard(report)
            }

            // Top Contributors
            item { TopContributorsCard(contributors) }

            // Local Stats
            item { LocalStatsCard(localStats) }
        }
    }
}

// ---------------------- Report Card ----------------------
@Composable
fun ReportCard(report: CommunityReport) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6A00),
            contentColor = Color.White
        ),
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
                modifier = Modifier.size(40.dp)
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

// ---------------------- Top Contributors Card ----------------------
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

// ---------------------- Local Stats Card ----------------------
@Composable
fun LocalStatsCard(stats: LocalStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6A00), contentColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text("Local Stats", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = stats.communityEngagement,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Community Engagement: ${(stats.communityEngagement * 100).toInt()}%",
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ---------------------- Preview ----------------------
@Preview(showBackground = true)
@Composable
fun CrowdsourcingHubScreenPreview() {
    CrowdsourcingHubScreen(rememberNavController())
}