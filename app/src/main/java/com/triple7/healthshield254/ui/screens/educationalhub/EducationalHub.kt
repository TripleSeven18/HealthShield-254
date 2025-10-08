package com.triple7.healthshield254.ui.screens.educationalhub

import androidx.compose.foundation.clickable
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

    val coroutineScope = rememberCoroutineScope()

    // Mutable state for tips, faqs, tutorials
    var tips by remember {
        mutableStateOf(
            listOf(
                "Always check medicine packaging for seals and correct spellings.",
                "Verify batch and expiry dates before taking any medication.",
                "Scan the barcode or QR code using the HealthShield app for authenticity.",
                "Buy medicines only from verified pharmacies or outlets.",
                "Report any suspicious or fake-looking medicines immediately."
            )
        )
    }

    var faqs by remember {
        mutableStateOf(
            listOf(
                FAQ(
                    "How do I scan a medicine?",
                    "Open the app, tap 'Scan & Verify', and point your camera at the barcode or QR code."
                ),
                FAQ(
                    "What happens when I report a medicine?",
                    "Your report helps others stay safe and supports regulators in tracking counterfeit hotspots."
                ),
                FAQ("Is my data safe?", "Yes. Your reports are anonymous and your location data is optional.")
            )
        )
    }

    var tutorials by remember {
        mutableStateOf(
            listOf(
                Tutorial("How to Scan & Verify", "Step-by-step video guide on using the AI-powered scanner."),
                Tutorial("How to Report Medicine", "Learn how to take photos, add notes, and send community reports."),
                Tutorial("Understanding Verification Results", "What 'Authentic', 'Suspected', and 'Counterfeit' mean.")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Educational Hub") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        }
    ) { paddingValues ->

        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {

            // Section 1: Medicine Safety Tips
            item {
                SectionTitle("Medicine Safety Tips")
                Spacer(Modifier.height(8.dp))
            }
            items(tips.indices.toList()) { index ->
                TipCard(tips[index]) {
                    coroutineScope.launch {
                        val updatedTip = fetchTipFromBackend(tips[index])
                        tips = tips.toMutableList().also { it[index] = updatedTip }
                    }
                }
            }

            // Section 2: Tutorials
            item {
                Spacer(Modifier.height(24.dp))
                SectionTitle("🎥 Tutorials")
                Spacer(Modifier.height(8.dp))
            }
            items(tutorials.indices.toList()) { index ->
                TutorialCard(tutorials[index]) {
                    coroutineScope.launch {
                        val updatedTutorial = fetchTutorialFromBackend(tutorials[index])
                        tutorials = tutorials.toMutableList().also { it[index] = updatedTutorial }
                    }
                }
            }

            // Section 3: FAQs
            item {
                Spacer(Modifier.height(24.dp))
                SectionTitle("Frequently Asked Questions")
                Spacer(Modifier.height(8.dp))
            }
            items(faqs.indices.toList()) { index ->
                FAQCard(faqs[index]) {
                    coroutineScope.launch {
                        val updatedFAQ = fetchFAQFromBackend(faqs[index])
                        faqs = faqs.toMutableList().also { it[index] = updatedFAQ }
                    }
                }
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

// Composables
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
        color = Color.Black,
    )
}

@Composable
fun TipCard(tip: String, onIconClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(tripleSeven),
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onIconClick() }
            )
            Spacer(Modifier.width(8.dp))
            Text(tip)
        }
    }
}

@Composable
fun TutorialCard(tutorial: Tutorial, onIconClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(tripleSeven),
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onIconClick() }
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(tutorial.title, fontWeight = FontWeight.Bold)
                Text(tutorial.description, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun FAQCard(faq: FAQ, onIconClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(tripleSeven))

    {
        Column(Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Call,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onIconClick() }
                    )
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

// Preview
@Preview(showBackground = true)
@Composable
fun PreviewEducationalHubScreen() {
    EducationalHubScreen(navController = null)
}
