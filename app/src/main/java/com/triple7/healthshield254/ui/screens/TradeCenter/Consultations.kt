package com.triple7.healthshield254.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
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
import java.text.SimpleDateFormat
import java.util.*

/* --------------------------------------------------------------------------
   📝 Data Models
-------------------------------------------------------------------------- */
data class ChatRoom(
    val chatId: String = "",
    val lastMessage: String = "",
    val lastTimestamp: Long = 0,
    val participantName: String = ""
)

data class Message(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)

/* --------------------------------------------------------------------------
   🧠 ChatBoard ViewModel
-------------------------------------------------------------------------- */
class ChatBoardViewModel(private val currentUserId: String) : ViewModel() {
    private val db = FirebaseDatabase.getInstance().getReference("chats")
    private val _chatRooms = mutableStateListOf<ChatRoom>()
    val chatRooms: List<ChatRoom> get() = _chatRooms

    private val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            _chatRooms.clear()
            snapshot.children.forEach { chatSnapshot ->
                val participants = chatSnapshot.child("participants").value as? Map<*, *> ?: return@forEach
                if (!participants.containsKey(currentUserId)) return@forEach

                val lastMessage = chatSnapshot.child("lastMessage").getValue(String::class.java) ?: ""
                val lastTimestamp = chatSnapshot.child("lastTimestamp").getValue(Long::class.java) ?: 0L
                val otherId = participants.keys.firstOrNull { it != currentUserId } as? String ?: ""
                var otherName = "Unknown"

                FirebaseDatabase.getInstance().getReference("users/$otherId/name")
                    .get().addOnSuccessListener { snapshotName ->
                        otherName = snapshotName.getValue(String::class.java) ?: "Unknown"
                        val existingIndex = _chatRooms.indexOfFirst { it.chatId == chatSnapshot.key }
                        val room = ChatRoom(
                            chatId = chatSnapshot.key ?: "",
                            lastMessage = lastMessage,
                            lastTimestamp = lastTimestamp,
                            participantName = otherName
                        )
                        if (existingIndex >= 0) _chatRooms[existingIndex] = room else _chatRooms.add(room)
                        _chatRooms.sortByDescending { it.lastTimestamp }
                    }
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    init { startListening() }
    fun startListening() = db.addValueEventListener(listener)
    fun stopListening() = db.removeEventListener(listener)
    override fun onCleared() { super.onCleared(); stopListening() }
}

/* --------------------------------------------------------------------------
   🧠 ChatScreen ViewModel
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
    override fun onCleared() { super.onCleared(); stopListening() }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val timestamp = System.currentTimeMillis()
        val msg = Message(senderId = currentUserId, text = text, timestamp = timestamp)
        val newKey = messagesRef.push().key ?: return
        messagesRef.child(newKey).setValue(msg)
        chatRef.updateChildren(mapOf(
            "lastMessage" to text,
            "lastTimestamp" to timestamp,
            "participants/$currentUserId" to true,
            "participants/$participantId" to true
        ))
    }
}

/* --------------------------------------------------------------------------
   🧩 ChatBoard Screen
-------------------------------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBoardScreen(
    navController: NavController,
    currentUserId: String,
    viewModel: ChatBoardViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ChatBoardViewModel(currentUserId) as T
        }
    })
) {
    val chatRooms by remember { mutableStateOf(viewModel.chatRooms) }
    val isPreview = LocalInspectionMode.current

    DisposableEffect(key1 = isPreview, key2 = viewModel) {
        if (!isPreview) viewModel.startListening()
        onDispose { if (!isPreview) viewModel.stopListening() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatRooms) { room ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("chat/${room.chatId}/${currentUserId}/${room.participantName}")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(room.participantName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(room.lastMessage, fontSize = 14.sp, color = Color.Gray)
                        }
                        Text(
                            if (room.lastTimestamp > 0) SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(room.lastTimestamp)) else "",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

/* --------------------------------------------------------------------------
   🧩 Chat Screen with Auto-Scroll
-------------------------------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    chatId: String,
    currentUserId: String,
    participantId: String,
    participantName: String,
    viewModel: ChatScreenViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ChatScreenViewModel(chatId, currentUserId, participantId) as T
        }
    })
) {
    var messageText by remember { mutableStateOf("") }
    val messages by remember { derivedStateOf { viewModel.messages } }
    val isPreview = LocalInspectionMode.current
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    DisposableEffect(key1 = isPreview, key2 = viewModel) {
        if (!isPreview) viewModel.startListening()
        onDispose { if (!isPreview) viewModel.stopListening() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(participantName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF1F8E9), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
                IconButton(onClick = {
                    viewModel.sendMessage(messageText)
                    messageText = ""
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = tripleSeven)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(messages) { msg ->
                val isCurrentUser = msg.senderId == currentUserId
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrentUser) tripleSeven else Color(0xFFF1F8E9)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            msg.text,
                            modifier = Modifier.padding(8.dp),
                            color = if (isCurrentUser) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}

/* --------------------------------------------------------------------------
   🧪 Previews
-------------------------------------------------------------------------- */
@Preview(showBackground = true)
@Composable
fun ChatBoardPreview() {
    ChatBoardScreen(navController = rememberNavController(), currentUserId = "user1")
}

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