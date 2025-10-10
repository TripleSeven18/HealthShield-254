package com.triple7.healthshield254.ui.screens.reportmedicine

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.R
import com.triple7.healthshield254.ui.theme.tripleSeven
import androidx.compose.ui.graphics.Color

// Data class to hold medicine info
data class Medicine(
    val name: String,
    val dosage: String,
    val imageRes: Int,
    val instructions: String,
    val sideEffects: String,
    val warnings: String,
    val cardColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScreen(navController: NavController) {

    val medicines = listOf(
        Medicine(
            name = "Paracetamol",
            dosage = "500mg Tablet",
            imageRes = R.drawable.img_17,
            instructions = "Take one tablet every 6 hours as needed. Max 4 tablets in 24 hours.",
            sideEffects = "Nausea, headache, dizziness. Consult a doctor if symptoms persist.",
            warnings = "Do not use if you have liver disease. Keep out of reach of children.",
            cardColor = tripleSeven
        ),
        Medicine(
            name = "Amoxicillin",
            dosage = "250mg Capsule",
            imageRes = R.drawable.img_18,
            instructions = "Take one capsule every 8 hours for 7 days. Complete the course.",
            sideEffects = "Diarrhea, stomach upset, rash. Consult a doctor if severe.",
            warnings = "Do not use if allergic to penicillin. Avoid alcohol.",
            cardColor = Color(0xFF81D4FA) // Light blue
        ),
        Medicine(
            name = "Ibuprofen",
            dosage = "200mg Tablet",
            imageRes = R.drawable.medicalinsurance,
            instructions = "Take one tablet every 6-8 hours with food. Max 6 tablets/day.",
            sideEffects = "Upset stomach, dizziness, mild headache.",
            warnings = "Avoid if you have stomach ulcers or kidney problems.",
            cardColor = Color(0xFFFFCC80) // Light orange
        ),

                Medicine(
                name = "Cetirizine",
        dosage = "10mg Tablet",
        imageRes = R.drawable.medicalinsurance, // Replace with a relevant image if available
        instructions = "Take one tablet daily with or without food. Do not exceed 10mg in 24 hours.",
        sideEffects = "Drowsiness, dry mouth, mild headache. Consult a doctor if severe.",
        warnings = "Avoid alcohol while taking this medicine. Use caution if you have kidney or liver disease.",
        cardColor = Color(0xFFA5D6A7) // Light green
    )

    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Info", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = tripleSeven,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = tripleSeven) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Navigate to Home */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Navigate to Report */ },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Report") },
                    label = { Text("Report") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Navigate to Info */ },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Info") },
                    label = { Text("Info") }
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            medicines.forEach { medicine ->
                item {
                    // Medicine Image
                    Image(
                        painter = painterResource(id = medicine.imageRes),
                        contentDescription = "${medicine.name} Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(0.9f)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Medicine Name & Dosage
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = medicine.cardColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = medicine.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = medicine.dosage,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Dosage Instructions
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Dosage Instructions",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = medicine.instructions,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Side Effects
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Side Effects",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = medicine.sideEffects,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Warnings
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Warnings",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = medicine.warnings,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicineScreenPreview() {
    MedicineScreen(navController = rememberNavController())
}
