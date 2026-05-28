package com.triple7.healthshield254.ui.screens.bot

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.triple7.healthshield254.ui.theme.WarmCream
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SUPPORT_NUMBER = "+254743887226"

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotEnquiryScreen(navController: NavController) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val coroutineScope = rememberCoroutineScope()

    // Initial bot greeting
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(ChatMessage("Hello! I am your HealthShield Assistant. How can I help you today regarding medicine safety or orders?", false))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HealthShield Bot") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // WhatsApp Support
                    IconButton(onClick = {
                        val waUri = Uri.parse("https://wa.me/${SUPPORT_NUMBER.removePrefix("+")}")
                        val intent = Intent(Intent.ACTION_VIEW, waUri)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat Support")
                    }
                    // Call Support
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$SUPPORT_NUMBER")
                        }
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Call Support")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = tripleSeven,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = WarmCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Urgent Support Engagement Banner
            UrgentSupportBanner()

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
            }

            // Input Area
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask something...") },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val userText = inputText
                                messages.add(ChatMessage(userText, true))
                                inputText = ""
                                coroutineScope.launch {
                                    delay(1000)
                                    val response = getBotResponse(userText)
                                    messages.add(ChatMessage(response, false))
                                }
                            }
                        },
                        containerColor = tripleSeven,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun UrgentSupportBanner() {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$SUPPORT_NUMBER")
                }
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)), // Urgent Red
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = "Urgent",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "URGENT: Direct Support Engagement",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Click to call $SUPPORT_NUMBER for immediate assistance",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (message.isUser) tripleSeven else Color.White
    val textColor = if (message.isUser) Color.White else Color.Black

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start) {
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isUser) 16.dp else 0.dp,
                    bottomEnd = if (message.isUser) 0.dp else 16.dp
                ),
                tonalElevation = 2.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    color = textColor,
                    fontSize = 15.sp
                )
            }
            if (!message.isUser) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp, top = 2.dp)) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Assistant", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}

fun getBotResponse(input: String): String {
    val lowerInput = input.lowercase()
    return when {
        lowerInput.contains("hello") || lowerInput.contains("hi") -> 
            "Hello! I am HealthShield Bot. I can help you verify medicines, track orders, or report counterfeit drugs."
        lowerInput.contains("verify") || lowerInput.contains("authentic") || lowerInput.contains("fake") -> 
            "To verify a medicine, use the 'Scan Medicine' feature on the home screen. Scan the barcode to check against our database."
        lowerInput.contains("order") || lowerInput.contains("buy") -> 
            "You can place an order by selecting 'Place Order' on the dashboard and choosing from verified suppliers."
        lowerInput.contains("report") || lowerInput.contains("counterfeit") -> 
            "To report suspicious medicine, go to 'Report Counterfeit' and fill in the distributor details."
        lowerInput.contains("pharmacist") || lowerInput.contains("doctor") -> 
            "For professional medical advice, please use the 'Consultation' section to connect with a licensed expert."
        lowerInput.contains("delivery") || lowerInput.contains("time") -> 
            "Delivery typically takes 1-3 business days depending on your location and the supplier's stock."
        lowerInput.contains("payment") || lowerInput.contains("mpesa") -> 
            "We support M-Pesa, Airtel Money, and Bank transfers. All payments are secured via our gateway."
        else -> "I'm sorry, I don't have information on that specifically. For more precise assistance, please call or chat with our human support team directly at $SUPPORT_NUMBER."
    }
}
