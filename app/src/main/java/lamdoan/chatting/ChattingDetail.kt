package lamdoan.chatting

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(navController: NavController, userId: String) {
    val database = FirebaseDatabase.getInstance().reference
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val currentUserId = sharedPreferences.getString("currentUserId", "") ?: ""

    var userName by remember { mutableStateOf("Người dùng") }
    var room by remember { mutableStateOf<Room?>(null) }
    var messageList by remember { mutableStateOf(listOf<MessageItem>()) }
    var newMessageText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val listState = rememberLazyListState() // LazyListState để quản lý trạng thái cuộn

    // Load tên người dùng và danh sách tin nhắn
    LaunchedEffect(Unit) {
        // Lấy tên người dùng
        database.child("users").child(userId).child("name").get()
            .addOnSuccessListener { snapshot ->
                userName = snapshot.getValue(String::class.java) ?: "Người dùng"
            }
            .addOnFailureListener {
                errorMessage = "Lỗi khi tải tên người dùng: ${it.message}"
            }

        // Lấy hoặc tạo phòng chat
        database.child("rooms").get().addOnSuccessListener { snapshot ->
            val rooms = snapshot.children.mapNotNull { it.getValue(Room::class.java) }
            room = rooms.find { it.userIds.containsAll(listOf(currentUserId, userId)) }

            if (room != null) {
                // Lắng nghe thay đổi danh sách tin nhắn
                database.child("rooms").child(room!!.id).child("listMessage")
                    .addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val newMessage = snapshot.getValue(MessageItem::class.java)
                            if (newMessage != null) {
                                messageList = messageList + newMessage
                            }
                        }

                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                        override fun onChildRemoved(snapshot: DataSnapshot) {}
                        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                        override fun onCancelled(error: DatabaseError) {
                            errorMessage = "Lỗi khi tải danh sách tin nhắn: ${error.message}"
                        }
                    })
            } else {
                val newRoomId = database.child("rooms").push().key ?: ""
                val newRoom = Room(
                    id = newRoomId,
                    userIds = listOf(currentUserId, userId),
                    lastMessage = "",
                    lastUpdated = System.currentTimeMillis()
                )
                database.child("rooms").child(newRoomId).setValue(newRoom)
                room = newRoom
            }
        }.addOnFailureListener {
            errorMessage = "Lỗi khi tải phòng chat: ${it.message}"
        }
    }

    // Cuộn xuống tin nhắn cuối cùng khi `messageList` thay đổi
    LaunchedEffect(messageList) {
        if (messageList.isNotEmpty()) {
            listState.animateScrollToItem(messageList.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat với $userName", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(Color(0xFFEDE7F6))
            ) {
                LazyColumn(
                    state = listState, // Kết nối với LazyListState
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(messageList) { message ->
                        MessageCard(message, currentUserId)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newMessageText,
                        onValueChange = { newMessageText = it },
                        placeholder = { Text("Nhập tin nhắn...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    IconButton(
                        onClick = {
                            if (newMessageText.isNotBlank()) {
                                val newMessage = MessageItem(
                                    text = newMessageText,
                                    senderId = currentUserId,
                                    timestamp = System.currentTimeMillis()
                                )
                                room?.let {
                                    database.child("rooms").child(it.id).child("listMessage").push()
                                        .setValue(newMessage)
                                    database.child("rooms").child(it.id).child("lastMessage")
                                        .setValue(newMessage.text)
                                    database.child("rooms").child(it.id).child("lastUpdated")
                                        .setValue(System.currentTimeMillis())
                                }
                                newMessageText = ""
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color(0xFF4CAF50))
                    }
                }
            }
        }
    )
}

@Composable
fun MessageCard(message: MessageItem, currentUserId: String) {
    val isCurrentUser = message.senderId == currentUserId
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isCurrentUser) Color(0xFF673AB7) else Color(0xFFBDBDBD),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (isCurrentUser) Color.White else Color.Black,
                fontSize = 14.sp
            )
        }
    }
}


