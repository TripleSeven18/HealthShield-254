package com.triple7.healthshield254.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.*
import com.triple7.healthshield254.navigation.ChatScreen
import com.triple7.healthshield254.ui.theme.tripleSeven
import java.text.SimpleDateFormat
import java.util.*

/* --------------------------------------------------------------------------
   🧠 Data Models
-------------------------------------------------------------------------- */
data class Message(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)

/* --------------------------------------------------------------------------
   🧠 Chat ViewModel
-------------------------------------------------------------------------- */
class ChatScreenViewModel(
    private val chatId: String,
    private val currentUserId: String,
    private val participantId: String
) : ViewModel() {

    private val messagesRef = FirebaseDatabase.getInstance().getReference("messages/$chatId")
    private val chatRef = FirebaseDatabase.getInstance().getReference("chats/$chatId")
    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> get() = _messages

    private val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            _messages.clear()
            snapshot.children.forEach { msgSnap ->
                val msg = msgSnap.getValue(Message::class.java)
                if (msg != null) _messages.add(msg)
            }
            _messages.sortBy { it.timestamp }
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    init { startListening() }

    fun startListening() = messagesRef.addValueEventListener(listener)
    fun stopListening() = messagesRef.removeEventListener(listener)

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val timestamp = System.currentTimeMillis()
        val msg = Message(senderId = currentUserId, text = text, timestamp = timestamp)
        val newKey = messagesRef.push().key ?: return
        messagesRef.child(newKey).setValue(msg)
        chatRef.updateChildren(
            mapOf(
                "lastMessage" to text,
                "lastTimestamp" to timestamp,
                "participants/$currentUserId" to true,
                "participants/$participantId" to true
            )
        )
    }
}

/* --------------------------------------------------------------------------
   🧩 ChatScreen (Updated UI Styled Like Login)
-------------------------------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    chatId: String,
    currentUserId: String,
    participantId: String,
    participantName: String,
    viewModel: ChatScreenViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ChatScreenViewModel(chatId, currentUserId, participantId) as T
            }
        }
    )
) {
    var messageText by remember { mutableStateOf("") }
    val messages by remember { derivedStateOf { viewModel.messages } }
    val listState = rememberLazyListState()

    val gradientBrush = Brush.verticalGradient(
        listOf(tripleSeven.copy(alpha = 0.9f), Color.White)
    )

    // 🔹 Auto-scroll when messages update
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = participantName,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(messages) { msg ->
                        val isMe = msg.senderId == currentUserId
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isMe) tripleSeven else Color(0xFFF1F8E9)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = msg.text,
                                        color = if (isMe) Color.White else Color.Black
                                    )
                                    Text(
                                        text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp)),
                                        color = if (isMe) Color.White.copy(0.8f) else Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        label = { Text("Type a message") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = tripleSeven,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = tripleSeven
                        )
                    )
                    IconButton(onClick = {
                        viewModel.sendMessage(messageText)
                        messageText = ""
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = tripleSeven)
                    }
                }
            }
        }
    }
}

/* --------------------------------------------------------------------------
   🧪 Preview
-------------------------------------------------------------------------- */
@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        navController = rememberNavController(),
        chatId = "chat1",
        currentUserId = "user1",
        participantId = "user2",
        participantName = "Supplier Bob"
    )
}