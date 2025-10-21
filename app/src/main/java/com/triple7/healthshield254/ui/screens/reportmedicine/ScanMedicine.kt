package com.triple7.healthshield254.ui.screens.reportmedicine

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.tooling.preview.Preview
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

    var hasPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    var isScanning by remember { mutableStateOf(false) }
    var barcodeResult by remember { mutableStateOf<String?>(null) }
    var showResultDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
            if (isGranted) {
                isScanning = true
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose { executor.shutdown() }
    }

    val gradientBrush = Brush.verticalGradient(colors = listOf(tripleSeven.copy(alpha = 0.5f), MaterialTheme.colorScheme.background))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Medicine QR/Barcode") },
                navigationIcon = {
                    IconButton(onClick = { if (isScanning) isScanning = false else navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(gradientBrush).padding(padding)) {
            when {
                !isScanning -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scanner Icon", modifier = Modifier.size(120.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Position the QR or barcode inside the frame to scan it.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = {
                                if (hasPermission) isScanning = true else permissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Start Scanning", fontSize = 18.sp)
                        }
                    }
                }
                isScanning -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CameraPreviewView(
                            lifecycleOwner = lifecycleOwner,
                            executor = executor,
                            onBarcodeDetected = { result ->
                                barcodeResult = result
                                showResultDialog = true
                                isScanning = false
                            }
                        )
                        ViewfinderOverlay(modifier = Modifier.fillMaxSize())
                    }
                }
            }

            if (showResultDialog) {
                AlertDialog(
                    onDismissRequest = { showResultDialog = false },
                    title = { Text("Scan Result") },
                    text = { Text(barcodeResult ?: "No result found.") },
                    confirmButton = { Button(onClick = { showResultDialog = false }) { Text("OK") } },
                    dismissButton = {
                        OutlinedButton(onClick = { showResultDialog = false; isScanning = true }) {
                            Text("Scan Again")
                        }
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
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(previewView) {
        val cameraProvider = context.getCameraProvider()
        val preview = androidx.camera.core.Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
        val scanner = BarcodeScanning.getClient(options)

        imageAnalysis.setAnalyzer(executor) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        barcodes.firstOrNull()?.rawValue?.let(onBarcodeDetected)
                    }
                    .addOnFailureListener { Log.e("ScanMedicine", "Barcode scanning failed.", it) }
                    .addOnCompleteListener { imageProxy.close() }
            } else {
                imageProxy.close()
            }
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
        } catch (exc: Exception) {
            Log.e("ScanMedicine", "Use case binding failed", exc)
        }
    }

    AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProviderFuture ->
        cameraProviderFuture.addListener({
            continuation.resume(cameraProviderFuture.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

@Composable
fun ViewfinderOverlay(modifier: Modifier = Modifier) {
    val strokeWidth = with(LocalDensity.current) { 2.dp.toPx() }

    Canvas(modifier = modifier) {
        val boxSize = size.width * 0.7f
        val cornerLength = boxSize * 0.15f
        val topLeft = Offset((size.width - boxSize) / 2, (size.height - boxSize) / 2)

        drawRect(color = Color.Black.copy(alpha = 0.5f), size = size)
        drawRoundRect(
            topLeft = topLeft,
            size = Size(boxSize, boxSize),
            cornerRadius = CornerRadius(16.dp.toPx()),
            color = Color.Transparent,
            blendMode = BlendMode.Clear
        )

        val path = androidx.compose.ui.graphics.Path()
        path.moveTo(topLeft.x, topLeft.y + cornerLength)
        path.lineTo(topLeft.x, topLeft.y)
        path.lineTo(topLeft.x + cornerLength, topLeft.y)

        path.moveTo(topLeft.x + boxSize - cornerLength, topLeft.y)
        path.lineTo(topLeft.x + boxSize, topLeft.y)
        path.lineTo(topLeft.x + boxSize, topLeft.y + cornerLength)

        path.moveTo(topLeft.x, topLeft.y + boxSize - cornerLength)
        path.lineTo(topLeft.x, topLeft.y + boxSize)
        path.lineTo(topLeft.x + cornerLength, topLeft.y + boxSize)

        path.moveTo(topLeft.x + boxSize - cornerLength, topLeft.y + boxSize)
        path.lineTo(topLeft.x + boxSize, topLeft.y + boxSize)
        path.lineTo(topLeft.x + boxSize, topLeft.y + boxSize - cornerLength)

        drawPath(path = path, color = Color.White, style = Stroke(width = strokeWidth))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScanMedicine() {
    HealthShield254Theme {
        ScanMedicine(rememberNavController())
    }
}
