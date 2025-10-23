package com.triple7.healthshield254.ui.screens.TradeCenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.triple7.healthshield254.R
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.NumberFormat
import java.util.*

// --- DATA MODELS ---
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val uploadedBy: String = "HealthShield Pharmacy"
)

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
    val buyerName: String = "", // Added buyer name
    val quantity: Int = 1,
    val paymentMethod: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isApproved: Boolean = false,
    val receipt: String = ""
)

// --- VIEWMODEL FOR DATA FETCHING ---
open class PlaceOrderViewModel : ViewModel() {
    private val _products = mutableStateOf<List<Product>>(emptyList())
    val products: State<List<Product>> = _products

    private val _loading = mutableStateOf(true)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var databaseRef: DatabaseReference? = null
    private var eventListener: ValueEventListener? = null

    fun startListeningForProducts() {
        viewModelScope.launch {
            _loading.value = true

            if (databaseRef == null) {
                databaseRef = FirebaseDatabase.getInstance().getReference("medicines")
            }
            val database = databaseRef ?: run {
                _error.value = "Database not available"
                _loading.value = false
                return@launch
            }

            eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val productList = snapshot.children.mapNotNull { dataSnapshot ->
                        val medicine = dataSnapshot.getValue(MedicineFromDB::class.java)
                        medicine?.let {
                            Product(
                                id = dataSnapshot.key ?: "",
                                name = it.name,
                                description = it.dosage,
                                price = 150.00 // placeholder
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
        eventListener?.let { databaseRef?.removeEventListener(it) }
    }
}

// --- PLACE ORDER SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceOrderScreen(
    currentUserType: String = "Distributor",
    currentUserId: String = "user123",
    viewModel: PlaceOrderViewModel = viewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isPreview = LocalInspectionMode.current

    val user = if (isPreview) null else FirebaseAuth.getInstance().currentUser
    val userName = remember(user) {
        user?.displayName?.takeIf { it.isNotBlank() }
            ?: user?.email?.split('@')?.get(0)?.replaceFirstChar { it.uppercase() }
            ?: "User"
    }

    val products by viewModel.products
    val loading by viewModel.loading
    val error by viewModel.error

    var showReceiptDialog by remember { mutableStateOf(false) }
    var generatedReceipt by remember { mutableStateOf("") }

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    try {
                        val pdfBytes = createPdfWithWatermark(context, generatedReceipt)
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            outputStream.write(pdfBytes)
                        }
                        Toast.makeText(context, "Receipt saved successfully!", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        Toast.makeText(context, "Failed to save receipt: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "Saving cancelled.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    if (showReceiptDialog) {
        OrderConfirmationDialog(
            receipt = generatedReceipt,
            onDismiss = { showReceiptDialog = false },
            onDownloadPdf = {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_TITLE, "HealthShield_Receipt_${System.currentTimeMillis()}.pdf")
                }
                pdfLauncher.launch(intent)
            }
        )
    }

    DisposableEffect(viewModel, isPreview) {
        if (!isPreview) {
            viewModel.startListeningForProducts()
        }
        onDispose { /* ViewModel handles cleanup */ }
    }

    val firebaseRootRef: DatabaseReference? = remember {
        if (!isPreview) FirebaseDatabase.getInstance().reference else null
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            TopAppBar(
                title = { Text("Available Products", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                products.isEmpty() -> Text("No products available.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(products, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                onPlaceOrder = { quantity, paymentMethod, paymentDetail ->
                                    if (firebaseRootRef == null) {
                                        Toast.makeText(context, "Preview: Order for ${product.name} simulated", Toast.LENGTH_SHORT).show()
                                        return@ProductCard
                                    }

                                    coroutineScope.launch {
                                        val orderId = firebaseRootRef.child("orders").push().key ?: return@launch

                                        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "KE"))
                                        val paymentInfo = if (paymentDetail.isNotBlank()) " from $paymentDetail" else ""
                                        val receiptContent = """
                                            HealthShield254
                                            ================================
                                            RECEIPT
                                            --------------------------------
                                            Order ID: $orderId
                                            Buyer Name: $userName
                                            Product: ${product.name}
                                            Quantity: $quantity
                                            Price: ${currencyFormat.format(product.price * quantity)}
                                            Payment Method: $paymentMethod$paymentInfo
                                            Buyer ID: $currentUserId
                                            Status: PAID & APPROVED
                                            --------------------------------
                                            Thanks for trading with us!!!
                                        """.trimIndent()

                                        val newOrder = Order(
                                            id = orderId,
                                            productId = product.id,
                                            productName = product.name,
                                            buyerType = currentUserType,
                                            buyerId = currentUserId,
                                            buyerName = userName,
                                            quantity = quantity,
                                            paymentMethod = "$paymentMethod$paymentInfo",
                                            isApproved = true,
                                            receipt = receiptContent
                                        )

                                        firebaseRootRef.child("orders").child(orderId).setValue(newOrder)
                                            .addOnSuccessListener {
                                                generatedReceipt = receiptContent
                                                showReceiptDialog = true
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(product: Product, onPlaceOrder: (Int, String, String) -> Unit) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "KE")) }
    var isPlacingOrder by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) }
    var expanded by remember { mutableStateOf(false) }
    val paymentMethods = listOf(
        // Mobile Money
        "M-Pesa", "Airtel Money",
        // Kenyan Banks
        "Equity Bank", "KCB Bank", "Co-operative Bank", "Family Bank", "Absa Bank", "Stanbic Bank", "Standard Chartered Bank", "I&M Bank", "DTB Bank",
        // International Methods & Banks
        "PayPal", "Zelle", "Stripe", "Credit/Debit Card", "Bank of America", "HSBC", "Citi", "JPMorgan Chase", "Barclays", "Deutsche Bank"
    )
    var selectedPaymentMethod by remember { mutableStateOf(paymentMethods[0]) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    if (showPaymentDialog) {
        PaymentGatewayDialog(
            productName = product.name,
            quantity = quantity,
            totalPrice = product.price * quantity,
            paymentMethod = selectedPaymentMethod,
            onConfirmPayment = { paymentDetail ->
                isPlacingOrder = true
                onPlaceOrder(quantity, selectedPaymentMethod, paymentDetail)
                showPaymentDialog = false
            },
            onDismiss = {
                showPaymentDialog = false
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
            Text(product.description, fontSize = 14.sp, color = Color.Gray)

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // --- Quantity Selector ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quantity:", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (quantity > 1) quantity-- }) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
                    }
                    Text(text = quantity.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = { quantity++ }) {
                        Icon(Icons.Default.Add, contentDescription = "Increase quantity")
                    }
                }
            }

            // --- Payment Method Selector ---
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                TextField(
                    value = selectedPaymentMethod,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payment Method") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent, focusedContainerColor = Color.Transparent)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    paymentMethods.forEach { method ->
                        DropdownMenuItem(text = { Text(method) }, onClick = {
                            selectedPaymentMethod = method
                            expanded = false
                        })
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- Price & Uploader ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total: ${currencyFormat.format(product.price * quantity)}",
                    fontWeight = FontWeight.Bold, fontSize = 18.sp, color = tripleSeven
                )
                Text("By: ${product.uploadedBy}", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(16.dp))

            // --- Place Order Button ---
            Button(
                onClick = {
                    showPaymentDialog = true
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentGatewayDialog(
    productName: String,
    quantity: Int,
    totalPrice: Double,
    paymentMethod: String,
    onConfirmPayment: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "KE")) }
    val formattedPrice = currencyFormat.format(totalPrice)
    var paymentDetail by remember { mutableStateOf("") }
    val context = LocalContext.current

    val isPaymentDetailValid = remember(paymentMethod, paymentDetail) {
        when {
            paymentMethod.contains("M-Pesa") -> paymentDetail.isNotBlank() && paymentDetail.length >= 10
            else -> true // No input needed for other methods in this simulation
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Payment via $paymentMethod") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Please confirm the details below to proceed with your payment to HealthShield254.")
                Spacer(Modifier.height(8.dp))
                Text("Product: $productName", fontWeight = FontWeight.SemiBold)
                Text("Quantity: $quantity", fontWeight = FontWeight.SemiBold)
                Text("Total Amount: $formattedPrice", fontWeight = FontWeight.Bold, color = tripleSeven)
                Spacer(Modifier.height(16.dp))

                when {
                    paymentMethod.contains("M-Pesa") -> {
                        Text("Enter your M-Pesa number to receive a payment prompt.")
                        OutlinedTextField(
                            value = paymentDetail,
                            onValueChange = { paymentDetail = it },
                            label = { Text("M-Pesa Number (e.g. 07...)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("An STK push will be sent to this number. You will be asked to enter your PIN to authorize the payment.")
                    }
                    paymentMethod.contains("Bank") -> {
                        Text("You will be redirected to your bank's portal to complete the payment of $formattedPrice.")
                    }
                    else -> {
                        Text("You will be redirected to $paymentMethod to complete the payment of $formattedPrice.")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    Toast.makeText(context, "Initiating payment... Please check your phone for STK push.", Toast.LENGTH_LONG).show()
                    onConfirmPayment(paymentDetail)
                },
                enabled = isPaymentDetailValid,
                colors = ButtonDefaults.buttonColors(containerColor = tripleSeven)
            ) {
                Text("Confirm & Pay")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Cancel")
            }
        }
    )
}


// --- DIALOG & PREVIEW ---

@Composable
fun OrderConfirmationDialog(
    receipt: String,
    onDismiss: () -> Unit,
    onDownloadPdf: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Watermark
                Image(
                    painter = painterResource(id = R.drawable.medicalinsurance),
                    contentDescription = null, // Decorative
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .alpha(0.1f), // Make it transparent
                    contentScale = ContentScale.Fit
                )

                // Content
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Order Confirmed & Approved", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        receipt,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                            Text("Done")
                        }
                        Button(
                            onClick = onDownloadPdf,
                            colors = ButtonDefaults.buttonColors(containerColor = tripleSeven),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Download PDF", modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Save PDF")
                        }
                    }
                }
            }
        }
    }
}

