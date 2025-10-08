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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
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
import com.triple7.healthshield254.navigation.ROUT_EDUCATIONALHUB
import com.triple7.healthshield254.navigation.ROUT_HOTSPOTMAP
import com.triple7.healthshield254.navigation.ROUT_PROFILESETTINS
import com.triple7.healthshield254.navigation.ROUT_SENDREPORT
import com.triple7.healthshield254.navigation.ROUT_VERIFICATIONRECORDS
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.regex.Pattern
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var isDarkTheme by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var scannedExpiry by remember { mutableStateOf<String?>(null) }

    if (showScanner) {
        ExpiryScanner(
            onResult = { result ->
                scannedExpiry = result
                showScanner = false
            },
            onClose = { showScanner = false }
        )
    } else {
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
                                IconButton(onClick = { /* Search */ }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                                IconButton(onClick = { /* Notifications */ }) {
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
                            onClick = { /* Nav crowdsource */ }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Scan") },
                            selected = false,
                            colors = NavigationBarItemDefaults.colors(tripleSeven),
                            onClick = { showScanner = true }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.MoreVert, contentDescription = "Map") },
                            selected = false,
                            colors = NavigationBarItemDefaults.colors(tripleSeven),
                            onClick = { /* Map */ }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = "Community") },
                            selected = false,
                            colors = NavigationBarItemDefaults.colors(tripleSeven),
                            onClick = { /* Profile */ }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Face, contentDescription = "Profile") },
                            selected = false,
                            colors = NavigationBarItemDefaults.colors(tripleSeven),
                            onClick = { /* Settings */ }
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
                    // --- Scan Medicine Button ---
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = tripleSeven),
                        onClick = { showScanner = true },
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

                    // --- Optional: show scanned result if available ---
                    scannedExpiry?.let { expiry ->
                        Text(
                            text = "Scanned Expiry: $expiry",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = { navController.navigate(ROUT_SENDREPORT) },
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

                    // Quick Access Grid
                    val buttonModifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .padding(horizontal = 4.dp)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { navController.navigate(ROUT_HOTSPOTMAP)  },
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
                                onClick = { navController.navigate(ROUT_PROFILESETTINS)  },
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
                                colors = CardDefaults.cardColors(containerColor = tripleSeven)
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
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Running text banner
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

    // ✅ Permission launcher
    val launcher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    // ✅ Ask for permission when entering screen
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        } else {
            cameraProvider = ProcessCameraProvider.getInstance(context).get()
        }
    }

    // ✅ Wait until permission is granted
    if (!hasPermission) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Camera permission is required to scan.",
                color = Color.White,
                fontSize = 16.sp
            )
        }
        return
    }

    // ✅ Initialize camera once permission is available
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            cameraProvider = ProcessCameraProvider.getInstance(context).get()
        }
    }

    // === CAMERA PREVIEW ===
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx: android.content.Context ->  // ✅ specify type explicitly
                val previewView = androidx.camera.view.PreviewView(ctx)

                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)  // ✅ correct reference
                }

                val analyzer = androidx.camera.core.ImageAnalysis.Builder().build().also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        CoroutineScope(Dispatchers.Default).launch {
                            processImage(imageProxy) { detected ->
                                launch(Dispatchers.Main) {
                                    onResult(detected)
                                }
                            }
                            imageProxy.close()
                        }
                    }
                }

                val selector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(lifecycleOwner, selector, preview, analyzer)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        FloatingActionButton(
            onClick = onClose,
            containerColor = Color.Red,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }

        Text(
            text = "Scanning expiry date...",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            fontSize = 16.sp
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

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(navController = rememberNavController())
}
