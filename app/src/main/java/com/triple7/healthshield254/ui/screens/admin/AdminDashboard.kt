package com.triple7.healthshield254.ui.screens.admin

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.navigation.ROUT_ADD_MEDICINE
import com.triple7.healthshield254.navigation.ROUT_VIEWREPORT
import com.triple7.healthshield254.ui.theme.tripleSeven

@Composable
fun AdminScreen(navController: NavController) {

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D1B1E), Color(0xFF102C2E))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header
            Text(
                text = "HealthShield Admin Dashboard",
                fontSize = 22.sp,
                color = tripleSeven,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Manage and monitor the HealthShield system efficiently.",
                color = Color(0xFF00BCD4),
                fontSize = 14.sp,
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
                        // ✅ Navigate to appropriate destination
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

// ✅ Add your actual navigation routes
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
    var isPressed by remember { mutableStateOf(false) }
    val animatedColor by animateColorAsState(
        targetValue = if (isPressed) item.color.copy(alpha = 0.7f) else item.color
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isPressed = !isPressed
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2F2B)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(animatedColor, Color(0xFF1C2F2B))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Color(0xFF90DAE5)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onClick, // ✅ Navigates to item.route
                    colors = ButtonDefaults.buttonColors(
                        containerColor = item.color
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Open", color = Color.Black)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen(rememberNavController())
}
