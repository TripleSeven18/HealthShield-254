package com.triple7.healthshield254.ui.screens.educationalhub
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationalHubScreen(navController: NavController?) {
    val tips = remember {
        listOf(
            "Always check medicine packaging for seals and correct spellings.",
            "Verify batch and expiry dates before taking any medication.",
            "Scan the barcode or QR code using the HealthShield app for authenticity.",
            "Buy medicines only from verified pharmacies or outlets.",
            "Report any suspicious or fake-looking medicines immediately."
        )
    }

    val faqs = remember {
        listOf(
            FAQ("How do I scan a medicine?",
                "Open the app, tap 'Scan & Verify', and point your camera at the barcode or QR code."),
            FAQ("What happens when I report a medicine?",
                "Your report helps others stay safe and supports regulators in tracking counterfeit hotspots."),
            FAQ("Is my data safe?",
                "Yes. Your reports are anonymous and your location data is optional.")
        )
    }

    val tutorials = remember {
        listOf(
            Tutorial("How to Scan & Verify", "Step-by-step video guide on using the AI-powered scanner."),
            Tutorial("How to Report Medicine", "Learn how to take photos, add notes, and send community reports."),
            Tutorial("Understanding Verification Results", "What 'Authentic', 'Suspected', and 'Counterfeit' mean.")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Educational Hub") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Section 1: Medicine Safety Tips
            item {
                SectionTitle("Medicine Safety Tips")
                Spacer(Modifier.height(8.dp))
            }
            items(tips) { tip ->
                TipCard(tip)
            }

            // Section 2: Tutorials
            item {
                Spacer(Modifier.height(24.dp))
                SectionTitle("🎥 Tutorials")
                Spacer(Modifier.height(8.dp))
            }
            items(tutorials) { tutorial ->
                TutorialCard(tutorial)
            }

            // Section 3: FAQs
            item {
                Spacer(Modifier.height(24.dp))
                SectionTitle("Frequently Asked Questions")
                Spacer(Modifier.height(8.dp))
            }
            items(faqs) { faq ->
                FAQCard(faq)
            }

            // Section 4: Local Awareness Campaigns
            item {
                Spacer(Modifier.height(24.dp))
                SectionTitle("Local Awareness Campaigns")
                Spacer(Modifier.height(8.dp))
                Text(
                    "Stay tuned for upcoming HealthShield community campaigns in your area!",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Gray
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = "Tutorials",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start
    )
}

@Composable
fun TipCard(tip: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(tip)
        }
    }
}

@Composable
fun TutorialCard(tutorial: Tutorial) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(tutorial.title, fontWeight = FontWeight.Bold)
                Text(tutorial.description, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun FAQCard(faq: FAQ) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(faq.question, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.CheckCircle else Icons.Default.Call,
                        contentDescription = null
                    )
                }
            }
            if (expanded) {
                Spacer(Modifier.height(4.dp))
                Text(faq.answer, color = Color.DarkGray)
            }
        }
    }
}

// Data classes
data class FAQ(val question: String, val answer: String)
data class Tutorial(val title: String, val description: String)

@Preview(showBackground = true)
@Composable
fun PreviewEducationalHubScreen() {
    EducationalHubScreen(navController = null)
}
