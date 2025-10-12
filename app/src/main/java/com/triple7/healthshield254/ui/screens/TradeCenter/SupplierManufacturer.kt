@file:OptIn(ExperimentalMaterial3Api::class)

package com.triple7.healthshield254.ui.screens.TradeCenter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.firestore.FirebaseFirestore
import com.triple7.healthshield254.R
import com.triple7.healthshield254.ui.theme.triple777
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// -----------------------------------------------------
// DATA MODEL
// -----------------------------------------------------
data class Partner(
    val id: String = "",
    val name: String = "",
    val company: String = "",
    val certification: String = "",
    val contact: String = "",
    val type: String = "",          // "Manufacturer" or "Supplier"
    val verified: Boolean = false,
    val blacklisted: Boolean = false
)

// -----------------------------------------------------
// MAIN SCREEN (NO NAVIGATION)
// -----------------------------------------------------
@Composable
fun SupplierManufacturerDashboard(navController: NavController) {
    var partners by remember { mutableStateOf<List<Partner>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // --- Function to fetch data from Firebase ---
    suspend fun fetchPartners(category: String) {
        loading = true
        error = null
        try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("partners").get().await()
            val allPartners = snapshot.toObjects(Partner::class.java)

            partners = when (category) {
                "Verified Manufacturers" -> allPartners.filter { it.verified && it.type == "Manufacturer" }
                "Verified Suppliers" -> allPartners.filter { it.verified && it.type == "Supplier" }
                "All Verified Partners" -> allPartners.filter { it.verified }
                "Blacklisted Suppliers" -> allPartners.filter { it.blacklisted }
                else -> emptyList()
            }

            selectedCategory = category
        } catch (e: Exception) {
            error = e.localizedMessage
        } finally {
            loading = false
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FB),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* future add feature */ },
                containerColor = Color(0xFF2979FF),
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // --- Header ---
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trade With Us",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Green
                )
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.Gray,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- 📷 Image at top ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()        // Fill the horizontal space
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center // Centers the image horizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.medicalinsurance),
                    contentDescription = "Trade Center Illustration",
                    modifier = Modifier
                        .size(width = 180.dp, height = 150.dp) // You can adjust width/height as needed
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }


            Spacer(Modifier.height(16.dp))

            // --- Blue Summary Card ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2979FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Verified Database", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Trusted Partners Overview", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                    LinearProgressIndicator(
                        progress = 0.7f,
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                    )
                    Text("70% verified partners", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Partner Categories",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(12.dp))

// --- Category Cards ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight() // ⬅️ Fill available screen height
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(
                    "Verified Manufacturers",
                    Icons.Default.Info,
                    bgColor = Color(0xFFBBDEFB),
                    iconColor = Color(0xFF1976D2)
                ) {
                    coroutineScope.launch { fetchPartners("Verified Manufacturers") }
                }

                DashboardCard(
                    "Verified Suppliers",
                    Icons.Default.Info,
                    bgColor = Color(0xFFC8E6C9),
                    iconColor = Color(0xFF388E3C)
                ) {
                    coroutineScope.launch { fetchPartners("Verified Suppliers") }
                }

                DashboardCard(
                    "All Verified Partners",
                    Icons.Default.Info,
                    bgColor = Color(0xFFE1BEE7),
                    iconColor = Color(0xFF7B1FA2)
                ) {
                    coroutineScope.launch { fetchPartners("All Verified Partners") }
                }

                DashboardCard(
                    "Blacklisted Suppliers",
                    Icons.Default.Info,
                    bgColor = Color(0xFFFFCDD2),
                    iconColor = Color(0xFFD32F2F)
                ) {
                    coroutineScope.launch { fetchPartners("Blacklisted Suppliers") }
                }
            }


            Spacer(Modifier.height(24.dp))

            // --- List Display ---
            when {
                loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                }
                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                partners.isNotEmpty() -> {
                    Text(
                        text = "$selectedCategory (${partners.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(partners) { partner -> PartnerCard(partner) }
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

// -----------------------------------------------------
// DASHBOARD CARD COMPONENT
// -----------------------------------------------------
@Composable
fun DashboardCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = tripleSeven),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .background(bgColor, shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconColor)
            }
            Spacer(Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Medium, fontSize = 15.sp)
        }
    }
}

// -----------------------------------------------------
// PARTNER CARD
// -----------------------------------------------------
@Composable
fun PartnerCard(partner: Partner) {
    val bgColor = when {
        partner.blacklisted -> Color(0xFFFFEBEE)
        partner.verified -> Color(0xFFE8F5E9)
        else -> Color.White
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(partner.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(partner.company, color = Color.Gray, fontSize = 13.sp)
            Text(partner.certification, color = Color.Gray, fontSize = 13.sp)
            Text("📞 ${partner.contact}", fontSize = 13.sp)

            val statusColor = when {
                partner.blacklisted -> Color(0xFFD32F2F)
                partner.verified -> Color(0xFF388E3C)
                else -> Color.Gray
            }

            Text(
                when {
                    partner.blacklisted -> "⚠️ Blacklisted"
                    partner.verified -> "✅ Verified ${partner.type}"
                    else -> "Unverified"
                },
                color = statusColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// -----------------------------------------------------
// PREVIEW
// -----------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewSupplierManufacturer() {
    SupplierManufacturerDashboard(rememberNavController())
}
