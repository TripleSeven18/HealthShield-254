package com.triple7.healthshield254.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.triple7.healthshield254.ui.theme.HealthShield254Theme
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

// Data model for medicine
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
    val inPreview = LocalInspectionMode.current

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var sideEffects by remember { mutableStateOf("") }
    var warnings by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // Autocomplete states
    var allMedicines by remember { mutableStateOf<List<MedicineUpload>>(emptyList()) }
    var filteredMedicines by remember { mutableStateOf<List<MedicineUpload>>(emptyList()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // This will run once to populate your database with the initial set of medicines.
    LaunchedEffect(Unit) {
        if (!inPreview) {
            seedInitialMedicinesToFirebase()
        }
    }

    // Fetch all medicines from Firebase for autocomplete
    LaunchedEffect(Unit) {
        if (!inPreview) {
            val dbRef = FirebaseDatabase.getInstance().getReference("medicines")
            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val medicineList = snapshot.children.mapNotNull {
                        it.getValue(MedicineUpload::class.java)
                    }
                    allMedicines = medicineList
                }
                override fun onCancelled(error: DatabaseError) { /* Handle error */ }
            })
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    val clearForm = {
        name = ""
        dosage = ""
        instructions = ""
        sideEffects = ""
        warnings = ""
        imageUri = null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Upload Medicine") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary, // Typo fixed
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = tripleSeven) // Using your specified background color
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Image Upload Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable(enabled = !isUploading) {
                            if (!inPreview) imagePickerLauncher.launch("image/*")
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Selected medicine image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info, // AddAPhoto Icon
                                    contentDescription = "Upload Icon",
                                    modifier = Modifier.size(50.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Tap to select image",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Details Input Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                        ) {
                            UploadTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                    filteredMedicines = allMedicines.filter { med -> med.name.contains(name, ignoreCase = true) }
                                    isDropdownExpanded = true
                                },
                                label = "Medicine Name",
                                enabled = !isUploading,
                                modifier = Modifier.menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded && filteredMedicines.isNotEmpty(),
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                filteredMedicines.forEach { medicine ->
                                    DropdownMenuItem(
                                        text = { Text(medicine.name) },
                                        onClick = {
                                            name = medicine.name
                                            dosage = medicine.dosage
                                            instructions = medicine.instructions
                                            sideEffects = medicine.sideEffects
                                            warnings = medicine.warnings
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        UploadTextField(value = dosage, onValueChange = { dosage = it }, label = "Dosage", enabled = !isUploading)
                        Spacer(modifier = Modifier.height(8.dp))
                        UploadTextField(value = instructions, onValueChange = { instructions = it }, label = "Instructions", enabled = !isUploading)
                        Spacer(modifier = Modifier.height(8.dp))
                        UploadTextField(value = sideEffects, onValueChange = { sideEffects = it }, label = "Side Effects", enabled = !isUploading)
                        Spacer(modifier = Modifier.height(8.dp))
                        UploadTextField(value = warnings, onValueChange = { warnings = it }, label = "Warnings", enabled = !isUploading)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- SUBMIT BUTTON WITH ROBUST UPLOAD LOGIC ---
                Button(
                    onClick = {
                        if (inPreview || imageUri == null || name.isBlank()) {
                            coroutineScope.launch { snackbarHostState.showSnackbar("Please fill all fields and select an image.") }
                            return@Button
                        }

                        coroutineScope.launch {
                            isUploading = true
                            try {
                                // 1. Upload image and get URL
                                val imageUrl = uploadImageToFirebase(imageUri!!)

                                // 2. Create medicine data object
                                val medicineData = MedicineUpload(name, dosage, instructions, sideEffects, warnings, imageUrl)

                                // 3. Save to Realtime Database
                                val dbRef = FirebaseDatabase.getInstance().getReference("medicines")
                                val id = dbRef.push().key!!
                                dbRef.child(id).setValue(medicineData).await()

                                // 4. Show success and clear form
                                snackbarHostState.showSnackbar("Medicine uploaded successfully!")
                                clearForm()

                            } catch (e: Exception) {
                                // 5. Show failure message
                                snackbarHostState.showSnackbar("Upload failed: ${e.message}")
                            } finally {
                                isUploading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isUploading
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Upload Medicine")
                    }
                }
            }
        }
    }
}

// --- NEW ROBUST UPLOAD FUNCTION ---
suspend fun uploadImageToFirebase(imageUri: Uri): String {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageFileName = "images/${UUID.randomUUID()}.jpg"
    val imageRef = storageRef.child(imageFileName)

    // Upload the file and wait for the operation to complete
    imageRef.putFile(imageUri).await()
    // Get the download URL and wait for it
    return imageRef.downloadUrl.await().toString()
}


@Composable
fun UploadTextField(
    value: String, onValueChange: (String) -> Unit, label: String, enabled: Boolean, modifier: Modifier = Modifier) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(targetValue = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, label = "")

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        enabled = enabled,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline
        )
    )
}

private fun seedInitialMedicinesToFirebase() {
    val medicinesToSeed = listOf(
        MedicineUpload("Paracetamol", "500mg", "Take 1-2 tablets every 4-6 hours.", "Nausea, stomach pain.", "Do not exceed 8 tablets in 24 hours."),
        MedicineUpload("Ibuprofen", "200mg", "Take with food.", "Heartburn, dizziness.", "Avoid if you have stomach ulcers.")
        // Add more initial medicines here
    )

    val dbRef = FirebaseDatabase.getInstance().getReference("medicines")
    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.childrenCount < medicinesToSeed.size) { // Simple check to avoid re-seeding
                medicinesToSeed.forEach { medicine ->
                    // Check if a medicine with the same name already exists to be more robust
                    val alreadyExists = snapshot.children.any { 
                        it.getValue(MedicineUpload::class.java)?.name?.equals(medicine.name, ignoreCase = true) == true 
                    }
                    if (!alreadyExists) {
                         val id = dbRef.push().key!!
                         dbRef.child(id).setValue(medicine)
                    }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {}
    })
}

@Preview(showBackground = true)
@Composable
fun UploadMedicineScreenPreview() {
    HealthShield254Theme {
        UploadMedicineScreen(rememberNavController())
    }
}
