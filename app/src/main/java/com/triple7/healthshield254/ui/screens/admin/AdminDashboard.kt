package com.triple7.healthshield254.ui.screens.admin

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.navigation.ROUT_ADD_MEDICINE
import com.triple7.healthshield254.navigation.ROUT_ANALYTICSSCREEN
import com.triple7.healthshield254.navigation.ROUT_CHATBOARDSCREEN
import com.triple7.healthshield254.navigation.ROUT_VIEWORDERS
import com.triple7.healthshield254.navigation.ROUT_VIEWREPORT
import com.triple7.healthshield254.ui.theme.tripleSeven
import com.triple7.healthshield254.ui.theme.tripleseven

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(tripleSeven.copy(alpha = 0.9f), Color.White)
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "HealthShield Admin Dashboard",
                style = MaterialTheme.typography.headlineSmall,
                color = tripleseven,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Manage and monitor the HealthShield system efficiently.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
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
        "Upload Medicine",
        "Upload, edit, and manage details about medicines, dosages, and inventory.",
        Color(0xFF03A9F4),
        ROUT_ADD_MEDICINE
    ),
    DashboardItem(
        "Consultations",
        "Monitor doctor-patient sessions.",
        Color(0xFF00BCD4),
        ROUT_CHATBOARDSCREEN
    ),
    DashboardItem(
        "Analytics",
        "Track system analytics and usage.",
        Color(0xFF00BCD4),
        ROUT_ANALYTICSSCREEN
    ),
    DashboardItem(
        "View Orders",
        "View and manage all orders",
        Color(0xFF03A9F4),
        ROUT_VIEWORDERS
    ),
)

@Composable
fun DashboardCard(item: DashboardItem, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }

    // Smooth pop animation
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .clickable(
                onClick = {
                    pressed = true
                    onClick()
                    // reset animation after click
                    pressed = false
                }
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(50.dp), // 👈 bubble-like shape
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(item.color.copy(alpha = 0.15f), Color.White)
                    )
                )
                .padding(20.dp)
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

            // Rounded button fits the bubble aesthetic
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = item.color),
                shape = CircleShape,
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