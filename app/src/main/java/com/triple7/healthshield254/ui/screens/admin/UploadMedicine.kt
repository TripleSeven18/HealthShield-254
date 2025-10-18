package com.triple7.healthshield254.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import com.triple7.healthshield254.R

// Data model
data class MedicineUpload(
    val name: String = "",
    val dosage: String = "",
    val instructions: String = "",
    val sideEffects: String = "",
    val warnings: String = "",
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadMedicineScreen(navController: NavController) {
    // Prevent Firebase or runtime API calls from running in Preview Mode
    val inPreview = LocalInspectionMode.current
    val dbRef = if (!inPreview) FirebaseDatabase.getInstance().getReference("medicines") else null

    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var sideEffects by remember { mutableStateOf("") }
    var warnings by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var isUploading by remember { mutableStateOf(false) }
    var uploadMessage by remember { mutableStateOf("") }

    // Image picker launcher (disabled in Preview)
    val imagePickerLauncher =
        if (!inPreview) {
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                imageUri = uri
            }
        } else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Upload Medicine Info",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF004D40)
                ),
                actions = {
                    IconButton(onClick = { /* Placeholder for info or notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF004D40)) {
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.popBackStack() },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White) },
                    label = { Text("Home", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Add future actions here */ },
                    icon = { Icon(Icons.Default.Create, contentDescription = "Save", tint = Color.White) },
                    label = { Text("Save", color = Color.White) }
                )
            }
        }
    ) { paddingValues ->

        // Background gradient
        val backgroundBrush = Brush.verticalGradient(
            listOf(Color(0xFF147380), Color(0xFF00BCD4))
        )

        // ✅ Added scroll state
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundBrush)
                .padding(paddingValues)
                .verticalScroll(scrollState) // 👈 Enables scrolling
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Enter Medicine Details",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFFB2FFCB),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Upload image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1C2F2B))
                    .clickable(enabled = !inPreview) { imagePickerLauncher?.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Medicine Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Upload Icon",
                            tint = Color(0xFFB2FFCB),
                            modifier = Modifier.size(50.dp)
                        )
                        Text(
                            "Tap to upload image",
                            color = Color(0xFFB2FFCB),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input fields
            UploadTextField(value = name, onValueChange = { name = it }, label = "Medicine Name")
            UploadTextField(value = dosage, onValueChange = { dosage = it }, label = "Dosage (e.g. 500mg)")
            UploadTextField(value = instructions, onValueChange = { instructions = it }, label = "Instructions")
            UploadTextField(value = sideEffects, onValueChange = { sideEffects = it }, label = "Side Effects")
            UploadTextField(value = warnings, onValueChange = { warnings = it }, label = "Warnings")

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (!inPreview) {
                        if (name.isNotEmpty() && dosage.isNotEmpty()) {
                            isUploading = true
                            val id = dbRef?.push()?.key ?: return@Button
                            val medicine = MedicineUpload(
                                name = name,
                                dosage = dosage,
                                instructions = instructions,
                                sideEffects = sideEffects,
                                warnings = warnings,
                                imageUrl = imageUri?.toString() ?: ""
                            )
                            dbRef.child(id).setValue(medicine).addOnCompleteListener {
                                isUploading = false
                                uploadMessage = if (it.isSuccessful)
                                    "✅ Medicine uploaded successfully!"
                                else
                                    "❌ Upload failed. Try again."
                            }
                        } else {
                            uploadMessage = "⚠️ Please fill in at least the medicine name and dosage."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Submit", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            if (uploadMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uploadMessage,
                    color = if (uploadMessage.contains("✅")) Color(0xFF80CBC4) else Color.Red,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun UploadTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFFB2FFCB)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF26A69A),
            unfocusedBorderColor = Color(0xFF004D40),
            cursorColor = Color(0xFF26A69A),
            focusedLabelColor = Color(0xFFB2FFCB),
            unfocusedLabelColor = Color(0xFF7BD6A5),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun UploadMedicinePreview() {
    UploadMedicineScreen(rememberNavController())
}
