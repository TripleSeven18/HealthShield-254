package com.triple7.healthshield254.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.navigation.ROUT_ADD_MEDICINE
import com.triple7.healthshield254.navigation.ROUT_VIEWREPORT
import com.triple7.healthshield254.ui.theme.tripleSeven

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    Scaffold(
        containerColor = Color.White // Set a solid background color
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Brush.verticalGradient(listOf(tripleSeven.copy(alpha = 0.9f), Color.White)))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "HealthShield Admin Dashboard",
                style = MaterialTheme.typography.headlineSmall,
                color = tripleSeven,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Manage and monitor the HealthShield system efficiently.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dashboard Cards
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(dashboardItems.size) { index ->
                    val item = dashboardItems[index]
                    DashboardCard(item = item) {
                        navController.navigate(item.route)
                    }
                }
            }
        }
    }
}

// Data class for dashboard cards
data class DashboardItem(
    val title: String,
    val description: String,
    val color: Color,
    val route: String
)

val dashboardItems = listOf(
    DashboardItem(
        "View Report",
        "View and manage reports.",
        Color(0xFF00BCD4),
        ROUT_VIEWREPORT
    ),
    DashboardItem(
        "Medicine Information",
        "Upload, edit, and manage details about medicines, dosages, and inventory.",
        Color(0xFF03A9F4),
        ROUT_ADD_MEDICINE
    ),
    DashboardItem(
        "Consultations",
        "Monitor doctor-patient sessions.",
        Color(0xFF00BCD4),
        "consultations"
    ),
    DashboardItem(
        "Analytics",
        "Track system analytics and usage.",
        Color(0xFF00BCD4),
        "analytics"
    ),
    DashboardItem(
        "Notifications",
        "Manage alerts and announcements.",
        Color(0xFF03A9F4),
        "notifications"
    ),
)

@Composable
fun DashboardCard(item: DashboardItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = item.color
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Open", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen(rememberNavController())
}
