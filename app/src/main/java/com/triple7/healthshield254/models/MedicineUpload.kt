package com.triple7.healthshield254.models

data class MedicineUpload(
    var id: String? = null,            // Unique product ID
    val name: String? = null,          // Medicine name
    val category: String? = null,      // Category (e.g., painkiller, antibiotic)
    val brand: String? = null,         // Manufacturer or brand
    val price: String? = null,         // Medicine price (String for Firebase consistency)
    val description: String? = null,   // Short description of the medicine
    val dosage: String? = null,        // Dosage instructions
    val sideEffects: String? = null,   // Possible side effects
    val warnings: String? = null,      // Warnings for the medicine
    val stock: String? = null,         // Stock quantity
    val phoneNumber: String? = null,   // Seller's contact phone number (if applicable)
    val imageUrl: String? = null      // Cloudinary image URL or similar
)

