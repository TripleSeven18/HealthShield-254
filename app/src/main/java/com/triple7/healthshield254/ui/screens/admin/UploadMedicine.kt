package com.triple7.healthshield254.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.triple7.healthshield254.R
import com.triple7.healthshield254.data.MedicineViewModel
import com.triple7.healthshield254.navigation.ROUT_VIEW_MEDICINES
import com.triple7.healthshield254.ui.theme.tripleSeven

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(navController: NavController) {

    // ----------------------------- STATE VARIABLES -----------------------------
    var name by remember { mutableStateOf("") }            // Medicine name
    var category by remember { mutableStateOf("") }        // Medicine category
    var price by remember { mutableStateOf("") }           // Price
    var description by remember { mutableStateOf("") }     // Description / purpose
    var dosage by remember { mutableStateOf("") }          // Dosage instructions
    var sideEffects by remember { mutableStateOf("") }     // Side effects
    var warnings by remember { mutableStateOf("") }        // Warnings
    var stock by remember { mutableStateOf("") }           // Stock available
    var phoneNumber by remember { mutableStateOf("") }     // Seller / pharmacist contact

    val imageUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imageUri.value = it }
    }

    val medicineViewModel: MedicineViewModel = viewModel()
    val context = LocalContext.current
    var selectedIndex by remember { mutableStateOf(0) }

    // ----------------------------- UI SCAFFOLD -----------------------------
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Medicine", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = tripleSeven,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = tripleSeven) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "View") },
                    label = { Text("View") },
                    selected = selectedIndex == 1,
                    onClick = { navController.navigate(ROUT_VIEW_MEDICINES) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { launcher.launch("image/*") },
                containerColor = tripleSeven
            ) {
                Icon(Icons.Default.Add, contentDescription = "Pick Image")
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // ----------------------------- IMAGE PICKER -----------------------------
                Card(
                    modifier = Modifier
                        .size(160.dp)
                        .clickable { launcher.launch("image/*") },
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    AnimatedContent(
                        targetState = imageUri.value,
                        label = "Image Picker Animation"
                    ) { targetUri ->
                        AsyncImage(
                            model = targetUri ?: R.drawable.ic_launcher_foreground,
                            contentDescription = "Medicine Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Text(
                    text = "Choose an image to upload",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                )

                // ----------------------------- MEDICINE FORM -----------------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        // Medicine Name
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Medicine Name") },
                            placeholder = { Text("e.g., Paracetamol") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                        )

                        // Category dropdown
                        var expanded by remember { mutableStateOf(false) }
                        val options = listOf("Painkiller", "Antibiotic", "Supplement", "Antiseptic", "Other")
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = { },
                                label = { Text("Category") },
                                placeholder = { Text("Select category") },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        Modifier.clickable { expanded = !expanded }
                                    )
                                },
                                shape = RoundedCornerShape(12.dp)
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                options.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            category = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Price
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Price") },
                            placeholder = { Text("e.g., 250") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Dosage
                        OutlinedTextField(
                            value = dosage,
                            onValueChange = { dosage = it },
                            label = { Text("Dosage") },
                            placeholder = { Text("e.g., 1 tablet every 6 hours") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Side Effects
                        OutlinedTextField(
                            value = sideEffects,
                            onValueChange = { sideEffects = it },
                            label = { Text("Side Effects") },
                            placeholder = { Text("e.g., Nausea, Drowsiness") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Warnings
                        OutlinedTextField(
                            value = warnings,
                            onValueChange = { warnings = it },
                            label = { Text("Warnings") },
                            placeholder = { Text("e.g., Not for children under 12") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Stock
                        OutlinedTextField(
                            value = stock,
                            onValueChange = { stock = it },
                            label = { Text("Stock Quantity") },
                            placeholder = { Text("e.g., 100") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Phone Number
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Pharmacist Contact") },
                            placeholder = { Text("e.g., +254712345678") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Description
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            placeholder = { Text("Brief description of medicine") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 4
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ----------------------------- ACTION BUTTONS -----------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        modifier = Modifier.width(140.dp)
                    ) {
                        Text("Cancel", color = Color.DarkGray)
                    }

                    Button(
                        onClick = {
                            medicineViewModel.uploadMedicine(
                                imageUri.value,
                                name,
                                category,
                                price,
                                description,
                                stock,
                                phoneNumber,
                                dosage,
                                sideEffects,
                                warnings,
                                context,
                                navController
                            )
                            navController.navigate(ROUT_VIEW_MEDICINES)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = tripleSeven),
                        modifier = Modifier.width(140.dp)
                    ) {
                        Text("Save", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddMedicineScreenPreview() {
    AddMedicineScreen(rememberNavController())
}
