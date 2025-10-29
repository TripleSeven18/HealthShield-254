package com.triple7.healthshield254.ui.screens.admin

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.launch

// ---------------------------
// 🧩 Data Model
// ---------------------------
data class Order(
    val id: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val buyerPhone: String = "",
    val buyerType: String = "",
    val paymentMethod: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val sellerId: String = "",
    val timestamp: Long = 0L,
    val approved: Boolean = false,
    val receipt: String = ""
)

// ---------------------------
// 🧠 ViewModel
// ---------------------------
open class ViewOrdersViewModel : ViewModel() {
    protected val _allOrders = mutableStateOf<List<Order>>(emptyList())
    open val allOrders: State<List<Order>> get() = _allOrders

    protected val _loading = mutableStateOf(true)
    open val loading: State<Boolean> get() = _loading

    protected val _error = mutableStateOf<String?>(null)
    open val error: State<String?> get() = _error

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

    fun updateOrderApproval(orderId: String, approved: Boolean) {
        val db = FirebaseDatabase.getInstance().getReference("orders").child(orderId)
        db.child("approved").setValue(approved)
    }

    override fun onCleared() {
        listener?.let { dbRef?.removeEventListener(it) }
        super.onCleared()
    }
}

// ---------------------------
// 🎨 Composable Screen
// ---------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrdersScreen(
    navController: NavController,
    adminId: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    viewModel: ViewOrdersViewModel = viewModel()
) {
    val isPreview = LocalInspectionMode.current
    val allOrders by viewModel.allOrders
    val loading by viewModel.loading
    val error by viewModel.error
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(viewModel, isPreview, adminId) {
        if (!isPreview) viewModel.startListeningForAdminOrders(adminId)
        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders Management", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
        ) {
            when {
                loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = tripleSeven)
                }

                error != null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(text = error ?: "Error loading orders", color = Color.Red)
                }

                allOrders.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No orders found.", color = Color.Gray)
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allOrders) { order ->
                        OrderCard(
                            order = order,
                            onApprove = {
                                coroutineScope.launch {
                                    viewModel.updateOrderApproval(order.id, true)
                                }
                            },
                            onReject = {
                                coroutineScope.launch {
                                    viewModel.updateOrderApproval(order.id, false)
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
// 🧾 Order Card
// ---------------------------
@Composable
fun OrderCard(
    order: Order,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.productName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = tripleSeven
                )

                // ✅ Status badge
                Box(
                    modifier = Modifier
                        .background(
                            color = if (order.approved) Color(0xFF4CAF50) else Color(0xFFFFC107),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (order.approved) "Approved" else "Pending",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Buyer: ${order.buyerName}", fontSize = 15.sp, color = Color.DarkGray)

            if (order.buyerPhone.isNotEmpty()) {
                Text(
                    text = "Contact: ${order.buyerPhone}",
                    fontSize = 15.sp,
                    color = Color(0xFF0277BD),
                    fontWeight = FontWeight.Medium
                )
            }

            Text("Payment: ${order.paymentMethod}", fontSize = 15.sp, color = Color.Gray)
            Text("Quantity: ${order.quantity}", fontSize = 15.sp)

            Spacer(Modifier.height(10.dp))

            if (!order.receipt.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .background(
                            if (expanded) Color(0xFFE3F2FD) else Color(0xFFF1F1F1),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "Receipt",
                        tint = tripleSeven,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (expanded) "Hide Receipt" else "View Receipt",
                        color = tripleSeven,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(Color(0xFFF9F9F9), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = order.receipt.trim(),
                            fontSize = 13.sp,
                            color = Color(0xFF37474F),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            if (!order.approved) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
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
    val navController = rememberNavController()

    val sampleOrders = listOf(
        Order(
            id = "1",
            buyerName = "John Doe",
            buyerPhone = "0712345678",
            productName = "Vitamin C Tablets",
            quantity = 2,
            paymentMethod = "M-Pesa",
            approved = false,
            receipt = "Order ID: 1\nProduct: Vitamin C Tablets\nBuyer: John Doe\nPayment: M-Pesa\nStatus: Pending Approval"
        ),
        Order(
            id = "2",
            buyerName = "Jane Smith",
            buyerPhone = "0798765432",
            productName = "Pain Relief Cream",
            quantity = 1,
            paymentMethod = "M-Pesa",
            approved = true,
            receipt = "Order ID: 2\nProduct: Pain Relief Cream\nBuyer: Jane Smith\nPayment: M-Pesa\nStatus: Approved"
        )
    )

    // ✅ Mock ViewModel (no private property access)
    val previewVM = object : ViewOrdersViewModel() {
        @SuppressLint("UnrememberedMutableState")
        private val fakeAllOrders = mutableStateOf(sampleOrders)
        @SuppressLint("UnrememberedMutableState")
        private val fakeLoading = mutableStateOf(false)
        @SuppressLint("UnrememberedMutableState")
        private val fakeError = mutableStateOf<String?>(null)

        override val allOrders: State<List<Order>> get() = fakeAllOrders
        override val loading: State<Boolean> get() = fakeLoading
        override val error: State<String?> get() = fakeError
    }

    ViewOrdersScreen(navController = navController, adminId = "previewUser", viewModel = previewVM)
}