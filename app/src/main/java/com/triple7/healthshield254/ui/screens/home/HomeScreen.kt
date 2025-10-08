package com.triple7.digconnectke254.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.navigation.ROUT_CROWDSOURCING
import com.navigation.ROUT_EDUCATIONALHUB
import com.navigation.ROUT_HOTSPOTMAP
import com.navigation.ROUT_PROFILESETTINS
import com.navigation.ROUT_VERIFICATIONRECORDS
import com.triple7.healthshield254.R
import com.triple7.healthshield254.ui.theme.tripleSeven
import com.triple7.healthshield254.ui.theme.triple777
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var isDarkTheme by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.medicalinsurance),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(32.dp).padding(end = 8.dp)
                            )
                            Text(
                                text = "MediCheck",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { /* Handle search */ }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                            IconButton(onClick = { /* Handle notifications */ }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AccountBox, contentDescription = "AccountBox") },
                        selected = true,
                        colors = NavigationBarItemDefaults.colors(tripleSeven),
                        onClick = { navController.navigate(ROUT_CROWDSOURCING) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Scan") },
                        selected = false,
                        colors = NavigationBarItemDefaults.colors(tripleSeven),
                        onClick = {}
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.MoreVert, contentDescription = "Map") },
                        selected = false,
                        colors = NavigationBarItemDefaults.colors(tripleSeven),
                        onClick = {}
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Community") },
                        selected = false,
                        colors = NavigationBarItemDefaults.colors(tripleSeven),
                        onClick = {}
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Face, contentDescription = "Profile") },
                        selected = false,
                        colors = NavigationBarItemDefaults.colors(tripleSeven),
                        onClick = {}
                    )
                }
            }
        ) { paddingValues ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main Action Buttons
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = tripleSeven),
                    onClick = { navController.navigate("camera_screen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(vertical = 8.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Scan")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan Medicine")
                }

                OutlinedButton(
                    onClick = { navController.navigate(ROUT_CROWDSOURCING) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(vertical = 4.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Report")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Report Suspected Product")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Access Tiles (2x2 Grid) - Uniform size
                Column {
                    val buttonModifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .padding(horizontal = 4.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { navController.navigate(ROUT_HOTSPOTMAP) },
                            shape = RoundedCornerShape(62.dp),
                            colors = ButtonDefaults.buttonColors(tripleSeven),
                            modifier = buttonModifier
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Hotspot Map")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hotspot Map")
                        }

                        Button(
                            onClick = { navController.navigate(ROUT_EDUCATIONALHUB) },
                            shape = RoundedCornerShape(62.dp),
                            colors = ButtonDefaults.buttonColors(tripleSeven),
                            modifier = buttonModifier
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Education Hub")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Education Hub")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { navController.navigate(ROUT_PROFILESETTINS) },
                            shape = RoundedCornerShape(62.dp),
                            colors = ButtonDefaults.buttonColors(tripleSeven),
                            modifier = buttonModifier
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Profile")
                        }

                        Button(
                            onClick = { navController.navigate(ROUT_VERIFICATIONRECORDS) },
                            shape = RoundedCornerShape(62.dp),
                            colors = ButtonDefaults.buttonColors(tripleSeven),
                            modifier = buttonModifier
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "History")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verification Records")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Alerts Banner (Scrollable)
                Text("Alerts", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    items(3) { index ->
                        Card(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .width(250.dp)
                                .height(80.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = tripleSeven),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (index) {
                                        0 -> "!Counterfeit batch reported in Nairobi"
                                        1 -> "New safety notice from PPB"
                                        else -> "Verified medicine database updated"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Image Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = tripleSeven)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nurse),
                        contentDescription = "Nurse Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Running Text Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(tripleSeven),
                    contentAlignment = Alignment.CenterStart
                ) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val translateX by infiniteTransition.animateFloat(
                        initialValue = 1000f,
                        targetValue = -1000f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 15000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )

                    Text(
                        text = " Important notice: Always check medicine authenticity!",
                        modifier = Modifier.offset { IntOffset(translateX.roundToInt(), 0) },
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}
