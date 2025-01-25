package lamdoan.chatting

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.*

data class MessageItem(
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    // Constructor không tham số cần thiết cho Firebase
    constructor() : this("", "", System.currentTimeMillis())
}

data class Room(
    val id: String = "",
    val userIds: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val listMessage: List<MessageItem> = emptyList(),
    val isSeen: String = "false" // Giá trị mặc định
) {
    // Constructor không tham số cần thiết cho Firebase
    constructor() : this("", emptyList(), "", System.currentTimeMillis(), emptyList(), "false")
}


@Composable
fun ChatDetailScreen(navController: NavController, userId: String) {
    val database = FirebaseDatabase.getInstance().reference
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val currentUserId = sharedPreferences.getString("currentUserId", "") ?: ""

    var room by remember { mutableStateOf<Room?>(null) }
    var messageList by remember { mutableStateOf(listOf<MessageItem>()) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        database.child("rooms").get()
            .addOnSuccessListener { snapshot ->
                val rooms = snapshot.children.mapNotNull { child ->
                    child.getValue(Room::class.java)
                }

                // Tìm phòng có cả currentUserId và userId
                room = rooms.find { it.userIds.contains(currentUserId) && it.userIds.contains(userId) }

                if (room != null) {
                    // Lấy danh sách tin nhắn
                    messageList = room!!.listMessage
                } else {
                    // Tạo phòng mới nếu không tồn tại
                    val newRoomId = database.child("rooms").push().key ?: ""

                }
            }
            .addOnFailureListener { error ->
                errorMessage = "Lỗi khi tải danh sách phòng: ${error.message}"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Phòng trò chuyện",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        } else if (room != null) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(messageList) { message ->
                    MessageItemCard(message = message, currentUserId = currentUserId)
                }
            }
        }
    }
}

@Composable
fun MessageItemCard(message: MessageItem, currentUserId: String) {
    val isCurrentUser = message.senderId == currentUserId
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Text(
            text = message.text,
            modifier = Modifier
                .background(if (isCurrentUser) Color.Blue else Color.Gray)
                .padding(8.dp)
        )
    }
}
