package com.triple7.healthshield254.models

data class Order(
    val id: String = "",
    val productId: String = "",
    val productName: String? = "",
    val buyerType: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val sellerId: String? = "",
    val quantity: Int = 1,
    val paymentMethod: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isApproved: Boolean = false,
    val receipt: String = ""
)
