// file: com/triple7/healthshield254/ui/screens/reportmedicine/ReportMedicineScreen.kt
package com.triple7.healthshield254.ui.screens.reportmedicine

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.*
import com.google.firebase.database.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.triple7.healthshield254.ui.theme.triple777
import com.triple7.healthshield254.ui.theme.tripleSeven
import java.io.File

data class Report(
    val photos: List<Uri> = emptyList(),
    val batch: String? = null,
    val expiry: String? = null,
    val notes: String? = null,
    val lat: Double? = null,
    val lon: Double? = null
)

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportMedicineScreen(onSend: (Report) -> Unit = {}) {
    val context = LocalContext.current
    val isInPreviewMode = LocalInspectionMode.current
    val scope = rememberCoroutineScope()

    var photos by remember { mutableStateOf(listOf<Uri>()) }
    var batch by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(TextFieldValue("")) }
    var lat by remember { mutableStateOf<Double?>(null) }
    var lon by remember { mutableStateOf<Double?>(null) }

    var isCounterfeit by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var purchaseHistory by remember { mutableStateOf(listOf<Report>()) }
    var suspiciousBatches by remember { mutableStateOf(listOf<String>()) }

    if (!isInPreviewMode) {
        val database = FirebaseDatabase.getInstance()
        val reportsRef = database.getReference("reports")
        val suspiciousRef = database.getReference("suspicious_batches")

        LaunchedEffect(Unit) {
            suspiciousRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    suspiciousBatches = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
            reportsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    purchaseHistory = snapshot.children.mapNotNull { it.getValue(Report::class.java) }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    } else {
        // Dummy data for preview rendering
        purchaseHistory = listOf(
            Report(batch = "XYZ123", expiry = "12/2026", notes = "Sample", lat = -1.286, lon = 36.817)
        )
        suspiciousBatches = listOf("XYZ123")
    }

    val takePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && !isInPreviewMode) {
            val latestFile = context.cacheDir.listFiles()?.maxByOrNull { it.lastModified() }
            latestFile?.let { file ->
                val uri = Uri.fromFile(file)
                photos = photos + uri
                runOcrOnImage(context, uri) { recognizedText ->
                    if (batch.isBlank()) batch = extractBatch(recognizedText) ?: batch
                    if (expiry.isBlank()) expiry = extractExpiry(recognizedText) ?: expiry
                    if (batch in suspiciousBatches) {
                        isCounterfeit = true
                        alertMessage = "⚠️ Suspicious medicine detected! Batch: $batch"
                    }
                }
            }
        }
    }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && !isInPreviewMode) fetchLocation(context) { la, lo -> lat = la; lon = lo }
    }

    val sendReport: () -> Unit = {
        val report = Report(
            photos,
            batch.ifBlank { null },
            expiry.ifBlank { null },
            notes.text.ifBlank { null },
            lat,
            lon
        )
        if (!isInPreviewMode) {
            FirebaseDatabase.getInstance().getReference("reports").push().setValue(report)
        }
        onSend(report)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Medicine") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isCounterfeit) Text(alertMessage, color = Color.Red, modifier = Modifier.padding(8.dp))

            Button(onClick = {
                val file = File(context.cacheDir, "report_${System.currentTimeMillis()}.jpg").apply { createNewFile() }
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                takePhotoLauncher.launch(uri)
            }) { Text("Take Photo") }

            Spacer(Modifier.height(12.dp))

            if (photos.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    photos.forEach { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp).padding(4.dp)
                        )
                    }
                }
            } else Text("No photos yet. Capture front, back, and batch/expiry.", color = Color.Gray)

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = batch, onValueChange = { batch = it }, label = { Text("Batch (auto-extracted)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = expiry, onValueChange = { expiry = it }, label = { Text("Expiry (auto-extracted)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") }, modifier = Modifier.fillMaxWidth().height(120.dp))

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (lat != null && lon != null) "Location captured: ${formatLocation(lat, lon)}" else "Location (optional)",
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {
                    Text(if (lat != null) "Added" else "Add Location")
                }
            }

            Spacer(Modifier.height(18.dp))
            Button(colors = ButtonDefaults.buttonColors(triple777), onClick = sendReport, enabled = photos.isNotEmpty() || isInPreviewMode, modifier = Modifier.fillMaxWidth()) {
                Text("Send Report")
            }

            Spacer(Modifier.height(12.dp))
            Button(onClick = { if (!isInPreviewMode) exportReports(context, purchaseHistory) }, enabled = purchaseHistory.isNotEmpty(), modifier = Modifier.fillMaxWidth()) {
                Text("Export Verification Logs")
            }

            Spacer(Modifier.height(12.dp))
            if (purchaseHistory.isNotEmpty()) {
                Text("Analytics Dashboard", style = MaterialTheme.typography.titleMedium)
                val suspiciousCount = purchaseHistory.count { it.batch in suspiciousBatches }
                Text("Total suspicious medicines: $suspiciousCount")
            }
        }
    }
}

/* ---------- Helpers ---------- */

private fun extractBatch(text: String): String? =
    Regex("(?i)batch[:\\s-]*([A-Za-z0-9]+)").find(text)?.groups?.get(1)?.value

private fun extractExpiry(text: String): String? =
    Regex("(?i)(exp|expiry)[:\\s-]*([A-Za-z0-9/]+)").find(text)?.groups?.get(2)?.value

fun runOcrOnImage(context: Context, uri: Uri, onResult: (String) -> Unit) {
    try {
        val image = InputImage.fromFilePath(context, uri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { visionText -> onResult(visionText.text) }
            .addOnFailureListener { onResult("") }
    } catch (e: Exception) { onResult("") }
}

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("MissingPermission")
private fun fetchLocation(context: Context, onResult: (Double?, Double?) -> Unit) {
    try {
        val fused = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 2000L).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                fused.removeLocationUpdates(this)
                val loc = result.lastLocation
                onResult(loc?.latitude, loc?.longitude)
            }
        }
        fused.requestLocationUpdates(request, callback, Looper.getMainLooper())
    } catch (e: Exception) { onResult(null, null) }
}

private fun formatLocation(lat: Double?, lon: Double?): String =
    if (lat != null && lon != null) String.format("%.5f, %.5f", lat, lon) else "N/A"

private fun exportReports(context: Context, reports: List<Report>) {
    val csv = buildString {
        append("Batch,Expiry,Notes,Lat,Lon,Photos\n")
        reports.forEach { r ->
            append("${r.batch ?: ""},${r.expiry ?: ""},${r.notes ?: ""},${r.lat ?: ""},${r.lon ?: ""},${r.photos.joinToString(";")}\n")
        }
    }
    File(context.cacheDir, "verification_logs.csv").writeText(csv)
}

/* ---------- Preview ---------- */
@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun ReportMedicineScreenPreview() {
    ReportMedicineScreen()
}