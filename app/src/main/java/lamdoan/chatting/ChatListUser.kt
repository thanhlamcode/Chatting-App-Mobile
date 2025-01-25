package lamdoan.chatting

import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatListScreen(navController: NavController) {
    val database = FirebaseDatabase.getInstance().reference
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val currentUserId = sharedPreferences.getString("currentUserId", "") ?: ""

    var chatList by remember { mutableStateOf(listOf<Room>()) }
    var userMap by remember { mutableStateOf(mapOf<String, User>()) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        database.child("rooms").orderByChild("lastUpdated").get()
            .addOnSuccessListener { snapshot ->
                val rooms = snapshot.children.mapNotNull { it.getValue(Room::class.java) }
                    .filter { it.userIds.contains(currentUserId) }
                    .sortedByDescending { it.lastUpdated }

                chatList = rooms

                // Tải thông tin người dùng cho từng phòng chat
                val userIds = rooms.flatMap { it.userIds }.distinct().filter { it != currentUserId }
                userIds.forEach { userId ->
                    database.child("users").child(userId).get()
                        .addOnSuccessListener { userSnapshot ->
                            val user = userSnapshot.getValue(User::class.java)
                            if (user != null) {
                                userMap = userMap + (userId to user)
                            }
                        }
                        .addOnFailureListener {
                            errorMessage = "Lỗi khi tải thông tin người dùng: ${it.message}"
                        }
                }
            }
            .addOnFailureListener {
                errorMessage = "Lỗi khi tải danh sách chat: ${it.message}"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C)) // Background màu xám
            .padding(16.dp)
    ) {
        Text(
            text = "Danh sách đoạn chat",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White, // Chữ màu trắng
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(chatList) { room ->
                    val otherUserId = room.userIds.first { it != currentUserId }
                    val user = userMap[otherUserId]

                    ChatCard(
                        room = room,
                        user = user,
                        onClick = {
                            navController.navigate("chat_detail/${otherUserId}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatCard(room: Room, user: User?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3A3A3A)) // Màu nền xám đậm
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = if (user?.avatar.isNullOrEmpty()) {
                painterResource(id = R.drawable.ic_placeholder) // Placeholder nếu không có avatar
            } else {
                rememberAsyncImagePainter(user?.avatar)
            },
            contentDescription = "Avatar",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user?.name ?: "Người dùng",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White // Tên màu trắng
            )
            Text(
                text = room.lastMessage,
                fontSize = 14.sp,
                color = Color(0xFFB0B0B0), // Tin nhắn gần đây màu xám nhạt
                maxLines = 1
            )
        }

        val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(room.lastUpdated))
        Text(
            text = formattedTime,
            fontSize = 12.sp,
            color = Color(0xFFB0B0B0) // Thời gian màu xám nhạt
        )
    }
}
