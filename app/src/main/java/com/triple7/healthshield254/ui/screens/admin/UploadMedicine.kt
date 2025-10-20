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
    // You can remove this LaunchedEffect after running the app once.
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

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val clearForm = {
        name = ""
        dosage = ""
        instructions = ""
        sideEffects = ""
        warnings = ""
        imageUri = null
    }

    val gradientBrush = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.background
        )
    )

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
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = tripleSeven)
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
                                    imageVector = Icons.Default.Info, //AddAPhoto icon
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

                        // Autocomplete Medicine Name
                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                        ) {
                            UploadTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                    filteredMedicines = allMedicines.filter {
                                        it.name.contains(name, ignoreCase = true)
                                    }
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
                        UploadTextField(
                            value = dosage,
                            onValueChange = { dosage = it },
                            label = "Dosage",
                            enabled = !isUploading
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        UploadTextField(
                            value = instructions,
                            onValueChange = { instructions = it },
                            label = "Instructions",
                            enabled = !isUploading
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        UploadTextField(
                            value = sideEffects,
                            onValueChange = { sideEffects = it },
                            label = "Side Effects",
                            enabled = !isUploading
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        UploadTextField(
                            value = warnings,
                            onValueChange = { warnings = it },
                            label = "Warnings",
                            enabled = !isUploading
                        )
                    }
                }


                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (inPreview) return@Button
                        if (name.isBlank() || dosage.isBlank() || imageUri == null) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please fill all fields and select an image.")
                            }
                            return@Button
                        }

                        isUploading = true
                        uploadMedicineToFirebase(
                            imageUri = imageUri!!,
                            medicine = MedicineUpload(name, dosage, instructions, sideEffects, warnings),
                            onSuccess = {
                                isUploading = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Medicine uploaded successfully!")
                                }
                                clearForm()
                            },
                            onFailure = { exception ->
                                isUploading = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Upload failed: ${exception.message}")
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isUploading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Upload Medicine", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

private fun seedInitialMedicinesToFirebase() {
    val medicinesToSeed = listOf(
        MedicineUpload(
            name = "Paracetamol",
            dosage = "500 mg to 1000 mg every 4 to 6 hours as needed.",
            instructions = "Can be taken with or without food. Swallow whole with water. Avoid alcohol.",
            sideEffects = "Nausea, upset stomach, rash. Serious: Liver damage (with overdose), severe allergic reaction.",
            warnings = "Do NOT take if you have severe liver disease or are taking other paracetamol products."
        ),
        MedicineUpload(
            name = "Ibuprofen",
            dosage = "200–400 mg every 6 hours as needed. Max: 1200 mg/day.",
            instructions = "Take with food or milk to prevent stomach upset.",
            sideEffects = "Stomach pain, heartburn, nausea, dizziness.",
            warnings = "Avoid if you have stomach ulcers, kidney disease, or heart failure. Do not combine with alcohol or other NSAIDs."
        ),
        MedicineUpload(
            name = "Amoxicillin",
            dosage = "500 mg every 8 hours or 875 mg every 12 hours for 7–10 days.",
            instructions = "Take with or without food, complete the full course even if you feel better.",
            sideEffects = "Diarrhea, nausea, rash, yeast infection.",
            warnings = "Avoid if allergic to penicillin. Tell your doctor if you have kidney problems."
        ),
        MedicineUpload(
            name = "Metformin",
            dosage = "Typical: 500 mg once or twice daily with meals.",
            instructions = "Take with meals to reduce stomach upset.",
            sideEffects = "Diarrhea, nausea, metallic taste, low vitamin B12 with long-term use.",
            warnings = "Do not use if you have severe kidney or liver disease. Avoid alcohol—can cause lactic acidosis."
        ),
        MedicineUpload(
            name = "Loratadine",
            dosage = "Adults & children over 12: 10 mg once daily.",
            instructions = "Take once daily, with or without food.",
            sideEffects = "Sleepiness (rare), headache, dry mouth.",
            warnings = "Use caution if you have liver disease. Avoid alcohol or sedatives."
        ),
        MedicineUpload(
            name = "Omeprazole",
            dosage = "Adults: 20–40 mg once daily before meals for 14–28 days.",
            instructions = "Swallow whole before breakfast, do not crush or chew.",
            sideEffects = "Headache, constipation, nausea, abdominal pain.",
            warnings = "Long-term use may cause vitamin B12 deficiency, bone weakness, or kidney issues."
        ),
        MedicineUpload(
            name = "Salbutamol (Albuterol)",
            dosage = "Inhaler: 1–2 puffs every 4–6 hours as needed.",
            instructions = "Shake inhaler before use; inhale deeply; rinse mouth afterward.",
            sideEffects = "Tremor, rapid heartbeat, nervousness, headache.",
            warnings = "Do not exceed dose. Seek help if breathing worsens."
        ),
        MedicineUpload(
            name = "Cetirizine",
            dosage = "Adults & children over 12: 10 mg once daily.",
            instructions = "Take once daily, preferably in the evening.",
            sideEffects = "Drowsiness, dry mouth, fatigue.",
            warnings = "Avoid alcohol and driving if drowsy. Use cautiously with kidney or liver disease."
        ),
        MedicineUpload(
            name = "Diclofenac",
            dosage = "Adults: 50 mg every 8 hours as needed. Max: 150 mg/day.",
            instructions = "Take with food to avoid stomach upset.",
            sideEffects = "Stomach pain, nausea, dizziness, heartburn.",
            warnings = "Avoid with ulcers, heart disease, kidney problems, or other NSAIDs."
        )
    )

    val dbRef = FirebaseDatabase.getInstance().getReference("medicines")
    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            medicinesToSeed.forEach { medicineToSeed ->
                // Check if a medicine with the same name already exists
                val alreadyExists = snapshot.children.any { 
                    it.getValue(MedicineUpload::class.java)?.name?.equals(medicineToSeed.name, ignoreCase = true) == true
                }
                if (!alreadyExists) {
                    val id = dbRef.push().key!!
                    dbRef.child(id).setValue(medicineToSeed)
                }
            }
        }
        override fun onCancelled(error: DatabaseError) { /* Handle error */ }
    })
}

private fun uploadMedicineToFirebase(
    imageUri: Uri,
    medicine: MedicineUpload,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val storageRef = FirebaseStorage.getInstance().reference
    val dbRef = FirebaseDatabase.getInstance().getReference("medicines")

    val imageFileName = "images/${UUID.randomUUID()}.jpg"
    val imageRef = storageRef.child(imageFileName)

    val uploadTask = imageRef.putFile(imageUri)

    uploadTask.continueWithTask { task ->
        if (!task.isSuccessful) {
            task.exception?.let {
                throw it
            }
        }
        imageRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result
            val medicineWithUrl = medicine.copy(imageUrl = downloadUri.toString())
            val id = dbRef.push().key

            if (id != null) {
                dbRef.child(id).setValue(medicineWithUrl)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            } else {
                onFailure(Exception("Could not generate a key for the database entry."))
            }
        } else {
            task.exception?.let { 
                onFailure(it) 
            }
        }
    }
}


@Composable
fun UploadTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        label = "borderColor"
    )

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

@Preview(showBackground = true)
@Composable
fun UploadMedicineScreenPreview() {
    HealthShield254Theme {
        UploadMedicineScreen(rememberNavController())
    }
}
