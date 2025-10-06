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
import com.triple7.healthshield254.ui.theme.triple777
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var isDarkTheme by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

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
                            // App logo
                            Image(
                                painter = painterResource(id = R.drawable.medicalinsurance),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 8.dp)
                            )
                            Text(
                                text = "MediCheck",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f)
                            )

                            // Search bar icon
                            IconButton(onClick = { /* Handle search */ }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }

                            // Dark/Light mode toggle
                            class ThemeViewModel : ViewModel() {
                                private val _isDarkTheme = MutableStateFlow(false)
                                val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

                                fun toggleTheme() {
                                    _isDarkTheme.value = !_isDarkTheme.value
                                }
                            }

                            // Notifications
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
                        colors =  NavigationBarItemDefaults.colors(tripleSeven),
                        onClick = {navController.navigate(ROUT_CROWDSOURCING)}
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Scan") },
                        selected = false,
                        colors =  NavigationBarItemDefaults.colors(tripleSeven),
                        onClick = {}
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.MoreVert, contentDescription = "Map") },
                        selected = false,
                        colors =  NavigationBarItemDefaults.colors(tripleSeven),
                        onClick = {}
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Community") },
                        selected = false,
                        colors =  NavigationBarItemDefaults.colors(tripleSeven),
                        onClick = {}
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Face, contentDescription = "Profile") },
                        selected = false,
                        colors =  NavigationBarItemDefaults.colors(tripleSeven),
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tripleSeven
                    ),
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
                    onClick = {navController.navigate(ROUT_CROWDSOURCING)},
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

                // Quick Access Tiles (2x2 Grid)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {navController.navigate(ROUT_HOTSPOTMAP)},
                            shape = RoundedCornerShape(62.dp),
                            colors = ButtonDefaults.buttonColors(tripleSeven),
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Hotspot Map")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hotspot Map")
                        }

                        Button(
                            onClick = {navController.navigate(ROUT_EDUCATIONALHUB)},
                            shape = RoundedCornerShape(62.dp),
                            colors = ButtonDefaults.buttonColors(tripleSeven),
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
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
                            onClick = {navController.navigate(ROUT_PROFILESETTINS)},
                            shape = RoundedCornerShape(62.dp),
                            colors = ButtonDefaults.buttonColors(tripleSeven),
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Profile")
                        }

                        Button(
                            onClick = {navController.navigate(ROUT_VERIFICATIONRECORDS)},
                            shape = RoundedCornerShape(62.dp),
                            colors = ButtonDefaults.buttonColors(tripleSeven),
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
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

                // --- IMAGE CARD AT THE BOTTOM ---
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

                // --- RUNNING TEXT BELOW IMAGE ---
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
fun QuickTile(title: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp)
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = tripleSeven)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(32.dp))

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview(){
    HomeScreen(navController = rememberNavController())
}
