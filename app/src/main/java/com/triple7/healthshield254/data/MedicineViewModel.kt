package com.triple7.healthshield254.data

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.triple7.healthshield254.models.MedicineUpload
import com.google.firebase.database.FirebaseDatabase
import com.triple7.healthshield254.navigation.ROUT_VIEW_MEDICINES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.InputStream

class MedicineViewModel : ViewModel() {

    private val cloudinaryUrl = "https://api.cloudinary.com/v1_1/djj5gylbp/image/upload"
    private val uploadPreset = "wewenunua"

    /** ---------------- UPLOAD MEDICINE ---------------- */
    fun uploadMedicine(
        imageUri: Uri?,
        name: String,
        category: String,
        price: String,
        description: String,
        stock: String,
        phoneNumber: String,  // Added phone number
        dosage: String,       // Added dosage
        sideEffects: String,  // Added side effects
        warnings: String,     // Added warnings
        context: Context,
        navController: NavController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Upload the image to Cloudinary and get the URL
                val imageUrl = imageUri?.let { uploadToCloudinary(context, it) }
                val ref = FirebaseDatabase.getInstance().getReference("Medicines").push()

                // Create the Medicine data to store in Firebase
                val medicineData = mapOf(
                    "id" to ref.key,
                    "name" to name,
                    "category" to category,
                    "price" to price,
                    "description" to description,
                    "stock" to stock,
                    "phoneNumber" to phoneNumber,
                    "dosage" to dosage,
                    "sideEffects" to sideEffects,
                    "warnings" to warnings,
                    "imageUrl" to imageUrl
                )

                // Save the data to Firebase
                ref.setValue(medicineData).await()

                // Notify the user and navigate to the list of products
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Medicine saved successfully", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUT_VIEW_MEDICINES)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Medicine not saved", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /** ---------------- CLOUDINARY UPLOAD ---------------- */
    private fun uploadToCloudinary(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val fileBytes = inputStream?.readBytes() ?: throw Exception("Image read failed")

        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", "image.jpg",
                RequestBody.create("image/*".toMediaTypeOrNull(), fileBytes)
            )
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder().url(cloudinaryUrl).post(requestBody).build()
        val response = OkHttpClient().newCall(request).execute()

        if (!response.isSuccessful) throw Exception("Upload failed")
        val responseBody = response.body?.string()

        val secureUrl = Regex("\"secure_url\":\"(.*?)\"")
            .find(responseBody ?: "")?.groupValues?.get(1)

        return secureUrl ?: throw Exception("Failed to get image URL")
    }

    /** ---------------- FETCH MEDICINES ---------------- */
    private val _medicines = mutableStateListOf<MedicineUpload>()
    val medicines: List<MedicineUpload> = _medicines

    fun fetchMedicines(context: Context) {
        val ref = FirebaseDatabase.getInstance().getReference("Medicines")
        ref.get().addOnSuccessListener { snapshot ->
            _medicines.clear()
            for (child in snapshot.children) {
                val medicine = child.getValue(MedicineUpload::class.java)
                medicine?.let {
                    it.id = child.key
                    _medicines.add(it)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to load medicines", Toast.LENGTH_LONG).show()
        }
    }



}
