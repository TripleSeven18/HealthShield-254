package com.triple7.healthshield254.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.triple7.healthshield254.models.Order
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** --- VIEW ORDERS VIEWMODEL --- **/
class ViewOrdersViewModel : ViewModel() {
    private val _orders = mutableStateOf<List<Order>>(emptyList())
    val orders: State<List<Order>> = _orders

    private val _loading = mutableStateOf(true)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var orderListener: ValueEventListener? = null
    private var dbRef: Query? = null

    fun startListeningForOrders(sellerId: String) {
        if (sellerId.isBlank()) {
            _loading.value = false
            _error.value = "Current user ID is not available."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            dbRef = FirebaseDatabase.getInstance().getReference("orders")
                .orderByChild("sellerId")
                .equalTo(sellerId)

            orderListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orderList = snapshot.children.mapNotNull { data ->
                        data.getValue(Order::class.java)
                    }.sortedByDescending { it.timestamp }
                    _orders.value = orderList
                    _loading.value = false
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    _error.value = databaseError.message
                    _loading.value = false
                }
            }
            dbRef?.addValueEventListener(orderListener!!)
        }
    }

    override fun onCleared() {
        orderListener?.let { dbRef?.removeEventListener(it) }
        super.onCleared()
    }
}

/** --- VIEW ORDERS SCREEN --- **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrdersScreen(
    navController: NavController,
    currentUserId: String,
    viewModel: ViewOrdersViewModel = viewModel()
) {
    val isPreview = LocalInspectionMode.current

    val orders by viewModel.orders
    val loading by viewModel.loading
    val error by viewModel.error

    DisposableEffect(viewModel, isPreview, currentUserId) {
        if (!isPreview) {
            viewModel.startListeningForOrders(currentUserId)
        }
        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Incoming Orders", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven, navigationIconContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        ViewOrdersContent(
            paddingValues = paddingValues,
            loading = loading,
            error = error,
            orders = orders
        )
    }
}

@Composable
fun ViewOrdersContent(
    paddingValues: PaddingValues,
    loading: Boolean,
    error: String?,
    orders: List<Order>
) {
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        when {
            loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.align(Alignment.Center))
            orders.isEmpty() -> Text("You have no orders yet.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(orders, key = { it.id }) { order ->
                        OrderCard(order = order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    val formattedDate = remember(order.timestamp) {
        val sdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(order.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(order.productName ?: "Unnamed Product", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Text("Order from: ${order.buyerName}", fontSize = 14.sp, color = Color.Gray)
            Text("Quantity: ${order.quantity}", fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text("Ordered on: $formattedDate", fontSize = 12.sp, color = Color.LightGray)

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Button(
                onClick = { /* TODO: Implement dispatch logic */ },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = tripleSeven)
            ) {
                Text("Dispatch Order")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewViewOrdersScreen() {
    // Passing fake data for preview
    val sampleOrders = listOf(
        Order(
            id = "1",
            productId = "prod1",
            productName = "Paracetamol",
            buyerType = "Customer",
            buyerId = "user1",
            buyerName = "John Doe",
            sellerId = "seller1",
            quantity = 2,
            paymentMethod = "MPesa",
            timestamp = System.currentTimeMillis(),
            isApproved = true,
            receipt = "Sample Receipt"
        ),
        Order(
            id = "2",
            productId = "prod2",
            productName = "Amoxicillin",
            buyerType = "Customer",
            buyerId = "user2",
            buyerName = "Jane Smith",
            sellerId = "seller2",
            quantity = 1,
            paymentMethod = "Cash",
            timestamp = System.currentTimeMillis(),
            isApproved = true,
            receipt = "Sample Receipt"
        )
    )

    // Use ViewOrdersContent directly for previewing UI without Firebase
    ViewOrdersContent(
        paddingValues = PaddingValues(0.dp),
        loading = false,
        error = null,
        orders = sampleOrders
    )
}
