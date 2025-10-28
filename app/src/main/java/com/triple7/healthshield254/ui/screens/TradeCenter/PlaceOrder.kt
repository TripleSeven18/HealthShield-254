package com.triple7.healthshield254.ui.screens.TradeCenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.util.Log
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
import androidx.compose.ui.draw.clip
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
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.triple7.healthshield254.R
import com.triple7.healthshield254.models.MedicineUpload
import com.triple7.healthshield254.models.Order
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.NumberFormat
import java.util.*

/** --- VIEWMODEL --- **/
class PlaceOrderViewModel : ViewModel() {
    internal val _products = mutableStateOf<List<MedicineUpload>>(emptyList())
    val products: State<List<MedicineUpload>> = _products

    internal val _loading = mutableStateOf(true)
    val loading: State<Boolean> = _loading

    internal val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var listener: ValueEventListener? = null
    private val dbRef = FirebaseDatabase.getInstance().getReference("products")

    fun startListeningForProducts(currentUserType: String) {
        viewModelScope.launch {
            _loading.value = true
            val rolesToFetch = when (currentUserType) {
                "Customer" -> listOf("Pharmacist")
                "Pharmacist" -> listOf("Supplier", "Company")
                "Supplier" -> listOf("Company")
                else -> listOf("Pharmacist", "Supplier", "Company")
            }

            listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<MedicineUpload>()
                    for (snap in snapshot.children) {
                        try {
                            val product = snap.getValue(MedicineUpload::class.java)
                            if (product != null && product.uploaderType in rolesToFetch && !product.name.isNullOrBlank()) {
                                list.add(product)
                            }
                        } catch (e: Exception) {
                            Log.e("PlaceOrderViewModel", "Error converting to MedicineUpload", e)
                        }
                    }
                    _products.value = list
                    _loading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    _error.value = error.message
                    _loading.value = false
                }
            }
            dbRef.addValueEventListener(listener!!)
        }
    }

    override fun onCleared() {
        listener?.let { dbRef.removeEventListener(it) }
        super.onCleared()
    }
}

