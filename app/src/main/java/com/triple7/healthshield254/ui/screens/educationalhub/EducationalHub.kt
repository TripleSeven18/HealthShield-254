package com.triple7.healthshield254.ui.screens.educationalhub

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.triple7.healthshield254.R
import com.triple7.healthshield254.ui.theme.tripleS
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Data Models
data class FAQ(var question: String, var answer: String)
data class Tutorial(var title: String, var description: String)

//  Simulated Backend
suspend fun fetchTipFromBackend(tip: String): String {
    delay(1000) // simulate network delay
    return "$tip (Updated from backend)"
}

suspend fun fetchFAQFromBackend(faq: FAQ): FAQ {
    delay(1000) // simulate network delay
    return faq.copy(answer = faq.answer + " (Fetched from backend)")
}

suspend fun fetchTutorialFromBackend(tutorial: Tutorial): Tutorial {
    delay(1000) // simulate network delay
    return tutorial.copy(description = tutorial.description + " (Fetched from backend)")
}

// Main Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationalHubScreen(navController: NavController?) {
    val tips = listOf(
        "Check medicine packaging for seals.",
        "Verify batch and expiry dates.",
        "Scan QR codes for authenticity."
    )
    val faqs = listOf(
        FAQ("How to scan medicine?", "Open app -> Scan -> Verify"),
        FAQ("Is data safe?", "Yes, anonymous and secure.")
    )
    val tutorials = listOf(
        Tutorial("Scan & Verify", "Step-by-step guide."),
        Tutorial("Report Medicine", "Learn how to report.")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Info, null) })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Favorite, null) })
                Box(modifier = Modifier.offset(y = (-28).dp)) {
                    FloatingActionButton(onClick = {}, containerColor = Color(0xFF3366FF)) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Place, null) })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Person, null) })
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Top header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Educational HubDashboard", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Image(
                        painter = painterResource(id = R.drawable.medicalinsurance),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3366FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("January", color = Color.White)
                        Spacer(Modifier.height(8.dp))
                        Text("$500", color = Color.White, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(progress = 0.7f, color = Color.White, trackColor = Color.White.copy(alpha = 0.3f))
                        Spacer(Modifier.height(8.dp))
                        Text("Daily spend target: $16.67", color = Color.White)
                    }
                }
            }

            // Daily spends
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Spends", fontWeight = FontWeight.Bold)
                    Text("See All", color = Color(0xFF3366FF))
                }
            }

            items(tips) { tip ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF3366FF))
                        Spacer(Modifier.width(12.dp))
                        Text(tip)
                    }
                }
            }

            // Wishlist horizontal scroll
            item {
                Text("Wishlist", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("Tee", "Gym", "Bike", "Saving").forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp), // optional: adjust as needed
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = tripleS)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(item, color = Color.White, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }

        }
    }
}


// Preview
@Preview(showBackground = true)
@Composable
fun PreviewEducationalHubScreen() {
    EducationalHubScreen(navController = null)
}
