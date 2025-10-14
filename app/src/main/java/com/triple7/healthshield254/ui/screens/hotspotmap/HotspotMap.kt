package com.triple7.healthshield254.ui.screens.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotspotMapScreen(navController: NavController) {
    val activities = listOf(
        ActivityItem("Walking", Icons.Filled.Info, Color(0xFF6DD5FA)), //DirectionsWalk icon
        ActivityItem("Cycling", Icons.Filled.Info, Color(0xFFFFC371)), //DirectionsBike icon
        ActivityItem("Driving", Icons.Filled.Info, Color(0xFFFF6B6B)), //DirectionsCar icon
        ActivityItem("Train", Icons.Filled.Info, Color(0xFF42A5F5)), //Train icon
        ActivityItem("Hiking", Icons.Filled.Info, Color(0xFF81C784)), //Hiking icon
        ActivityItem("Flight", Icons.Filled.Info, Color(0xFFBA68C8)) //Flight icon
    )

    Scaffold(
        containerColor = Color(0xFFF8F8F8),
        bottomBar = { ModernBottomNav() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFF5F6D), Color(0xFFFFC371))
                        )
                    )
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Good Morning,",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Satwik Pachino ☀️",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("21°C • Cloudy", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Start a new journey",
                modifier = Modifier.padding(horizontal = 24.dp),
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(activities) { item ->
                    JourneyCard(item)
                }
            }
        }
    }
}

@Composable
fun JourneyCard(item: ActivityItem) {
    var isPressed by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = if (isPressed) item.color.copy(alpha = 0.9f) else Color.White,
        label = "cardColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clickable {
                isPressed = !isPressed
            },
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = if (isPressed) Color.White else item.color,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = item.title,
                color = if (isPressed) Color.White else Color.Black,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
fun ModernBottomNav() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf("Home", "Friends", "Stats", "Profile")
        items.forEachIndexed { index, label ->
            NavigationBarItem(
                selected = index == 0,
                onClick = {},
                icon = {
                    Icon(
                        imageVector = when (label) {
                            "Friends" -> Icons.Default.Info//Hiking icon
                            "Stats" -> Icons.Default.Info//Train Icon
                            "Profile" -> Icons.Default.Info//DirectionsBike icon
                            else -> Icons.Default.Info//DirectionsWalk
                        },
                        contentDescription = label
                    )
                },
                label = { Text(label) },
                alwaysShowLabel = true
            )
        }
    }
}

data class ActivityItem(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

@Preview(showBackground = true)
@Composable
fun PreviewHotspotMapScreen() {
    HotspotMapScreen(rememberNavController())
}