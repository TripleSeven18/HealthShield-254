package com.triple7.healthshield254.ui.screens.TradeCenter

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.database.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

// --- Professional Color Palette ---
val HealthShieldBlue = Color(0xFF007BFF)
val HealthShieldTextDark = Color(0xFF1A2E35)
val HealthShieldBackgroundLight = Color(0xFFF4F7F9)
val HealthShieldAccentGreen = Color(0xFF28A745)
val HealthShieldCardBackground = Color.White

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
    val supplyChain: List<SupplyChainEvent> = emptyList(), // <- FIXED: Removed trailing comma
)

data class Order(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val buyerType: String = "",
    val buyerId: String = "",
    val quantity: Int = 1,
    val supplyChain: List<SupplyChainEvent> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
)

data class SupplyChainEvent(
    val actorType: String = "",
    val actorId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "",
)

// ---------------------------
// VIEWMODEL FOR DATA FETCHING
// ---------------------------
class PlaceOrderViewModel : ViewModel() {
    private val _products = mutableStateOf<List<Product>>(emptyList())
    val products: State<List<Product>> get() = _products

    private val _loading = mutableStateOf(true)
    val loading: State<Boolean> get() = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> get() = _error

    private val database = FirebaseDatabase.getInstance().getReference("products")
    private var eventListener: ValueEventListener? = null

    /**
     * Attaches a real-time listener to the 'products' node in Firebase.
     * The UI will automatically update whenever the data changes.
     */
    fun startListeningForProducts() {
        _loading.value = true
        eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = snapshot.children.mapNotNull { it.getValue(Product::class.java) }
                _products.value = productList
                _loading.value = false
                _error.value = null
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _error.value = databaseError.message
                _loading.value = false
            }
        }
        database.addValueEventListener(eventListener!!)
    }

    override fun onCleared() {
        super.onCleared()
        // Important: Remove the listener to prevent memory leaks when the ViewModel is destroyed.
        eventListener?.let { database.removeEventListener(it) }
    }
}

// ---------------------------
// PLACE ORDER SCREEN
// ---------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceOrderScreen(
    currentUserType: String,
    currentUserId: String,
    viewModel: PlaceOrderViewModel = viewModel(),
) {
    val database = FirebaseDatabase.getInstance().reference
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isPreview = LocalInspectionMode.current

    val products by viewModel.products
    val loading by viewModel.loading
    val error by viewModel.error

    // This effect starts listening for real-time data when the screen is composed
    // and stops when it's disposed.
    DisposableEffect(viewModel, isPreview) {
        if (!isPreview) {
            viewModel.startListeningForProducts()
        }
        onDispose {
            // Cleanup logic can go here if needed, but ViewModel handles it in onCleared()
        }
    }

    Scaffold(
        containerColor = HealthShieldBackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Available Products", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HealthShieldBlue)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: $error", color = Color.Red)
                }
            } else if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No products available at the moment.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onPlaceOrder = {
                                coroutineScope.launch {
                                    val orderId = database.child("orders").push().key ?: return@launch
                                    val newOrder = Order(
                                        id = orderId,
                                        productId = product.id,
                                        productName = product.name,
                                        buyerType = currentUserType,
                                        buyerId = currentUserId,
                                        quantity = 1, // Default quantity
                                        supplyChain = product.supplyChain + SupplyChainEvent(
                                            actorType = currentUserType,
                                            actorId = currentUserId,
                                            status = "Ordered"
                                        )
                                    )
                                    database.child("orders").child(orderId).setValue(newOrder)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Order for ${product.name} placed!", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Order failed: ${it.message}", Toast.LENGTH_LONG).show()
                                        }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onPlaceOrder: () -> Unit) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "KE")) }
    var isPlacingOrder by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = HealthShieldCardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = HealthShieldTextDark
            )
            Spacer(Modifier.height(4.dp))
            Text(product.description, fontSize = 14.sp, color = Color.Gray)

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Price: ${currencyFormat.format(product.price)}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = HealthShieldTextDark
                )
                Text(
                    "By: ${product.uploadedBy}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    isPlacingOrder = true
                    onPlaceOrder()
                },
                enabled = !isPlacingOrder, // ✅ Disable button when order is in progress
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HealthShieldAccentGreen)
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Info, contentDescription = "Place Order", tint = Color.White) // AddShoppingCart Icon
                    Spacer(Modifier.width(8.dp))
                    Text("Place Order", color = Color.White, fontWeight = FontWeight.Bold)
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
    // This preview will show the "No products" state as it doesn't run the ViewModel logic.
    PlaceOrderScreen(currentUserType = "Consumer", currentUserId = "user123")
}
