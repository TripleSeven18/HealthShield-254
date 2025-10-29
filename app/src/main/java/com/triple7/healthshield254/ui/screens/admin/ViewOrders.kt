package com.triple7.healthshield254.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.*
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.launch

// ---------------------------
// 🧩 Data Model (matches Firebase exactly)
// ---------------------------
data class Order(
    val id: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val buyerType: String = "",
    val paymentMethod: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val sellerId: String = "",
    val timestamp: Long = 0L,
    val approved: Boolean = false,
    val receipt: String = ""
) {
    val orderId: String get() = id
}

// ---------------------------
// 🧠 ViewModel
// ---------------------------
class ViewOrdersViewModel : ViewModel() {
    private val _allOrders = mutableStateOf<List<Order>>(emptyList())
    val allOrders: State<List<Order>> = _allOrders

    private val _loading = mutableStateOf(true)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var listener: ValueEventListener? = null
    private var dbRef: Query? = null

    fun startListeningForAdminOrders(adminId: String) {
        if (adminId.isBlank()) {
            _error.value = "Admin ID missing."
            _loading.value = false
            return
        }

        val db = FirebaseDatabase.getInstance().getReference("orders")
        dbRef = db.orderByChild("sellerId").equalTo(adminId)

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull {
                    it.getValue(Order::class.java)
                }.sortedByDescending { it.timestamp }

                _allOrders.value = list
                _loading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _error.value = error.message
                _loading.value = false
            }
        }

        dbRef?.addValueEventListener(listener!!)
    }

    override fun onCleared() {
        listener?.let { dbRef?.removeEventListener(it) }
        super.onCleared()
    }

    fun updateOrderApproval(orderId: String, approved: Boolean) {
        val db = FirebaseDatabase.getInstance().getReference("orders").child(orderId)
        db.child("approved").setValue(approved)
    }
}

// ---------------------------
// 🎨 Composable Screen
// ---------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrdersScreen(
    navController: NavController,
    adminId: String,
    viewModel: ViewOrdersViewModel = viewModel()
) {
    val isPreview = LocalInspectionMode.current
    val allOrders by viewModel.allOrders
    val loading by viewModel.loading
    val error by viewModel.error
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Pending", "Approved")

    DisposableEffect(viewModel, isPreview, adminId) {
        if (!isPreview) viewModel.startListeningForAdminOrders(adminId)
        onDispose { }
    }

    val orders = when (selectedTab) {
        0 -> allOrders.filter { !it.approved } // pending
        1 -> allOrders.filter { it.approved }  // approved
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders Management", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 🔹 Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = tripleSeven,
                contentColor = Color.White
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) Color.White else Color.LightGray,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when {
                loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = tripleSeven)
                }

                error != null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(text = error ?: "Error loading orders", color = Color.Red)
                }

                orders.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No ${tabTitles[selectedTab].lowercase()} orders found.", color = Color.Gray)
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8FAFC)),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(orders) { order ->
                        OrderCard(
                            order = order,
                            isApproved = selectedTab == 1,
                            onApprove = {
                                coroutineScope.launch {
                                    viewModel.updateOrderApproval(order.orderId, true)
                                }
                            },
                            onReject = {
                                coroutineScope.launch {
                                    viewModel.updateOrderApproval(order.orderId, false)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------
// 💳 Order Card UI
// ---------------------------
@Composable
fun OrderCard(
    order: Order,
    isApproved: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(order.productName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = tripleSeven)
            Spacer(Modifier.height(4.dp))
            Text("Buyer: ${order.buyerName}", fontSize = 15.sp, color = Color.DarkGray)
            Text("Payment: ${order.paymentMethod}", fontSize = 15.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text("Quantity: ${order.quantity}", fontSize = 15.sp)
            Spacer(Modifier.height(10.dp))

            if (!isApproved) {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onReject,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) { Text("Reject", color = Color.White) }

                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) { Text("Approve", color = Color.White) }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "✅ Approved",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ---------------------------
// 🧪 Preview
// ---------------------------
@Preview(showBackground = true)
@Composable
fun ViewOrdersPreview() {
    ViewOrdersScreen(navController = rememberNavController(), adminId = "previewUser")
}
