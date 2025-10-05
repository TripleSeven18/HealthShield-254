//package com.triple7.healthshield254.ui.screens.reportmedicine
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Context
//import android.net.Uri
//import android.os.Build
//import android.os.Looper
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.horizontalScroll
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.core.content.FileProvider
//import androidx.navigation.compose.rememberNavController
//import coil.compose.rememberAsyncImagePainter
//import com.google.android.gms.location.*
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.text.TextRecognition
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import java.io.File
//
//data class Report(
//    val photos: List<Uri>,
//    val batch: String?,
//    val expiry: String?,
//    val notes: String?,
//    val lat: Double?,
//    val lon: Double?
//)
//
//@RequiresApi(Build.VERSION_CODES.S)
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ReportMedicineScreen(onSend: (Report) -> Unit = {}) {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//
//    var photos by remember { mutableStateOf(listOf<Uri>()) }
//    var batch by remember { mutableStateOf("") }
//    var expiry by remember { mutableStateOf("") }
//    var notes by remember { mutableStateOf(TextFieldValue("")) }
//    var lat by remember { mutableStateOf<Double?>(null) }
//    var lon by remember { mutableStateOf<Double?>(null) }
//
//    // 📷 Camera launcher
//    val takePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//        if (success) {
//            val latestFile = context.cacheDir.listFiles()?.maxByOrNull { it.lastModified() }
//            latestFile?.let { file ->
//                val uri = Uri.fromFile(file)
//                photos = photos + uri
//                runOcrOnImage(context, uri) { recognizedText ->
//                    if (batch.isBlank()) batch = extractBatch(recognizedText) ?: batch
//                    if (expiry.isBlank()) expiry = extractExpiry(recognizedText) ?: expiry
//                }
//            }
//        }
//    }
//
//    // 📍 Location permission launcher
//    val locationLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { granted ->
//        if (granted) {
//            fetchLocation(context) { la, lo ->
//                lat = la
//                lon = lo
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Report Medicine") },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Button(onClick = {
//                val file = File(context.cacheDir, "report_${System.currentTimeMillis()}.jpg").apply { createNewFile() }
//                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
//                takePhotoLauncher.launch(uri)
//            }) {
//                Text("Take Photo")
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // 🖼️ Photo previews
//            if (photos.isNotEmpty()) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .horizontalScroll(rememberScrollState())
//                ) {
//                    photos.forEach { uri ->
//                        Image(
//                            painter = rememberAsyncImagePainter(uri),
//                            contentDescription = null,
//                            modifier = Modifier
//                                .size(100.dp)
//                                .padding(4.dp)
//                        )
//                    }
//                }
//            } else {
//                Text("No photos yet. Capture front, back, and batch/expiry.", color = Color.Gray)
//            }
//
//            Spacer(Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = batch,
//                onValueChange = { batch = it },
//                label = { Text("Batch (auto-extracted)") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = expiry,
//                onValueChange = { expiry = it },
//                label = { Text("Expiry (auto-extracted)") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = notes,
//                onValueChange = { notes = it },
//                label = { Text("Notes (optional)") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(120.dp)
//            )
//
//            Spacer(Modifier.height(12.dp))
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Text(
//                    text = if (lat != null && lon != null)
//                        "Location captured: ${formatLocation(lat, lon)}"
//                    else
//                        "Location (optional)",
//                    modifier = Modifier.weight(1f)
//                )
//                Button(onClick = { locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {
//                    Text(if (lat != null) "Added" else "Add Location")
//                }
//            }
//
//            Spacer(Modifier.height(18.dp))
//
//            Button(
//                onClick = {
//                    scope.launch(Dispatchers.IO) {
//                        val report = Report(
//                            photos,
//                            batch.ifBlank { null },
//                            expiry.ifBlank { null },
//                            notes.text.ifBlank { null },
//                            lat,
//                            lon
//                        )
//                        // TODO: Upload images & send report to backend
//                        launch(Dispatchers.Main) { onSend(report) }
//                    }
//                },
//                enabled = photos.isNotEmpty(),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Send Report")
//            }
//        }
//    }
//}
//
///* -------------------- Helpers -------------------- */
//
//private fun extractBatch(text: String): String? {
//    val regex = Regex("(?i)batch[:\\s-]*([A-Za-z0-9]+)")
//    return regex.find(text)?.groups?.get(1)?.value
//}
//
//private fun extractExpiry(text: String): String? {
//    val regex = Regex("(?i)(exp|expiry)[:\\s-]*([A-Za-z0-9/]+)")
//    return regex.find(text)?.groups?.get(2)?.value
//}
//
//
//fun runOcrOnImage(
//    context: Context,
//    uri: Uri,
//    onResult: (String) -> Unit
//) {
//    try {
//        val image = InputImage.fromFilePath(context, uri)
//        val recognizer = TextRecognition.getClient
//
//        recognizer.process(image)
//            .addOnSuccessListener { visionText ->
//                onResult(visionText.text)
//            }
//            .addOnFailureListener { e ->
//                e.printStackTrace()
//                onResult("")
//            }
//    } catch (e: Exception) {
//        e.printStackTrace()
//        onResult("")
//    }
//}
//
//@RequiresApi(Build.VERSION_CODES.S)
//@SuppressLint("MissingPermission")
//private fun fetchLocation(context: Context, onResult: (Double?, Double?) -> Unit) {
//    try {
//        val fused = LocationServices.getFusedLocationProviderClient(context)
//        val request = LocationRequest.Builder(
//            Priority.PRIORITY_BALANCED_POWER_ACCURACY, 2000L
//        ).build()
//        val callback = object : LocationCallback() {
//            override fun onLocationResult(result: LocationResult) {
//                fused.removeLocationUpdates(this)
//                val loc = result.lastLocation
//                onResult(loc?.latitude, loc?.longitude)
//            }
//        }
//        fused.requestLocationUpdates(request, callback, Looper.getMainLooper())
//    } catch (e: Exception) {
//        e.printStackTrace()
//        onResult(null, null)
//    }
//}
//
///* -------------------- Preview -------------------- */
//
//@RequiresApi(Build.VERSION_CODES.S)
//@Preview(showBackground = true)
//@Composable
//fun ReportMedicineScreenPreview() {
//    ReportMedicineScreen()
//}
