package com.triple7.healthshield254.ui.screens.TradeCenter

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.database.*
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*


// --- DATA MODELS ---
// This class represents a product that can be ordered.
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0, // Added a price field
    val uploadedBy: String = "HealthShield Pharmacy"
)

// This class is used to deserialize data from your 'medicines' node.
data class MedicineFromDB(
    val name: String = "",
    val dosage: String = ""
)

data class Order(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val buyerType: String = "",
    val buyerId: String = "",
    val quantity: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)

// --- VIEWMODEL FOR DATA FETCHING ---
class PlaceOrderViewModel : ViewModel() {
    private val _products = mutableStateOf<List<Product>>(emptyList())
    val products: State<List<Product>> = _products

    private val _loading = mutableStateOf(true)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // Corrected to point to 'medicines' node
    private val database = FirebaseDatabase.getInstance().getReference("medicines")
    private var eventListener: ValueEventListener? = null

    fun startListeningForProducts() {
        viewModelScope.launch {
            _loading.value = true
            eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val productList = snapshot.children.mapNotNull { dataSnapshot ->
                        val medicine = dataSnapshot.getValue(MedicineFromDB::class.java)
                        medicine?.let {
                            Product(
                                id = dataSnapshot.key ?: "",
                                name = it.name,
                                description = it.dosage, // Use dosage as description
                                price = 150.00 // Placeholder price, since it's not in your DB
                            )
                        }
                    }
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
    }

    override fun onCleared() {
        super.onCleared()
        eventListener?.let { database.removeEventListener(it) }
    }
}

// --- PLACE ORDER SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceOrderScreen(
    currentUserType: String = "Distributor", // Default values for preview
    currentUserId: String = "user123",
    viewModel: PlaceOrderViewModel = viewModel(),
) {
    val database = FirebaseDatabase.getInstance().reference
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isPreview = LocalInspectionMode.current

    val products by viewModel.products
    val loading by viewModel.loading
    val error by viewModel.error

    DisposableEffect(viewModel, isPreview) {
        if (!isPreview) {
            viewModel.startListeningForProducts()
        }
        onDispose { /* ViewModel now handles cleanup */ }
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(text = "Error: $error", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
                products.isEmpty() -> {
                    Text("No products available at the moment.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
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
                                            buyerId = currentUserId
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
}

// --- PRODUCT CARD ---
@Composable
fun ProductCard(product: Product, onPlaceOrder: () -> Unit) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "KE")) }
    var isPlacingOrder by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = tripleSeven)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = HealthShieldTextDark)
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
                    fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = HealthShieldTextDark
                )
                Text("By: ${product.uploadedBy}", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    isPlacingOrder = true
                    onPlaceOrder()
                    // A real app might reset isPlacingOrder in a callback from the ViewModel
                },
                enabled = !isPlacingOrder,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = tripleSeven)
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = "Place Order", tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Place Order", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun PreviewPlaceOrderScreen() {
    MaterialTheme {
        PlaceOrderScreen()
    }
}
