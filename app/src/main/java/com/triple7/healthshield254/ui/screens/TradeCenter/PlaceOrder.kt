package com.triple7.healthshield254.ui.screens.TradeCenter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlinx.coroutines.launch

// ---------------------------
// DATA MODELS
// ---------------------------
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val uploadedBy: String = "",
    val uploaderId: String = "",
    val supplyChain: List<SupplyChainEvent> = emptyList()
)

data class Order(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val buyerType: String = "",
    val buyerId: String = "",
    val quantity: Int = 1,
    val supplyChain: List<SupplyChainEvent> = emptyList()
)

data class SupplyChainEvent(
    val actorType: String = "",
    val actorId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = ""
)

// ---------------------------
// PLACE ORDER SCREEN
// ---------------------------
@Composable
fun PlaceOrderScreen(
    currentUserType: String,
    currentUserId: String
) {
    val database = FirebaseDatabase.getInstance().reference
    val coroutineScope = rememberCoroutineScope()

    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // --- Mock multiple products to fill the screen ---
    LaunchedEffect(Unit) {
        loading = true
        products = List(15) { index ->
            Product(
                id = "p$index",
                name = "Medicine $index",
                description = "Description of Medicine $index",
                price = (5..50).random().toDouble(),
                uploadedBy = if (index % 2 == 0) "Manufacturer" else "Supplier",
                uploaderId = "user$index",
                supplyChain = listOf(
                    SupplyChainEvent("Manufacturer", "user$index", status = "Produced")
                )
            )
        }
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Text(
            text = "Place Orders",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color(0xFF2E7D32)
        )
        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text(text = "Error: $error", color = Color.Red)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .background(Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Gradient Header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(Modifier.height(8.dp))
                            Text(product.description, fontSize = 14.sp, color = Color.DarkGray)
                            Spacer(Modifier.height(4.dp))
                            Text("Price: \$${product.price}", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))

                            // Supply Chain Badges
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                product.supplyChain.forEach { event ->
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = Color(0xFFE3F2FD),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            "${event.actorType}: ${event.status}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF0D47A1)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Place Order Button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val orderId = database.child("orders").push().key ?: return@clickable
                                        val newOrder = Order(
                                            id = orderId,
                                            productId = product.id,
                                            productName = product.name,
                                            buyerType = currentUserType,
                                            buyerId = currentUserId,
                                            quantity = 1,
                                            supplyChain = product.supplyChain + SupplyChainEvent(
                                                actorType = currentUserType,
                                                actorId = currentUserId,
                                                status = "Ordered"
                                            )
                                        )
                                        database.child("orders").child(orderId).setValue(newOrder)
                                    }
                                    .background(Color(0xFF388E3C), RoundedCornerShape(12.dp))
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Place Order", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------
// PREVIEW
// ---------------------------
@Preview(showBackground = true)
@Composable
fun PreviewPlaceOrderScreen() {
    PlaceOrderScreen(currentUserType = "Consumer", currentUserId = "user123")
}