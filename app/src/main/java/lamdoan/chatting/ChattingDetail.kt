package lamdoan.chatting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.*

data class MessageItem(
    val text: String = "",
    val isFromMe: Boolean = false,
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(currentUserId: String, userId: String, roomId: String, navController: NavController) {
    val database = FirebaseDatabase.getInstance().reference
    var messages by remember { mutableStateOf(listOf<MessageItem>()) }
    var messageText by remember { mutableStateOf("") }

    // Fetch messages from Firebase
    LaunchedEffect(roomId) {
        database.child("rooms").child(roomId).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fetchedMessages = snapshot.children.mapNotNull {
                        it.getValue(MessageItem::class.java)
                    }.sortedBy { it.timestamp } // Sort messages by timestamp
                    messages = fetchedMessages
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error fetching messages: ${error.message}")
                }
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Set background color
    ) {
        // Header
        ChatHeader(navController = navController, userId = userId)

        Spacer(modifier = Modifier.height(8.dp))

        // Messages
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            messages.forEach { message ->
                ChatBubble(
                    text = message.text,
                    isFromMe = message.senderId == currentUserId,
                    timestamp = message.timestamp
                )
            }
        }

        // Input Bar
        ChatInputBar(
            messageText = messageText,
            onMessageTextChange = { messageText = it },
            onSend = {
                if (messageText.isNotBlank()) {
                    val message = MessageItem(
                        text = messageText,
                        isFromMe = true,
                        senderId = currentUserId
                    )
                    val roomRef = database.child("rooms").child(roomId)
                    val messageRef = roomRef.child("messages").push()

                    messageRef.setValue(message).addOnSuccessListener {
                        roomRef.child("lastMessage").setValue(messageText) // Cập nhật lastMessage
                        roomRef.child("lastUpdated").setValue(System.currentTimeMillis()) // Cập nhật lastUpdated
                    }

                    messageText = ""
                }
            }
        )
    }
}


@Composable
fun ChatHeader(navController: NavController, userId: String) {
    val database = FirebaseDatabase.getInstance().reference
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        database.child("users").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error fetching user: ${error.message}")
            }
        })
    }

    Spacer(modifier = Modifier.height(40.dp)) // Tạo khoảng cách với cạnh trên


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Image(
            painter = rememberAsyncImagePainter(user?.avatar ?: R.drawable.ic_placeholder),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(40.dp)
                .background(Color.Gray, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(user?.name ?: "User", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
            Text("Online", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        }
    }
}



@Composable
fun ChatBubble(text: String, isFromMe: Boolean, timestamp: Long) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start
    ) {
        Text(
            text = text,
            modifier = Modifier
                .background(
                    color = if (isFromMe) Color(0xFF4CAF50) else Color(0xFFBB86FC),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp),
            color = Color.White,
            fontSize = 16.sp
        )
        Text(
            text = getTimeAgo(timestamp),
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4CAF50))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = messageText,
            onValueChange = onMessageTextChange,
            placeholder = {
                Text(
                    text = "Type a message",
                    color = Color.White.copy(alpha = 0.5f)
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            maxLines = 1
        )

        IconButton(onClick = { onSend() }) {
            Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}
