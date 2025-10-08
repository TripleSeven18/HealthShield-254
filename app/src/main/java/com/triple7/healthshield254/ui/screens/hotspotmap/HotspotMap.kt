package com.triple7.healthshield254.ui.screens.hotspotmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.triple7.digconnectke254.ui.screens.home.HomeScreen
import com.triple7.healthshield254.ui.theme.triple777
import com.triple7.healthshield254.ui.theme.tripleSeven

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotspotMapScreen(navController: NavController) {

    // Mocked data for now — replace with live data later
    val alerts = remember {
        listOf(
            Alert("Nairobi", "Counterfeit Paracetamol detected", "Paracetamol", "High"),
            Alert("Mombasa", "Fake Amoxicillin batch reported", "Amoxicillin", "Moderate"),
            Alert("Kisumu", "Expired Ibuprofen stock alert", "Ibuprofen", "Low")
        )
    }

    var selectedFilter by remember { mutableStateOf("All") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hotspot Map & Alerts") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = triple777
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Filter controls
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filter:", fontWeight = FontWeight.Bold)
                FilterDropdown(selectedFilter) { selectedFilter = it }
            }

            Spacer(Modifier.height(12.dp))

            // Simulated interactive map area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(color = tripleSeven),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Interactive Map Placeholder\n(Geotagged reports)",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color.Black
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Recent Alerts",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(alerts.filter {
                    selectedFilter == "All" || it.medicine == selectedFilter
                }) { alert ->
                    AlertCard(alert)
                }
            }
        }
    }
}

@Composable
fun AlertCard(alert: Alert) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = tripleSeven),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(alert.location, fontWeight = FontWeight.Bold)
            Text(alert.description)
            Text("Medicine: ${alert.medicine}")
            Text(
                "Risk Level: ${alert.severity}",
                color = when (alert.severity) {
                    "High" -> Color.Black
                    "Moderate" -> Color.Black
                    else -> Color.Black
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FilterDropdown(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("All", "Paracetamol", "Amoxicillin", "Ibuprofen")

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

data class Alert(
    val location: String,
    val description: String,
    val medicine: String,
    val severity: String
)
@Composable
@Preview(showBackground = true)

fun HotspotMapScreenPreview(){

    HotspotMapScreen(rememberNavController())

}
