package com.triple7.healthshield254.ui.screens.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.R
import com.triple7.healthshield254.navigation.*
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.regex.Pattern
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var showScanner by remember { mutableStateOf(false) }
    var scannedExpiry by remember { mutableStateOf<String?>(null) }

    if (showScanner) {
        ExpiryScanner(
            onResult = { scannedExpiry = it; showScanner = false },
            onClose = { showScanner = false }
        )
        return
    }

    Scaffold(
        bottomBar = {
            HomeBottomNavigation(
                navController = navController,
                showScanner = { showScanner = true },
                onProfileClick = { navController.navigate(ROUT_PROFILESETTINS) }
            )
        }
    ) { paddingValues ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFCEFF9))
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
                        text = "Hello, John 👋",
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
                text = "Scan Medicine Screen",
                icon = Icons.Default.Info,
                gradient = Brush.horizontalGradient(listOf(Color(0xFFEE0979), Color(0xFFFF6A00)))
            ) {
                // navController.navigate(ROUT_SCANMEDICINE)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Popular Doctors Carousel ---
            Text("Popular Doctors", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val doctors = listOf(
                    R.drawable.nurse,
                    R.drawable.medicalinsurance,
                    R.drawable.img_18,
                    R.drawable.img
                )
                items(doctors.size) { index ->
                    Card(
                        modifier = Modifier
                            .width(160.dp)
                            .height(200.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = doctors[index]),
                                contentDescription = "Doctor $index",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(50.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Dr. John Doe", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Cardiologist", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Dashboard Cards (Horizontally Scrollable, Two per Column) ---
            val dashboardItems = listOf(
                Triple("Hotspot Map", R.drawable.hotspotmap, Color(0xFFFFC107)),
                Triple("Report counterfeit", R.drawable.reportcounterfeit, Color(0xFF03A9F4)),
                Triple("Medicine", R.drawable.medicine, Color(0xFFFF5722)),
                Triple("Profile & Settings", R.drawable.profile, Color(0xFF607D8B)),
                Triple("Place Order", R.drawable.placeorder, Color(0xFFFF9800)),
                Triple("Supplier Manufacturer", R.drawable.supplier, Color(0xFF009688)),
                Triple("Analytics Screen", R.drawable.supplier, Color(0xFF3F51B5)),
                Triple("ChatBoard Screen", R.drawable.supplier, Color(0xFF795548)),
                Triple("Admin Screen", R.drawable.supplier, Color(0xFFCDDC39)),
                Triple("Upload-Medicine Screen", R.drawable.supplier, Color(0xFF673AB7))
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = dashboardItems.chunked(2)) { pair ->   // Use `items(items = ...)`
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
                                        "Report counterfeit" -> navController.navigate(ROUT_SENDREPORT)
                                        "Medicine" -> navController.navigate(ROUT_MEDICINE)
                                        "Place Order" -> navController.navigate(ROUT_PLACEORDER)
                                        "Supplier Manufacturer" -> navController.navigate(ROUT_SUPPLIERMANUFACTURER)
                                        "Analytics Screen" -> navController.navigate(ROUT_ANALYTIVCSCREEN)
                                        "ChatBoard Screen" -> navController.navigate(ROUT_CHATBOARDCHSCREEN)
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

// --- Remaining Composables unchanged ---
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun ExpiryScanner(onResult: (String) -> Unit, onClose: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    if (!hasPermission) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission is required to scan.", color = Color.White)
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx)

            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    CoroutineScope(Dispatchers.Default).launch {
                        processImage(imageProxy) { detected ->
                            launch(Dispatchers.Main) { onResult(detected) }
                        }
                        imageProxy.close()
                    }
                }
            }

            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(lifecycleOwner, selector, preview, analyzer)

            previewView
        }, modifier = Modifier.fillMaxSize())

        FloatingActionButton(
            onClick = onClose,
            containerColor = Color.Red,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }

        Text(
            text = "Scanning expiry date...",
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
        )
    }
}

private suspend fun processImage(imageProxy: ImageProxy, onTextDetected: (String) -> Unit) {
    withContext(Dispatchers.Default) {
        try {
            val fakeDetectedText = "12/2025"
            val expiryPattern = Pattern.compile("(0[1-9]|1[0-2])/20\\d{2}")
            val matcher = expiryPattern.matcher(fakeDetectedText)
            if (matcher.find()) {
                onTextDetected(matcher.group(0)!!)
            }
        } catch (e: Exception) {
            Log.e("ExpiryScanner", "Error processing image", e)
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
    val infiniteTransition = rememberInfiniteTransition()
    val translateX by infiniteTransition.animateFloat(
        initialValue = 1000f,
        targetValue = -1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
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
    showScanner: () -> Unit,
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
            onClick = { showScanner() }
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
            onClick = { navController.navigate(ROUT_PROFILESETTINS) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(navController = rememberNavController())
}