package com.triple7.healthshield254.ui.screens.reportmedicine

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.triple7.healthshield254.data.MedicineViewModel
import com.triple7.healthshield254.models.MedicineUpload
import com.triple7.healthshield254.navigation.ROUT_ADD_MEDICINE
import com.triple7.healthshield254.navigation.ROUT_UPDATE_MEDICINE
import com.triple7.healthshield254.ui.screens.admin.AddMedicineScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicinesScreen(navController: NavController) {
    val medicineViewModel: MedicineViewModel = viewModel()
    val medicines = medicineViewModel.medicines
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    // Fetch medicines from Firebase
    LaunchedEffect(Unit) {
        medicineViewModel.fetchMedicines(context)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Medicines") },
                    actions = {
                        IconButton(onClick = { /* TODO: Add menu options */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFFC107),
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text("Search medicines...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFC107),
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFFFC107)) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(ROUT_ADD_MEDICINE) }, // Navigate to Add Medicine
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Medicine") },
                    label = { Text("Add") }
                )
            }
        }
    ) { paddingValues ->

        // Filtered list
        val filteredMedicines = medicines.filter { med ->
            med.name?.contains(searchQuery, ignoreCase = true) == true
        }

        // Grid layout
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredMedicines) { medicine ->
                MedicineCard(
                    medicine = medicine,
                    onDelete = {  },
                    navController = navController
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineCard(
    medicine: MedicineUpload,
    onDelete: (String) -> Unit,
    navController: NavController
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    // Delete confirmation sheet
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            containerColor = Color.White,
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Delete Medicine?", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Are you sure you want to delete ${medicine.name}?")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = { showBottomSheet = false }) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            showBottomSheet = false
                            medicine.id?.let { onDelete(it) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Card showing medicine info
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Medicine Image
            medicine.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Medicine Image",
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Medicine name & price
            Text(medicine.name ?: "Unnamed", style = MaterialTheme.typography.titleMedium)
            Text("Price: Ksh${medicine.price ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(4.dp))
            Text("Dosage: ${medicine.dosage ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
            Text("Stock: ${medicine.stock ?: "N/A"}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))


            // Contact Pharmacist via SMS
            val context = LocalContext.current
            Button(
                onClick = {
                    val smsIntent = Intent(Intent.ACTION_SENDTO)
                    smsIntent.data = "smsto:${medicine.phoneNumber}".toUri()
                    smsIntent.putExtra("sms_body", "Hello, I’d like to inquire about ${medicine.name}.")
                    context.startActivity(smsIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                Text("Message Pharmacist")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MedicinesScreenPreview() {
    MedicinesScreen(rememberNavController())
}