fun createPdfWithWatermark(context: Context, receiptText: String): ByteArray {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 page size
    val page = document.startPage(pageInfo)
    val canvas = page.canvas

    // Draw Watermark
    val watermarkBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.medicalinsurance)
    val watermarkPaint = Paint().apply { alpha = 30 } // ~12% opacity
    val canvasWidth = canvas.width
    val canvasHeight = canvas.height
    val bitmapWidth = watermarkBitmap.width
    val bitmapHeight = watermarkBitmap.height
    val left = (canvasWidth - bitmapWidth) / 2f
    val top = (canvasHeight - bitmapHeight) / 2f
    canvas.drawBitmap(watermarkBitmap, left, top, watermarkPaint)

    // Draw Text
    val textPaint = Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 12f
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.NORMAL)
    }

    var y = 40f
    receiptText.split('\n').forEach { line ->
        canvas.drawText(line, 10f, y, textPaint)
        y += textPaint.fontSpacing
    }

    document.finishPage(page)
    val outputStream = ByteArrayOutputStream()
    document.writeTo(outputStream)
    document.close()
    return outputStream.toByteArray()
}


@Preview(showBackground = true)
@Composable
fun PreviewPlaceOrderScreen() {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            ProductCard(
                product = Product(
                    id = "sample-1",
                    name = "Paracetamol 500mg",
                    description = "Pain & fever relief - 20 tablets",
                    price = 120.0,
                    uploadedBy = "HealthShield Pharmacy"
                ),
                onPlaceOrder = { _, _, _ -> /* no-op for preview */ }
            )
        }
    }
}