/** --- MAIN SCREEN --- **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceOrderScreen(
    currentUserType: String,
    currentUserId: String,
    viewModel: PlaceOrderViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isPreview = LocalInspectionMode.current

    val user = if (isPreview) null else FirebaseAuth.getInstance().currentUser
    val userName = remember(user) {
        user?.displayName?.takeIf { it.isNotBlank() }
            ?: user?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
            ?: "User"
    }

    val products by viewModel.products
    val loading by viewModel.loading
    val error by viewModel.error

    var showReceiptDialog by remember { mutableStateOf(false) }
    var generatedReceipt by remember { mutableStateOf("") }

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
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

    DisposableEffect(Unit) {
        if (!isPreview) viewModel.startListeningForProducts(currentUserType)
        onDispose { }
    }

    val firebaseRootRef = remember {
        if (!isPreview) FirebaseDatabase.getInstance().reference else null
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            TopAppBar(
                title = { Text("Place Order", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            Image(
                painter = painterResource(id = R.drawable.medicalinsurance),
                contentDescription = "Watermark",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.05f),
                contentScale = ContentScale.Fit
            )
            when {
                loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                products.isEmpty() -> Text("No available products to order.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(products, key = { it.id ?: UUID.randomUUID().toString() }) { product ->
                            ProductCard(
                                product = product,
                                onPlaceOrder = { quantity, paymentMethod, paymentDetail ->
                                    coroutineScope.launch {
                                        val orderId = firebaseRootRef?.child("orders")?.push()?.key ?: return@launch
                                        val priceAsDouble = product.price?.toDoubleOrNull() ?: 0.0
                                        val receiptText = createReceiptText(
                                            orderId = orderId,
                                            buyerName = userName,
                                            product = product,
                                            quantity = quantity,
                                            paymentMethod = paymentMethod,
                                            paymentDetail = paymentDetail,
                                            buyerId = currentUserId,
                                            totalPrice = priceAsDouble * quantity
                                        )
                                        val order = Order(
                                            id = orderId,
                                            productId = product.id ?: "",
                                            productName = product.name,
                                            buyerType = currentUserType,
                                            buyerId = currentUserId,
                                            buyerName = userName,
                                            sellerId = product.uploaderId,
                                            quantity = quantity,
                                            paymentMethod = paymentMethod,
                                            isApproved = true,
                                            receipt = receiptText
                                        )
                                        firebaseRootRef.child("orders").child(orderId).setValue(order)
                                            .addOnSuccessListener {
                                                generatedReceipt = receiptText
                                                showReceiptDialog = true
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(context, "Order failed: ${e.message}", Toast.LENGTH_LONG).show()
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

/** --- PRODUCT CARD --- **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: MedicineUpload,
    onPlaceOrder: (quantity: Int, paymentMethod: String, paymentDetail: String) -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "ke")) }
    val price = product.price?.toDoubleOrNull() ?: 0.0

    var isPlacingOrder by remember { mutableStateOf(false) }
    var quantity by remember { mutableIntStateOf(1) }
    var expanded by remember { mutableStateOf(false) }
    val paymentMethods = listOf("M-Pesa", "Airtel Money", "KCB Bank", "Equity Bank", "Family Bank", "PayPal")
    var selectedPaymentMethod by remember { mutableStateOf(paymentMethods[0]) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    if (showPaymentDialog) {
        PaymentGatewayDialog(
            productName = product.name ?: "Unknown",
            quantity = quantity,
            totalPrice = price * quantity,
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
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- Product Image ---
            if (product.imageUrls.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(product.imageUrls.first()),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // --- Product Info ---
            Text(product.name ?: "Unknown Product", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            product.description?.let { Text("Description: $it", fontSize = 14.sp, color = Color.Gray) }
            product.brand?.let { Text("Brand: $it", fontSize = 14.sp, color = Color.Gray) }
            product.category?.let { Text("Category: $it", fontSize = 14.sp, color = Color.Gray) }
            product.dosage?.let { Text("Dosage: $it", fontSize = 14.sp, color = Color.Gray) }
            product.sideEffects?.let { Text("Side Effects: $it", fontSize = 14.sp, color = Color.Gray) }
            product.warnings?.let { Text("Warnings: $it", fontSize = 14.sp, color = Color.Gray) }
            product.phoneNumber?.let { Text("Contact: $it", fontSize = 14.sp, color = Color.Gray) }

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
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable).fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    )
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
                    "Total: ${currencyFormat.format(price * quantity)}",
                    fontWeight = FontWeight.Bold, fontSize = 18.sp, color = tripleSeven
                )
                product.uploaderName?.let {
                    Text("By: $it", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- Place Order Button ---
            Button(
                onClick = { showPaymentDialog = true },
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

/** --- DIALOGS AND HELPERS --- **/

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
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "ke")) }
    val formattedPrice = currencyFormat.format(totalPrice)
    var paymentDetail by remember { mutableStateOf("") }
    val context = LocalContext.current

    val isPaymentDetailValid = remember(paymentMethod, paymentDetail) {
        when {
            paymentMethod.contains("M-Pesa") || paymentMethod.contains("Airtel Money") -> paymentDetail.isNotBlank() && paymentDetail.length >= 10
            else -> true
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
                    paymentMethod.contains("M-Pesa") || paymentMethod.contains("Airtel Money") -> {
                        Text("Enter your mobile number to receive a payment prompt. You will be asked to enter your PIN to authorize the payment.")
                        OutlinedTextField(
                            value = paymentDetail,
                            onValueChange = { paymentDetail = it },
                            label = { Text("Phone Number (e.g. 07...)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
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

@Composable
fun OrderConfirmationDialog(
    receipt: String,
    onDismiss: () -> Unit,
    onDownloadPdf: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 12.dp) {
            Column(Modifier.padding(16.dp)) {
                Text("Order Confirmed!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = tripleSeven)
                Spacer(Modifier.height(12.dp))
                Text(receipt, fontSize = 14.sp)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = onDownloadPdf, colors = ButtonDefaults.buttonColors(containerColor = tripleSeven)) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                        Spacer(Modifier.width(8.dp))
                        Text("Download PDF", color = Color.White)
                    }
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                        Text("Close", color = Color.White)
                    }
                }
            }
        }
    }
}

fun createReceiptText(
    orderId: String,
    buyerName: String,
    product: MedicineUpload,
    quantity: Int,
    paymentMethod: String,
    paymentDetail: String,
    buyerId: String,
    totalPrice: Double
): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ke"))
    val paymentInfo = if (paymentDetail.isNotBlank()) " $paymentDetail" else ""
    return """
        HealthShield254
        ================================
        RECEIPT
        --------------------------------
        Order ID: $orderId
        Buyer Name: $buyerName
        Product: ${product.name}
        Quantity: $quantity
        Price: ${currencyFormat.format(totalPrice)}
        Payment Method: $paymentMethod$paymentInfo
        Buyer ID: $buyerId
        Status: PAID & APPROVED
        --------------------------------
        Thanks for trading with us!!!
    """.trimIndent()
}

