package com.triple7.healthshield254.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
        user?.displayName?.takeIf { it.isNotBlank() }
            ?: user?.email?.split('@')?.get(0)
                ?.replaceFirstChar { it.uppercase() }
            ?: "User"
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

            // --- Greeting Row ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val currentHour =
                        java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
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

                IconButton(onClick = { /* navController.navigate(ROUT_NOTIFICATIONS) */ }) {
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
                icon = Icons.Default.Info,
                gradient = Brush.horizontalGradient(
                    listOf(tripleSeven, tripleSeven.copy(alpha = 0.7f))
                )
            ) {
                navController.navigate(ROUT_SCANMEDICINE)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Profile Section ---
            Text(
                "Your Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
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

            // --- Dashboard Cards in Two Scrollable Containers ---
            val impressiveColors = listOf(
                Color(0xFF4A90E2),
                Color(0xFF50E3C2),
                Color(0xFFB8E986),
                Color(0xFFF5A623),
                Color(0xFFF8E71C),
                Color(0xFF8B572A),
                Color(0xFF7ED321),
                Color(0xFF9013FE)
            )

            val dashboardItems = listOf(
                Triple("HotspotMapScreen", R.drawable.hotspotmap, impressiveColors[0]),
                Triple("Report counterfeit", R.drawable.reportcounterfeit, impressiveColors[1]),
                Triple("Medicine", R.drawable.medicine, impressiveColors[2]),
                Triple("Profile & Settings", R.drawable.profile, impressiveColors[3]),
                Triple("Place Order", R.drawable.placeorder, impressiveColors[4]),
                Triple("Supplier Manufacturer", R.drawable.supplier, impressiveColors[5]),
                Triple("Analytics Screen", R.drawable.supplier, impressiveColors[6]),
                Triple("ChatScreen", R.drawable.supplier, impressiveColors[7])
            )

            val cardGroups = dashboardItems.chunked(4) // Two groups of 4 cards each

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cardGroups.size) { groupIndex ->
                    val group = cardGroups[groupIndex]
                    Card(
                        modifier = Modifier
                            .fillParentMaxWidth(0.9f)
                            .wrapContentHeight() // ✅ Dynamic height
                            .shadow(6.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE)),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Two rows of two circular cards
                            for (row in group.chunked(2)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    row.forEach { (title, icon, color) ->
                                        DashboardCard(
                                            title = title,
                                            iconRes = icon,
                                            color = color
                                        ) {
                                            when (title) {
                                                "HotspotMapScreen" -> navController.navigate(ROUT_HOTSPOTMAP)
                                                "Report counterfeit" -> navController.navigate(ROUT_SENDREPORT)
                                                "Medicine" -> navController.navigate(ROUT_VIEW_MEDICINES)
                                                "Profile & Settings" -> navController.navigate(ROUT_PROFILESETTINS)
                                                "Place Order" -> navController.navigate(ROUT_PLACEORDER)
                                                "Supplier Manufacturer" -> navController.navigate(ROUT_SUPPLIERMANUFACTURER)
                                                "Analytics Screen" -> navController.navigate(ROUT_ANALYTICSSCREEN)
                                                "ChatScreen" -> navController.navigate(ROUT_CHATBOARDSCREEN)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Notice Banner ---
            MarqueeText("Important notice: Always check medicine authenticity before purchase!")
        }
    }
}

@Composable
fun DashboardCard(title: String, iconRes: Int, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(110.dp)
            .clickable { onClick() }
            .shadow(6.dp, CircleShape),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    lineHeight = 12.sp
                )
            }
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .offset { IntOffset(translateX.roundToInt(), 0) }
                    .padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )
        }
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