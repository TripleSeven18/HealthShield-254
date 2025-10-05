package com.triple7.healthshield254.ui.screens.verify

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.triple7.healthshield254.ui.theme.triple777
import com.triple7.healthshield254.ui.theme.tripleSeven
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanVerifyScreen(
    navController: NavHostController? = null, //make optional for Preview
    isPreview: Boolean = false
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var verificationResult by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Launcher for taking photo (disabled in preview)
    val cameraLauncher = if (!isPreview) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                imageUri = Uri.fromFile(File(context.cacheDir, "package_photo.jpg"))
                verificationResult = "Authentic"
            }
        }
    } else null

    // Temp file (skip for preview)
    val photoUri = if (!isPreview) {
        val photoFile = File(context.cacheDir, "package_photo.jpg")
        androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            photoFile
        )
    } else null

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(tripleSeven),
                title = { Text(
                    color = Color.White,
                    text = "Scan & Verify (AI-Powered)") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Scan Barcode/QR button
            Button(
                colors = ButtonDefaults.buttonColors(triple777),
                onClick = {
                    verificationResult = "Counterfeit"
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Scan Barcode / QR Code")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Packaging analysis button
            Button(
                colors = ButtonDefaults.buttonColors(triple777),
                onClick = { },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Analyze Packaging (AI Photo Check)")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Show verification result
            verificationResult?.let { result ->
                Text(
                    text = "Verification Result:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Text(
                    text = result,
                    fontSize = 20.sp,
                    color = when {
                        result.contains("Authentic") -> Color(0xFF2E7D32)
                        result.contains("Counterfeit") -> Color(0xFFC62828)
                        else -> Color(0xFFF9A825)
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Powered by AI badge
            Box(
                modifier = Modifier.size(160.dp, 50.dp)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = triple777,
                    shape = CircleShape,
                    shadowElevation = 6.dp
                ) {
                    Text(
                        "AI Powered",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanVerifyScreenPreview() {
    ScanVerifyScreen(isPreview = true) // 👈 disables camera & FileProvider in Preview
}