package com.triple7.healthshield254.ui.screens.admin

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.triple7.healthshield254.models.MedicineUpload
import com.triple7.healthshield254.navigation.ROUT_VIEW_MEDICINES
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch

// --- VIEWMODEL FOR CLOUDINARY & FIREBASE ---
class MedicineUploadViewModel : ViewModel() {
    private val productsRef = FirebaseDatabase.getInstance().getReference("products")

    fun uploadMedicine(
        imageUris: List<Uri>,
        name: String, description: String, category: String, brand: String, price: String,
        dosage: String, sideEffects: String, warnings: String, stock: String, phoneNumber: String,
        uploaderId: String, uploaderType: String, uploaderName: String,
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (imageUris.isEmpty() || name.isBlank() || price.isBlank() || stock.isBlank()) {
                Toast.makeText(context, "Please fill all fields and select at least one image.", Toast.LENGTH_LONG).show()
                return@launch
            }

            val mediaManager = try {
                MediaManager.get()
            } catch (e: IllegalStateException) {
                Toast.makeText(context, "Error: Cloudinary not initialized.", Toast.LENGTH_LONG).show()
                return@launch
            }

            val imageUrls = mutableListOf<String>()
            val latch = CountDownLatch(imageUris.size)
            var uploadFailed = false

            withContext(Dispatchers.IO) {
                for (uri in imageUris) {
                    mediaManager.upload(uri).unsigned("unsigned_medicine_upload").callback(object : UploadCallback {
                        override fun onStart(requestId: String) {}
                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                            imageUrls.add(resultData["secure_url"] as String)
                            latch.countDown()
                        }
                        override fun onError(requestId: String, error: ErrorInfo) {
                            uploadFailed = true
                            latch.countDown()
                        }
                        override fun onReschedule(requestId: String, error: ErrorInfo) {}
                    }).dispatch()
                }
                latch.await()
            }

            if (uploadFailed) {
                Toast.makeText(context, "Image upload failed. Check preset name.", Toast.LENGTH_LONG).show()
                return@launch
            }

            val medicine = MedicineUpload(
                name = name, description = description, category = category, brand = brand,
                price = price, dosage = dosage, sideEffects = sideEffects, warnings = warnings,
                stock = stock, phoneNumber = phoneNumber, imageUrls = imageUrls,
                uploaderId = uploaderId, uploaderType = uploaderType, uploaderName = uploaderName
            )

            try {
                val medicineId = productsRef.push().key ?: ""
                productsRef.child(medicineId).setValue(medicine.copy(id = medicineId)).await()
                Toast.makeText(context, "Medicine uploaded successfully!", Toast.LENGTH_SHORT).show()
                onSuccess()
            } catch (e: Exception) {
                Toast.makeText(context, "Database save failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

// --- UI: UPLOAD PRODUCT SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadProductScreen(
    navController: NavController,
    currentUserType: String,
    currentUserId: String,
) {
    val viewModel: MedicineUploadViewModel = viewModel()
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val user = if (isPreview) null else FirebaseAuth.getInstance().currentUser
    val userName = remember(user) {
        user?.displayName?.takeIf { it.isNotBlank() } ?: user?.email?.split('@')?.get(0)?.replaceFirstChar { it.uppercase() } ?: "Seller"
    }

    var name by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var brand by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var dosage by rememberSaveable { mutableStateOf("") }
    var sideEffects by rememberSaveable { mutableStateOf("") }
    var warnings by rememberSaveable { mutableStateOf("") }
    var stock by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    
    var imageUris by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        imageUris = uris.map { it.toString() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Medicine", color = Color.White) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven, navigationIconContentColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { launcher.launch("image/*") }, containerColor = tripleSeven) {
                Icon(Icons.Default.AddAPhoto, "Select Images")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            if (imageUris.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(imageUris) { uriString ->
                        ImagePreviewItem(Uri.parse(uriString))
                    }
                }
            } else {
                Text("Select images using the camera button.", color = Color.Gray, modifier = Modifier.padding(vertical = 40.dp))
            }
            
            Spacer(Modifier.height(20.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Medicine Name") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    OutlinedTextField(brand, { brand = it }, label = { Text("Brand/Manufacturer") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    
                    var expanded by remember { mutableStateOf(false) }
                    val options = listOf("Painkiller", "Antibiotic", "Supplement", "Antiseptic", "Other")
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        OutlinedTextField(category, {}, readOnly = true, label = { Text("Category") }, modifier = Modifier.fillMaxWidth().clickable { expanded = true }, trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown", Modifier.clickable { expanded = true }) })
                        DropdownMenu(expanded, { expanded = false }, modifier = Modifier.fillMaxWidth()) {
                            options.forEach { selectionOption ->
                                DropdownMenuItem(text = { Text(selectionOption) }, onClick = { category = selectionOption; expanded = false })
                            }
                        }
                    }
                    
                    OutlinedTextField(price, { price = it }, label = { Text("Price") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    OutlinedTextField(stock, { stock = it }, label = { Text("Stock Quantity") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    OutlinedTextField(dosage, { dosage = it }, label = { Text("Dosage") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    OutlinedTextField(sideEffects, { sideEffects = it }, label = { Text("Side Effects") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    OutlinedTextField(warnings, { warnings = it }, label = { Text("Warnings") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    OutlinedTextField(phoneNumber, { phoneNumber = it }, label = { Text("Contact Phone") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    OutlinedTextField(description, { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).height(100.dp), maxLines = 4)
                }
            }
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (user == null) {
                        Toast.makeText(context, "Authentication required.", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    viewModel.uploadMedicine(
                        imageUris.map { Uri.parse(it) }, name, description, category, brand, price, dosage, sideEffects, warnings, stock, phoneNumber,
                        currentUserId, currentUserType, userName, context,
                        onSuccess = { navController.navigate(ROUT_VIEW_MEDICINES) }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = tripleSeven)
            ) {
                Text("Upload Medicine", color = Color.White)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun ImagePreviewItem(uri: Uri) {
    Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray)) {
        AsyncImage(
            model = uri,
            contentDescription = "Selected Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UploadProductScreenPreview() {
    UploadProductScreen(rememberNavController(), currentUserType = "Pharmacist", currentUserId = "previewUser")
}
