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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.ShoppingCartCheckout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        bottomBar = {
            HomeBottomNavigation(
                navController = navController,
                onProfileClick = { navController.navigate(ROUT_PROFILESETTINS) }
            )
        }
    ) { paddingValues ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // --- ELONGATED HERO SECTION (3/4 of screen height) ---
            // Removed shadow and Card to eliminate any white artifact behind the green section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.75f)
                    .clip(RoundedCornerShape(bottomStart = 120.dp, bottomEnd = 20.dp))
                    .background(tripleSeven)
            ) {
                // Modern Physician Gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(tripleSeven, Color(0xFF002200))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // 1. YOUR PROFILE & PERSONAL DETAILS
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "Physician Profile",
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .padding(4.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            TailedDashboardIcon(
                                icon = Icons.Default.Person,
                                containerColor = Color.White,
                                iconColor = tripleSeven,
                                iconSize = 22.dp
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Dr. $displayName",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = user?.email ?: "Medical Practitioner",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Verified Clinician Profile",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 2. INTEGRATED ACTION PANEL (Scan + Order)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        HeroCompactAction(
                            title = "Authenticity Scanner",
                            info = "AI-powered pharmaceutical verification",
                            icon = Icons.Rounded.QrCodeScanner,
                            onClick = { navController.navigate(ROUT_SCANMEDICINE) }
                        )

                        HeroCompactAction(
                            title = "Inventory & Orders",
                            info = "Procure from verified supply chains",
                            icon = Icons.Rounded.ShoppingCartCheckout,
                            onClick = { navController.navigate(ROUT_PLACEORDER) }
                        )
                    }
                }
            }

            // --- REMAINING DASHBOARD SERVICES ---
            Spacer(modifier = Modifier.height(30.dp))
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Medical Services Hub",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = Color(0xFF2C3E50)
                )
                Spacer(modifier = Modifier.height(20.dp))

                val serviceColors = listOf(
                    Color(0xFF3498DB), Color(0xFF2ECC71), Color(0xFFF1C40F), Color(0xFFE74C3C),
                    Color(0xFF9B59B6), Color(0xFF1ABC9C), Color(0xFF27AE60),
                    Color(0xFFE67E22), Color(0xFF34495E)
                )

                val dashboardItems = listOf(
                    Triple(R.string.hotspot_map_screen, R.drawable.hotspotmap, serviceColors[0]),
                    Triple(R.string.report_counterfeit, R.drawable.reportcounterfeit, serviceColors[1]),
                    Triple(R.string.medicine, R.drawable.medicine, serviceColors[2]),
                    Triple(R.string.profile_settings, R.drawable.profile, serviceColors[3]),
                    Triple(R.string.supplier_manufacturer, R.drawable.supplier, serviceColors[4]),
                    Triple(R.string.analytics_screen, R.drawable.supplier, serviceColors[5]),
                    Triple(R.string.chat_screen, R.drawable.supplier, serviceColors[6]),
                    Triple(R.string.consultation, R.drawable.consultation, serviceColors[7]),
                    Triple(R.string.bot_enquiry, R.drawable.verification, serviceColors[8])
                )

                dashboardItems.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { (name, icon, bgColor) ->
                            ServiceCard(
                                titleRes = name,
                                iconRes = icon,
                                color = bgColor,
                                modifier = Modifier.weight(1f)
                            ) {
                                when (name) {
                                    R.string.hotspot_map_screen -> navController.navigate(ROUT_HOTSPOTMAP)
                                    R.string.report_counterfeit -> navController.navigate(ROUT_SENDREPORT)
                                    R.string.medicine -> navController.navigate(ROUT_VIEW_MEDICINES)
                                    R.string.profile_settings -> navController.navigate(ROUT_PROFILESETTINS)
                                    R.string.supplier_manufacturer -> navController.navigate(ROUT_SUPPLIERMANUFACTURER)
                                    R.string.analytics_screen -> navController.navigate(ROUT_ANALYTICSSCREEN)
                                    R.string.chat_screen -> navController.navigate(ROUT_CHATBOARDSCREEN)
                                    R.string.consultation -> navController.navigate(ROUT_CONSULTATION)
                                    R.string.bot_enquiry -> navController.navigate(ROUT_BOT_ENQUIRY)
                                }
                            }
                        }
                        if (row.size < 3) repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            MarqueeText(stringResource(R.string.important_notice))
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun HeroCompactAction(
    title: String,
    info: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TailedDashboardIcon(
                icon = icon,
                containerColor = Color.White,
                iconColor = tripleSeven,
                iconSize = 24.dp
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                Text(info, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_eye_open),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun TailedDashboardIcon(
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    iconSize: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(iconSize)
            .drawBehind {
                val r = size.width / 2
                val cx = size.width / 2
                val cy = size.height / 2
                
                val path = Path().apply {
                    val startAngle = Math.toRadians(310.0)
                    val x1 = (cx + r * Math.cos(startAngle)).toFloat()
                    val y1 = (cy + r * Math.sin(startAngle)).toFloat()
                    
                    moveTo(x1, y1)
                    lineTo(size.width * 1.5f, y1 - 4.dp.toPx())
                    
                    val endAngle = Math.toRadians(350.0)
                    val x2 = (cx + r * Math.cos(endAngle)).toFloat()
                    val y2 = (cy + r * Math.sin(endAngle)).toFloat()
                    lineTo(x2, y2)
                    close()
                }
                drawPath(path, color = containerColor)
            }
            .background(containerColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(iconSize * 0.6f)
        )
    }
}

@Composable
fun ServiceCard(titleRes: Int, iconRes: Int, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val title = stringResource(id = titleRes)
    Box(
        modifier = modifier
            .aspectRatio(1.1f)
            .clip(RoundedCornerShape(22.dp))
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
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
            .height(44.dp)
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFFC107)),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            modifier = Modifier
                .offset { IntOffset(translateX.roundToInt(), 0) }
                .padding(horizontal = 20.dp),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

@Composable
fun HomeBottomNavigation(
    navController: NavController,
    onProfileClick: () -> Unit
) {
    NavigationBar(containerColor = tripleSeven, tonalElevation = 0.dp) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.verification),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Reports", fontSize = 10.sp, color = Color.White) },
            selected = false,
            onClick = { navController.navigate(ROUT_VIEWREPORT) },
            colors = NavigationBarItemDefaults.colors(unselectedTextColor = Color.White)
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.scan),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Scanner", fontSize = 10.sp, color = Color.White) },
            selected = false,
            onClick = { navController.navigate(ROUT_SCANMEDICINE) },
            colors = NavigationBarItemDefaults.colors(unselectedTextColor = Color.White)
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Account", fontSize = 10.sp, color = Color.White) },
            selected = false,
            onClick = { onProfileClick() },
            colors = NavigationBarItemDefaults.colors(unselectedTextColor = Color.White)
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
