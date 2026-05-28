package com.triple7.healthshield254.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    }
    val displayName = userName ?: stringResource(id = R.string.default_user_name)

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
                        in 0..12 -> stringResource(R.string.good_morning)
                        in 12..15 -> stringResource(R.string.good_afternoon)
                        else -> stringResource(R.string.good_evening)
                    }

                    Text(
                        text = stringResource(R.string.hello_user, displayName),
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
                        contentDescription = stringResource(R.string.notifications),
                        modifier = Modifier.size(28.dp),
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Scan Medicine Button ---
            GradientButton(
                text = stringResource(R.string.scan_medicine),
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
                stringResource(R.string.your_profile),
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
                        contentDescription = stringResource(R.string.profile_picture),
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(displayName, fontWeight = FontWeight.Bold)
                        user?.email?.let { Text(it, color = Color.Gray) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Dashboard Items Grid ---
            val impressiveColors = listOf(
                Color(0xFF4A90E2), Color(0xFF50E3C2), Color(0xFFB8E986), Color(0xFFF5A623),
                Color(0xFFF8E71C), Color(0xFF8B572A), Color(0xFF7ED321), Color(0xFF9013FE),
                Color(0xFFE91E63), Color(0xFF009688)
            )

            val dashboardItems = listOf(
                Triple(R.string.hotspot_map_screen, R.drawable.hotspotmap, impressiveColors[0]),
                Triple(R.string.report_counterfeit, R.drawable.reportcounterfeit, impressiveColors[1]),
                Triple(R.string.medicine, R.drawable.medicine, impressiveColors[2]),
                Triple(R.string.profile_settings, R.drawable.profile, impressiveColors[3]),
                Triple(R.string.place_order, R.drawable.placeorder, impressiveColors[4]),
                Triple(R.string.supplier_manufacturer, R.drawable.supplier, impressiveColors[5]),
                Triple(R.string.analytics_screen, R.drawable.supplier, impressiveColors[6]),
                Triple(R.string.chat_screen, R.drawable.supplier, impressiveColors[7]),
                Triple(R.string.consultation, R.drawable.consultation, impressiveColors[8]),
                Triple(R.string.bot_enquiry, R.drawable.verification, impressiveColors[9])
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                dashboardItems.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { (titleRes, icon, color) ->
                            DashboardCard(
                                titleRes = titleRes,
                                iconRes = icon,
                                color = color,
                                modifier = Modifier.weight(1f)
                            ) {
                                when (titleRes) {
                                    R.string.hotspot_map_screen -> navController.navigate(ROUT_HOTSPOTMAP)
                                    R.string.report_counterfeit -> navController.navigate(ROUT_SENDREPORT)
                                    R.string.medicine -> navController.navigate(ROUT_VIEW_MEDICINES)
                                    R.string.profile_settings -> navController.navigate(ROUT_PROFILESETTINS)
                                    R.string.place_order -> navController.navigate(ROUT_PLACEORDER)
                                    R.string.supplier_manufacturer -> navController.navigate(ROUT_SUPPLIERMANUFACTURER)
                                    R.string.analytics_screen -> navController.navigate(ROUT_ANALYTICSSCREEN)
                                    R.string.chat_screen -> navController.navigate(ROUT_CHATBOARDSCREEN)
                                    R.string.consultation -> navController.navigate(ROUT_CONSULTATION)
                                    R.string.bot_enquiry -> navController.navigate(ROUT_BOT_ENQUIRY)
                                }
                            }
                        }
                        // Add empty boxes if the row is not full to maintain alignment
                        if (rowItems.size < 3) {
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Notice Banner ---
            MarqueeText(stringResource(R.string.important_notice))
        }
    }
}

@Composable
fun DashboardCard(titleRes: Int, iconRes: Int, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val title = stringResource(id = titleRes)
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() }
            .shadow(4.dp, CircleShape),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    lineHeight = 11.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2
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
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
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
                    contentDescription = stringResource(R.string.verification),
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
                    contentDescription = stringResource(R.string.scan),
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
                    contentDescription = stringResource(R.string.profile),
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