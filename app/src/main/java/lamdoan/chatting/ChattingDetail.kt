package lamdoan.chatting

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.*
import android.net.Uri
import android.provider.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(navController: NavController, userId: String) {
    val database = FirebaseDatabase.getInstance().reference
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val currentUserId = sharedPreferences.getString("currentUserId", "") ?: ""

    var userName by remember { mutableStateOf("Người dùng") }
    var avatarUrl by remember { mutableStateOf("") }
    var room by remember { mutableStateOf<Room?>(null) }
    var messageList by remember { mutableStateOf(listOf<MessageItem>()) }
    var newMessageText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    // Tự động cuộn xuống khi `messageList` thay đổi
    LaunchedEffect(messageList) {
        if (messageList.isNotEmpty()) {
            listState.animateScrollToItem(messageList.size - 1)
        }
    }

    LaunchedEffect(Unit) {
        database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
            userName = snapshot.child("name").getValue(String::class.java) ?: "Người dùng"
            avatarUrl = snapshot.child("avatar").getValue(String::class.java) ?: ""
        }.addOnFailureListener {
            errorMessage = "Lỗi khi tải thông tin người dùng: ${it.message}"
        }

        database.child("rooms").get().addOnSuccessListener { snapshot ->
            val rooms = snapshot.children.mapNotNull { it.getValue(Room::class.java) }
            room = rooms.find { it.userIds.containsAll(listOf(currentUserId, userId)) }

            if (room != null) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (avatarUrl.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(avatarUrl),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Gray)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "$userName", color = Color.White, maxLines = 1)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (!Settings.canDrawOverlays(context)) {
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        } else {
                            // Bắt đầu ChatBubbleService
                            startChatBubbleService(context, avatarUrl, userName)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bubble),
                            contentDescription = "Mở bong bóng chat",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        // Xóa toàn bộ tin nhắn
                        room?.let {
                            database.child("rooms").child(it.id).child("listMessage").removeValue()
                            messageList = listOf()
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa trò chuyện", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.DarkGray)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)
                    .padding(paddingValues)
                    .padding(8.dp)
            ) {
                LazyColumn(
                    state = listState,
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
                        placeholder = { Text("Nhập tin nhắn...", color = Color.Gray) },
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
                        Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color(0xFF2196F3))
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
                    color = if (isCurrentUser) Color(0xFF2196F3) else Color(0xFFBDBDBD),
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

fun startChatBubbleService(context: Context, avatarUrl: String, userName: String) {
    val intent = Intent(context, ChatBubbleService::class.java).apply {
        putExtra("avatarUrl", avatarUrl)
        putExtra("userName", userName)
    }
    context.startForegroundService(intent) // Chạy dịch vụ foreground
}



