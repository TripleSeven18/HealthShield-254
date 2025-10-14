package com.triple7.healthshield254.ui.screens.reportmedicine

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.R
import com.triple7.healthshield254.ui.theme.tripleS
import com.triple7.healthshield254.ui.theme.tripleSeven

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
            "Paracetamol",
            "500mg Tablet",
            R.drawable.img_17,
            "Take one tablet every 6 hours as needed. Max 4 tablets in 24 hours.",
            "Nausea, headache, dizziness. Consult a doctor if symptoms persist.",
            "Do not use if you have liver disease. Keep out of reach of children.",
            tripleSeven
        ),
        Medicine(
            "Amoxicillin",
            "250mg Capsule",
            R.drawable.img_18,
            "Take one capsule every 8 hours for 7 days. Complete the course.",
            "Diarrhea, stomach upset, rash. Consult a doctor if severe.",
            "Do not use if allergic to penicillin. Avoid alcohol.",
            Color(0xFF81D4FA)
        ),
        Medicine(
            "Ibuprofen",
            "200mg Tablet",
            R.drawable.medicalinsurance,
            "Take one tablet every 6-8 hours with food. Max 6 tablets/day.",
            "Upset stomach, dizziness, mild headache.",
            "Avoid if you have stomach ulcers or kidney problems.",
            Color(0xFFFFCC80)
        ),
        Medicine(
            "Cetirizine",
            "10mg Tablet",
            R.drawable.medicalinsurance,
            "Take one tablet daily with or without food. Do not exceed 10mg in 24 hours.",
            "Drowsiness, dry mouth, mild headache. Consult a doctor if severe.",
            "Avoid alcohol while taking this medicine. Use caution if you have kidney or liver disease.",
            Color(0xFFA5D6A7)
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
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(Icons.Default.Info, contentDescription = "Report") },
                    label = { Text("Report") }
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
                    Image(
                        painter = painterResource(id = medicine.imageRes),
                        contentDescription = "${medicine.name} Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(180.dp)
                            .fillMaxWidth(0.9f)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Medicine Title Card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = medicine.cardColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = medicine.name,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = medicine.dosage,
                                fontSize = 15.sp,
                                color = Color.DarkGray
                            )
                        }
                    }

                    MedicineCard("Dosage Instructions", medicine.instructions)
                    MedicineCard("Side Effects", medicine.sideEffects)
                    MedicineCard("Warnings", medicine.warnings)
                }
            }
        }
    }
}

@Composable
fun MedicineCard(title: String, text: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(90.dp)
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            MarqueeText(text = text)
        }
    }
}
@Composable
fun MarqueeText(text: String) {
    var textWidth by remember { mutableStateOf(0f) }
    var containerWidth by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(tripleS)
            .onGloballyPositioned { containerWidth = it.size.width.toFloat() },
        contentAlignment = Alignment.CenterStart
    ) {
        if (textWidth > containerWidth) {
            // Only scroll if text is wider than container
            Row(
                modifier = Modifier.offset {
                    val offset = (-textWidth * animatedOffset).toInt()
                    androidx.compose.ui.unit.IntOffset(offset, 0)
                }
            ) {
                Text(
                    text = text,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.onGloballyPositioned {
                        textWidth = it.size.width.toFloat()
                    }
                )
                Spacer(modifier = Modifier.width(50.dp))
                Text(text = text, fontSize = 14.sp, color = Color.White)
            }
        } else {
            // If text fits, just show normally
            Text(text = text, fontSize = 14.sp, color = Color.White)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MedicineScreenPreview() {
    MedicineScreen(navController = rememberNavController())
}