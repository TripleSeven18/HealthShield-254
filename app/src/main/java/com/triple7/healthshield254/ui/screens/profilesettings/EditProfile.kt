package com.triple7.healthshield254.ui.screens.profilesettings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.triple7.healthshield254.ui.theme.HealthShield254Theme
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(navController: NavController) {
    val inPreview = LocalInspectionMode.current
    val auth = if (!inPreview) FirebaseAuth.getInstance() else null
    val currentUser = auth?.currentUser

    // --- State Management ---
    var name by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val currentPhotoUrl = remember { currentUser?.photoUrl?.toString() }
    var isUpdating by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // --- Image Picker ---
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    val gradientBrush = Brush.verticalGradient(colors = listOf(tripleSeven.copy(alpha = 0.1f), Color.Transparent))

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Profile Image Section ---
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 20.dp)) {
                val painter = rememberAsyncImagePainter(imageUri ?: currentPhotoUrl ?: "https://cdn-icons-png.flaticon.com/512/3135/3135715.png")
                Image(
                    painter = painter,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit photo",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(8.dp)
                )
            }

            // --- Input Fields ---
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = currentUser?.email ?: "",
                onValueChange = {}, // Not editable
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false // Disabled
            )

            Spacer(Modifier.height(24.dp))

            // --- Save Button ---
            Button(
                onClick = {
                    if (!inPreview) {
                        coroutineScope.launch {
                            isUpdating = true
                            try {
                                val newPhotoUrl = if (imageUri != null) {
                                    uploadProfilePicture(imageUri!!, currentUser!!.uid)
                                } else {
                                    currentPhotoUrl
                                }

                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .setPhotoUri(newPhotoUrl?.let { Uri.parse(it) })
                                    .build()

                                currentUser?.updateProfile(profileUpdates)?.await()
                                snackbarHostState.showSnackbar("Profile updated successfully!")
                                navController.popBackStack()
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Failed to update profile: ${e.message}")
                            } finally {
                                isUpdating = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isUpdating
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Save Changes")
                }
            }
        }
    }
}

// --- Helper for Firebase Storage ---
suspend fun uploadProfilePicture(uri: Uri, userId: String): String {
    val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/$userId.jpg")
    // Upload the file to Firebase Storage
    storageRef.putFile(uri).await()
    // Get the download URL
    return storageRef.downloadUrl.await().toString()
}


@Preview(showBackground = true)
@Composable
fun PreviewHEditProfile() {
    HealthShield254Theme {
        EditProfile(rememberNavController())
    }
}