fun createPdfWithWatermark(context: Context, receipt: String): ByteArray {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint().apply {
        textSize = 12f
        color = android.graphics.Color.BLACK
    }

    // Load the watermark image
    val watermarkBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.medicalinsurance)
    val watermarkPaint = Paint().apply {
        alpha = 30 // Set transparency
    }
    // Calculate position to center the watermark
    val centerX = (canvas.width - watermarkBitmap.width) / 2f
    val centerY = (canvas.height - watermarkBitmap.height) / 2f
    canvas.drawBitmap(watermarkBitmap, centerX, centerY, watermarkPaint)

    val lines = receipt.lines()
    var yPosition = 50
    for (line in lines) {
        canvas.drawText(line, 50f, yPosition.toFloat(), paint)
        yPosition += 20
    }

    pdfDocument.finishPage(page)
    val outputStream = ByteArrayOutputStream()
    pdfDocument.writeTo(outputStream)
    pdfDocument.close()
    return outputStream.toByteArray()
}
@Preview(showBackground = true)
@Composable
fun PreviewPlaceOrderScreen() {
    val mockProducts = listOf(
        MedicineUpload(
            id = "1",
            name = "Paracetamol",
            description = "Pain reliever",
            brand = "HealthBrand",
            category = "Painkiller",
            dosage = "500mg",
            sideEffects = "None",
            warnings = "Do not exceed 4 tablets/day",
            price = "50",
            uploaderName = "Pharmacy A",
            imageUrls = listOf()
        ),
        MedicineUpload(
            id = "2",
            name = "Amoxicillin",
            description = "Antibiotic",
            brand = "BioPharma",
            category = "Antibiotic",
            dosage = "250mg",
            sideEffects = "Nausea",
            warnings = "Finish full course",
            price = "120",
            uploaderName = "Pharmacy B",
            imageUrls = listOf()
        )
    )

    // Instead of subclassing, just use a simple holder with State
    val productsState = remember { mutableStateOf(mockProducts) }
    val loadingState = remember { mutableStateOf(false) }
    val errorState = remember { mutableStateOf<String?>(null) }

    val fakeViewModel = object {
        val products: State<List<MedicineUpload>> = productsState
        val loading: State<Boolean> = loadingState
        val error: State<String?> = errorState
    }

    MaterialTheme {
        PlaceOrderScreen(
            currentUserType = "Customer",
            currentUserId = "PreviewUser",
            viewModel = fakeViewModel as PlaceOrderViewModel // we can adjust the screen to accept a simpler interface for preview
        )
    }
}
