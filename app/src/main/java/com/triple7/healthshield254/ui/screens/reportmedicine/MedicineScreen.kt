package com.triple7.healthshield254.ui.screens.reportmedicine

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.*

data class MedicineUpload(
    val name: String = "",
    val dosage: String = "",
    val instructions: String = "",
    val sideEffects: String = "",
    val warnings: String = "",
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScreen(navController: NavController) {
    val inPreview = LocalInspectionMode.current
    val databaseRef = if (!inPreview) FirebaseDatabase.getInstance().getReference("medicines") else null

    var medicineList by remember { mutableStateOf<List<MedicineUpload>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch data from Firebase
    LaunchedEffect(Unit) {
        if (!inPreview) {
            databaseRef?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val medicines = snapshot.children.mapNotNull { it.getValue(MedicineUpload::class.java) }
                    medicineList = medicines
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading = false
                }
            })
        } else {
            // Preview dummy data
            medicineList = listOf(
                MedicineUpload(
                    name = "Paracetamol",
                    dosage = "500mg",
                    instructions = "Take after meals",
                    sideEffects = "Drowsiness",
                    warnings = "Avoid alcohol",
                    imageUrl = ""
                ),
                MedicineUpload(
                    name = "Amoxicillin",
                    dosage = "250mg",
                    instructions = "Take twice a day",
                    sideEffects = "Nausea",
                    warnings = "Consult doctor if allergic",
                    imageUrl = ""
                )
            )
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Medicine Report",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF004D40)
                ),
                actions = {
                    IconButton(onClick = { /* Placeholder for info or filter */ }) {
                        Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF004D40)) {
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.popBackStack() },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White) },
                    label = { Text("Home", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Add future actions like filter or refresh */ },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Medicines", tint = Color.White) },
                    label = { Text("Medicines", color = Color.White) }
                )
            }
        }
    ) { paddingValues ->

        val backgroundBrush = Brush.verticalGradient(
            listOf(Color(0xFF0D1B1E), Color(0xFF004D40))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundBrush)
                .padding(paddingValues)
                .padding(12.dp)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF26A69A))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Loading medicines...", color = Color.White)
                    }
                }

                medicineList.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "No Data",
                            tint = Color(0xFF26A69A),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No medicines uploaded yet.", color = Color.White)
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(medicineList) { medicine ->
                            MedicineCard(medicine = medicine)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineCard(medicine: MedicineUpload) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2F2B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (medicine.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(medicine.imageUrl),
                    contentDescription = "Medicine Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF263A34)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Placeholder",
                        tint = Color(0xFF26A69A),
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                medicine.name,
                color = Color(0xFFB2FFCB),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Text(
                "Dosage: ${medicine.dosage}",
                color = Color.White,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Text(
                "Instructions: ${medicine.instructions}",
                color = Color(0xFFD1EED6),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Text(
                "Side Effects: ${medicine.sideEffects}",
                color = Color(0xFFE0B2B2),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Text(
                "Warnings: ${medicine.warnings}",
                color = Color(0xFFFFCC80),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicineScreenPreview() {
    MedicineScreen(navController = rememberNavController())
}
