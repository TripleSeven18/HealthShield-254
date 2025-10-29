package com.triple7.healthshield254.ui.screens.reportmedicine

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.triple7.healthshield254.ui.theme.HealthShield254Theme
import com.triple7.healthshield254.ui.theme.tripleSeven
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanMedicine(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var isScanning by remember { mutableStateOf(false) }
    var barcodeResult by remember { mutableStateOf<String?>(null) }
    var showResultDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
            if (granted) isScanning = true
        }
    )

    DisposableEffect(Unit) {
        onDispose { executor.shutdown() }
    }

    val gradientBrush = Brush.verticalGradient(
        listOf(tripleSeven.copy(alpha = 0.5f), MaterialTheme.colorScheme.background)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Medicine QR/Barcode") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isScanning) isScanning = false
                        else navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(padding)
        ) {
            when {
                !isScanning -> {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Position the QR or barcode inside the frame to scan it.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = {
                                if (hasPermission) isScanning = true
                                else permissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) { Text("Start Scanning", fontSize = 18.sp) }
                    }
                }

                isScanning -> {
                    Box(Modifier.fillMaxSize()) {
                        CameraPreviewView(
                            lifecycleOwner,
                            executor
                        ) { result ->
                            barcodeResult = result
                            showResultDialog = true
                            isScanning = false
                        }
                        ViewfinderOverlay(Modifier.fillMaxSize())
                    }
                }
            }

            if (showResultDialog) {
                AlertDialog(
                    onDismissRequest = { showResultDialog = false },
                    title = { Text("Scan Result") },
                    text = { Text(barcodeResult ?: "No result found.") },
                    confirmButton = {
                        Button(onClick = { showResultDialog = false }) { Text("OK") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = {
                            showResultDialog = false
                            isScanning = true
                        }) { Text("Scan Again") }
                    }
                )
            }
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun CameraPreviewView(
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    executor: ExecutorService,
    onBarcodeDetected: (String) -> Unit
) {
    val context = LocalContext.current
    // Keep a single PreviewView remembered
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        val cameraProvider = context.getCameraProvider()

        // Use fully-qualified name to avoid ambiguity with Compose's @Preview
        val cameraPreview = androidx.camera.core.Preview.Builder()
            .build()
            .also { cameraPreviewInstance ->
                cameraPreviewInstance.setSurfaceProvider(previewView.surfaceProvider)
            }

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(android.util.Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        val scanner = BarcodeScanning.getClient(options)

        // explicit ImageProxy parameter type to satisfy the compiler
        imageAnalysis.setAnalyzer(executor) { imageProxy: ImageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        val result = barcodes.firstOrNull()?.rawValue
                        if (result != null) {
                            // Ensure imageProxy closed before calling back
                            imageProxy.close()
                            (context as? android.app.Activity)?.runOnUiThread {
                                onBarcodeDetected(result)
                            }
                        } else {
                            imageProxy.close()
                        }
                    }
                    .addOnFailureListener {
                        Log.e("ScanMedicine", "Scan failed", it)
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }

        try {
            // bind the preview + analyzer to the lifecycle
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                cameraPreview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e("ScanMedicine", "Camera binding failed", e)
        }
    }

    AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { cont ->
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener(
            { cont.resume(future.get()) },
            ContextCompat.getMainExecutor(this)
        )
    }

/**
 * ViewfinderOverlay
 *
 * Draws:
 *  - Dimmed screen
 *  - Cleared rounded-rect "window" for the camera preview
 *  - Corner markers
 *  - Animated top->bottom scanning beam with soft gradient glow
 */
@Composable
fun ViewfinderOverlay(modifier: Modifier = Modifier) {
    // Composable reads and dp conversions MUST happen outside Canvas
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { 2.dp.toPx() }
    val beamHeightPx = with(density) { 6.dp.toPx() }
    val cornerRadiusPx = with(density) { 16.dp.toPx() }

    // Colors from MaterialTheme — read outside Canvas
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryHighAlpha = primaryColor.copy(alpha = 0.85f)
    val primaryMidAlpha = primaryColor.copy(alpha = 0.45f)
    val glowAlpha = primaryColor.copy(alpha = 0.12f)

    // infinite transition for beam movement (composable)
    val transition = rememberInfiniteTransition()
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        val boxSize = size.width * 0.7f
        val cornerLength = boxSize * 0.15f
        val topLeft = Offset((size.width - boxSize) / 2f, (size.height - boxSize) / 2f)

        // dim the background
        drawRect(color = Color.Black.copy(alpha = 0.5f), size = size)

        // clear a rounded rect hole in the dimmed overlay
        drawRoundRect(
            color = Color.Transparent,
            topLeft = topLeft,
            size = Size(boxSize, boxSize),
            cornerRadius = CornerRadius(cornerRadiusPx),
            blendMode = BlendMode.Clear
        )

        // corner markers (white stroke)
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(topLeft.x, topLeft.y + cornerLength)
            lineTo(topLeft.x, topLeft.y)
            lineTo(topLeft.x + cornerLength, topLeft.y)

            moveTo(topLeft.x + boxSize - cornerLength, topLeft.y)
            lineTo(topLeft.x + boxSize, topLeft.y)
            lineTo(topLeft.x + boxSize, topLeft.y + cornerLength)

            moveTo(topLeft.x, topLeft.y + boxSize - cornerLength)
            lineTo(topLeft.x, topLeft.y + boxSize)
            lineTo(topLeft.x + cornerLength, topLeft.y + boxSize)

            moveTo(topLeft.x + boxSize - cornerLength, topLeft.y + boxSize)
            lineTo(topLeft.x + boxSize, topLeft.y + boxSize)
            lineTo(topLeft.x + boxSize, topLeft.y + boxSize - cornerLength)
        }
        drawPath(path, Color.White, style = Stroke(width = strokeWidthPx))

        // ===== animated scanning beam =====
        val beamY = topLeft.y + progress * boxSize
        val horizontalPadding = boxSize * 0.05f
        val beamLeft = topLeft.x + horizontalPadding
        val beamWidth = boxSize - horizontalPadding * 2f

        val beamBrush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                primaryHighAlpha,
                primaryMidAlpha,
                Color.Transparent
            ),
            startY = beamY - beamHeightPx * 1.5f,
            endY = beamY + beamHeightPx * 1.5f
        )

        val beamTop = (beamY - beamHeightPx / 2f).coerceAtLeast(topLeft.y)
        val beamBottom = (beamY + beamHeightPx / 2f).coerceAtMost(topLeft.y + boxSize)
        val beamActualHeight = (beamBottom - beamTop).coerceAtLeast(0f)

        if (beamActualHeight > 0f) {
            drawRect(
                brush = beamBrush,
                topLeft = Offset(beamLeft, beamTop),
                size = Size(beamWidth, beamActualHeight),
                blendMode = BlendMode.SrcOver
            )

            // subtle secondary glow (wider, low alpha)
            val glowBrush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    glowAlpha,
                    Color.Transparent
                ),
                startY = beamY - beamHeightPx * 4f,
                endY = beamY + beamHeightPx * 4f
            )
            drawRect(
                brush = glowBrush,
                topLeft = Offset(beamLeft - 8f, beamTop - 8f),
                size = Size(beamWidth + 16f, beamActualHeight + 16f),
                blendMode = BlendMode.SrcOver
            )
        }
        // ===== end beam =====
    }
}

@ComposePreview(showBackground = true)
@Composable
fun PreviewScanMedicine() {
    HealthShield254Theme {
        ScanMedicine(rememberNavController())
    }
}