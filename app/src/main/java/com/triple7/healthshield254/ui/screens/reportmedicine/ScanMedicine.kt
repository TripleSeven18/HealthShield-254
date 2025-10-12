//package com.triple7.healthshield254.ui.screens.reportmedicine
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.pm.PackageManager
//import android.util.Log
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.PreviewView
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//import com.google.mlkit.vision.barcode.Barcode
//import com.google.mlkit.vision.barcode.BarcodeScannerOptions
//import com.google.mlkit.vision.barcode.BarcodeScanning
//import com.google.mlkit.vision.common.InputImage
//import com.triple7.healthshield254.R
//import androidx.concurrent.futures.ListenableFuture
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//
//@Composable
//fun ScanMedicine(navController: NavController) {
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val context = LocalContext.current
//
//    var hasPermission by remember {
//        mutableStateOf(
//            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
//                    PackageManager.PERMISSION_GRANTED
//        )
//    }
//
//    var scanning by remember { mutableStateOf(false) }
//    var barcodeResult by remember { mutableStateOf<String?>(null) }
//    val executor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        if (!hasPermission) {
//            // Permission request notice
//            Text(
//                text = "Camera permission required to scan medicine.",
//                color = Color.Red,
//                modifier = Modifier.padding(16.dp)
//            )
//            return@Column
//        }
//
//        // Scan Button / Card
//        Card(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth()
//                .height(80.dp)
//                .clickable { scanning = true },
//            colors = CardDefaults.cardColors(containerColor = Color(0xFF6200EE)),
//            shape = RoundedCornerShape(12.dp)
//        ) {
//            Box(contentAlignment = Alignment.Center) {
//                Text(
//                    "Scan Medicine",
//                    color = Color.White,
//                    style = MaterialTheme.typography.titleLarge
//                )
//            }
//        }
//
//        Spacer(Modifier.height(16.dp))
//
//        // Camera Preview
//        if (scanning) {
//            Box(modifier = Modifier.fillMaxSize()) {
//                CameraPreviewView(
//                    lifecycleOwner = lifecycleOwner,
//                    executor = executor,
//                    onBarcodeDetected = { barcode ->
//                        barcodeResult = barcode
//                        scanning = false
//                    }
//                )
//            }
//        } else {
//            // Show last scanned result
//            barcodeResult?.let {
//                Text("Last scanned barcode: $it", modifier = Modifier.padding(16.dp))
//            }
//        }
//
//        Spacer(Modifier.weight(1f))
//
//        // Bottom center image
//        Image(
//            painter = painterResource(id = R.drawable.medicalinsurance),
//            contentDescription = "Medicine Icon",
//            modifier = Modifier
//                .size(120.dp)
//                .align(Alignment.CenterHorizontally)
//        )
//
//        Spacer(Modifier.height(24.dp))
//    }
//}
//
//@SuppressLint("UnsafeOptInUsageError")
//@Composable
//fun CameraPreviewView(
//    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
//    executor: ExecutorService,
//    onBarcodeDetected: (String) -> Unit
//) {
//    val context = LocalContext.current
//    val previewView = remember { PreviewView(context) }
//
//    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = remember {
//        ProcessCameraProvider.getInstance(context)
//    }
//
//    LaunchedEffect(previewView) {
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//
//            // Preview setup
//            val preview = Preview.Builder().build()
//            preview.setSurfaceProvider(previewView.surfaceProvider)
//
//            // Image analysis for ML Kit
//            val analysis = ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build()
//
//            // ML Kit barcode scanner
//            val options = BarcodeScannerOptions.Builder()
//                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
//                .build()
//            val scanner = BarcodeScanning.getClient(options)
//
//            analysis.setAnalyzer(executor) { imageProxy ->
//                val mediaImage = imageProxy.image
//                if (mediaImage != null) {
//                    val inputImage =
//                        InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//                    scanner.process(inputImage)
//                        .addOnSuccessListener { barcodes ->
//                            barcodes.firstOrNull()?.rawValue?.let { onBarcodeDetected(it) }
//                        }
//                        .addOnFailureListener { Log.e("ScanMedicine", "Barcode scan failed", it) }
//                        .addOnCompleteListener { imageProxy.close() }
//                } else imageProxy.close()
//            }
//
//            // Camera selector
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            // Bind lifecycle
//            cameraProvider.unbindAll()
//            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analysis)
//        }, ContextCompat.getMainExecutor(context))
//    }
//
//    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewScanMedicine() {
//    ScanMedicine(navController = rememberNavController())
//}
