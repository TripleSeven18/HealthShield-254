package com.triple7.healthshield254.ui.screens.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        topBar = { HomeTopBar() },
        bottomBar = { HomeBottomNavigation(showScanner = { showScanner = true }) }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // --- Greeting / Hero Card ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hello, Ralib", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Good Morning", color = Color.Gray, fontSize = 14.sp)
                    }
                    Image(
                        painter = painterResource(id = R.drawable.nurse),
                        contentDescription = "Profile",
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(30.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Search Bar ---
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search doctors, medicine...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

// --- Scan Button ---
            GradientButton(
                text = "Scan Medicine Screen",
                icon = Icons.Default.Info,
                gradient = Brush.horizontalGradient(listOf(Color(0xFFEE0979), Color(0xFFFF6A00)))
            ) {
                // Navigate to ScanMedicine screen
//                navController.navigate(ROUT_SCANMEDICINE)
            }


            Spacer(modifier = Modifier.height(24.dp))

            // --- Popular Doctors Carousel ---
            Text("Popular Doctors", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val doctors = listOf(
                    R.drawable.nurse, R.drawable.medicalinsurance,
                    R.drawable.img_18, R.drawable.img
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
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

            // --- Quick Action Grid ---
            Column {
                // First row (existing)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard("Hotspot Map", Icons.Default.LocationOn, Color(0xFFFFC107)) {
                        navController.navigate(ROUT_HOTSPOTMAP)
                    }
                    DashboardCard("Education Hub", Icons.Default.Info, Color(0xFF4CAF50)) {
                        navController.navigate(ROUT_EDUCATIONALHUB)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Second row (existing)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard("Profile", Icons.Default.Person, Color(0xFF03A9F4)) {
                        navController.navigate(ROUT_PROFILESETTINS)
                    }
                    DashboardCard("Verification Records", Icons.Default.Info, Color(0xFFE91E63)) {
                        navController.navigate(ROUT_VERIFICATIONRECORDS)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- New row 1 ---
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard("Crowdsourcing Hub", Icons.Default.AccountBox, Color(0xFF9C27B0)) {
                        navController.navigate("crowdsourcingHub")
                    }
                    DashboardCard("Scan Medicine", Icons.Default.Info, Color(0xFF00BCD4)) {
                        navController.navigate(ROUT_CROWDSOURCING)
                    }
                }  //QrCodeScanner Icon

                Spacer(modifier = Modifier.height(12.dp))

                // --- New row 2 ---
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard("Hotline", Icons.Default.Call, Color(0xFFFF5722)) {
                        navController.navigate(ROUT_MEDICINE)
                    }
                    DashboardCard("Medicine Guide", Icons.Default.Info, Color(0xFF8BC34A)) {
                        navController.navigate(ROUT_REPORTMEDICINE)
                    }
                } //Book Icon

                Spacer(modifier = Modifier.height(12.dp))

                // --- New row 3 ---
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard("Nearby Clinics", Icons.Default.Info, Color(0xFF607D8B)) {
                        navController.navigate(ROUT_VERIFICATIONRECORDS)
                    } //LocalHospital Icon
                    DashboardCard("view report", Icons.Default.Info, Color(0xFFFF9800)) {
                        navController.navigate(ROUT_VIEWREPORT)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // --- New row 3 ---
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DashboardCard("Report Screen", Icons.Default.Info, Color(0xFF607D8B)) {
                    navController.navigate(ROUT_SENDREPORT)
                } //LocalHospital Icon
                DashboardCard("Place Order", Icons.Default.Info, Color(0xFFFF9800)) {
                        navController.navigate(ROUT_PLACEORDER)
                }
            }

            // --- New row 3 ---
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DashboardCard("Scan-History", Icons.Default.Info, Color(0xFF607D8B)) {
                    navController.navigate(ROUT_VERIFICATIONRECORDS)
                } //LocalHospital Icon
                DashboardCard("Supplier Manufacturer", Icons.Default.Info, Color(0xFFFF9800)) {
                    navController.navigate(ROUT_SUPPLIERMANUFACTURER)
                }
            }

            //Feedback Icon


            Spacer(modifier = Modifier.height(20.dp))

            // --- Running Notice Banner ---
            MarqueeText("⚠ Important notice: Always check medicine authenticity before purchase! ")
        }
    }
}


@SuppressLint("UnsafeOptInUsageError")
@Composable
fun ExpiryScanner(onResult: (String) -> Unit, onClose: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
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
                it.setSurfaceProvider(previewView.surfaceProvider)  // ✅ correct reference
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
            val fakeDetectedText = "12/2025" // Simulated OCR result
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.medicalinsurance),
                    contentDescription = "Logo",
                    modifier = Modifier.size(32.dp).padding(end = 8.dp)
                )
                Text(
                    text = "HealthShield254",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { /* Search */ }) { Icon(Icons.Default.Search, contentDescription = null) }
                IconButton(onClick = { /* Notifications */ }) { Icon(Icons.Default.Notifications, contentDescription = null) }
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = tripleSeven)
    )
}

@Composable
fun HomeBottomNavigation(showScanner: () -> Unit) {
    NavigationBar(containerColor = tripleSeven) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountBox, contentDescription = "Account") },
            selected = true,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "Scan") },
            selected = false,
            onClick = { showScanner() }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "Map") },
            selected = false,
            onClick = { /* Navigate */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            selected = false,
            onClick = { /* Navigate */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            selected = false,
            onClick = { /* Navigate */ }
        )
    }
}

@Composable
fun DashboardCard(title: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(100.dp)
            .clickable { onClick() }
            .padding(4.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(32.dp), tint = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
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


@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(navController = rememberNavController())
}
