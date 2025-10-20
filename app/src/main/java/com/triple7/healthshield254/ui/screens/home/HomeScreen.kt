package com.triple7.healthshield254.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.triple7.healthshield254.R
import com.triple7.healthshield254.navigation.*
import com.triple7.healthshield254.ui.theme.HealthShield254Theme
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlin.math.roundToInt


@Composable
fun HomeScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val userName = remember(user) {
        user?.displayName?.takeIf { it.isNotBlank() } ?: user?.email?.split('@')?.get(0)?.replaceFirstChar { it.uppercase() } ?: "User"
    }

    Scaffold(
        bottomBar = {
            HomeBottomNavigation(
                navController = navController,
                onProfileClick = { navController.navigate(ROUT_PROFILESETTINS) }
            )
        }
    ) { paddingValues ->

        val scrollState = rememberScrollState()
        val gradientBrush = Brush.verticalGradient(
            colors = listOf(tripleSeven.copy(alpha = 0.2f), Color.Transparent)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {

            // --- Top Row: Greeting + Notification Icon ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                    val greetingText = when (currentHour) {
                        in 0..11 -> "Good Morning"
                        in 12..16 -> "Good Afternoon"
                        else -> "Good Evening"
                    }

                    Text(
                        text = "Hello, $userName 👋",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = greetingText,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                IconButton(
                    onClick = { /* navController.navigate(ROUT_NOTIFICATIONS) */ }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.medicalinsurance),
                        contentDescription = "Notifications",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Scan Medicine Button ---
            GradientButton(
                text = "Scan Medicine",
                icon = Icons.Default.Info, // QrCodeScanner Icon
                gradient = Brush.horizontalGradient(
                    listOf(tripleSeven, tripleSeven.copy(alpha = 0.7f))
                )
            ) {
                navController.navigate(ROUT_SCANMEDICINE)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Your Profile Card ---
            Text("Your Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile), // Generic profile icon
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(userName, fontWeight = FontWeight.Bold)
                        user?.email?.let { Text(it, color = Color.Gray) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Dashboard Cards ---
            val impressiveColors = listOf(
                Color(0xFF4A90E2), // Bright Blue
                Color(0xFF50E3C2), // Aqua Green
                Color(0xFFB8E986), // Pastel Green
                Color(0xFFF5A623), // Orange
                Color(0xFFF8E71C), // Yellow
                Color(0xFF8B572A), // Brown
                Color(0xFF7ED321), // Lime Green
                Color(0xFF9013FE), // Purple
                Color(0xFFBD10E0), // Magenta
                Color(0xFF417505), // Dark Green
                Color(0xFFD0021B)  // Red
            )

            val dashboardItems = listOf(
                Triple("Hotspot Map", R.drawable.hotspotmap, impressiveColors[0]),
                Triple("ScanMedicine", R.drawable.scan, impressiveColors[1]),
                Triple("Report counterfeit", R.drawable.reportcounterfeit, impressiveColors[2]),
                Triple("Medicine", R.drawable.medicine, impressiveColors[3]),
                Triple("Profile & Settings", R.drawable.profile, impressiveColors[4]),
                Triple("Place Order", R.drawable.placeorder, impressiveColors[5]),
                Triple("Supplier Manufacturer", R.drawable.supplier, impressiveColors[6]),
                Triple("Analytics Screen", R.drawable.supplier, impressiveColors[7]),
                Triple("ChatBoard Screen", R.drawable.supplier, impressiveColors[8]),
                Triple("Admin Screen", R.drawable.supplier, impressiveColors[9]),
                Triple("Upload-Medicine Screen", R.drawable.supplier, impressiveColors[10])
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = dashboardItems.chunked(2)) { pair ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.width(280.dp)
                    ) {
                        pair.forEach { (title, icon, color) ->
                            DashboardCard(
                                title = title,
                                iconRes = icon,
                                color = color,
                                onClick = {
                                    when (title) {
                                        "Hotspot Map" -> navController.navigate(ROUT_HOTSPOTMAP)
                                        "ScanMedicine" -> navController.navigate(ROUT_SCANMEDICINE)
                                        "Report counterfeit" -> navController.navigate(ROUT_SENDREPORT)
                                        "Medicine" -> navController.navigate(ROUT_MEDICINE)
                                        "Place Order" -> navController.navigate(ROUT_PLACEORDER)
                                        "Supplier Manufacturer" -> navController.navigate(ROUT_SUPPLIERMANUFACTURER)
                                        "Analytics Screen" -> navController.navigate(ROUT_ANALYTICSSCREEN)
                                        "ChatBoard Screen" -> navController.navigate(ROUT_CHATBOARDSCREEN)
                                        "Admin Screen" -> navController.navigate(ROUT_ADMIN)
                                        "Upload-Medicine Screen" -> navController.navigate(ROUT_UPLOADMEDICINE)
                                        "Profile & Settings" -> navController.navigate(ROUT_PROFILESETTINS)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Notice Banner ---
            MarqueeText("⚠ Important notice: Always check medicine authenticity before purchase!")
        }
    }
}

@Composable
fun DashboardCard(title: String, iconRes: Int, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clickable { onClick() }
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(42.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun GradientButton(text: String, icon: ImageVector, gradient: Brush, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(gradient)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = text, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MarqueeText(text: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "marquee")
    val translateX by infiniteTransition.animateFloat(
        initialValue = 1000f,
        targetValue = -1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "translateX"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(Color(0xFFFFC107)),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            modifier = Modifier.offset { IntOffset(translateX.roundToInt(), 0) },
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun HomeBottomNavigation(
    navController: NavController,
    onProfileClick: () -> Unit
) {
    NavigationBar(containerColor = tripleSeven) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.verification),
                    contentDescription = "Verification",
                    modifier = Modifier.size(30.dp)
                )
            },
            selected = false,
            onClick = { navController.navigate(ROUT_VIEWREPORT) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.scan),
                    contentDescription = "Scan",
                    modifier = Modifier.size(30.dp)
                )
            },
            selected = false,
            onClick = { navController.navigate(ROUT_SCANMEDICINE) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(30.dp)
                )
            },
            selected = false,
            onClick = { onProfileClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HealthShield254Theme {
        HomeScreen(navController = rememberNavController())
    }
}
