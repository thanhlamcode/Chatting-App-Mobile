package lamdoan.chatting

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.*

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatar: String = ""
)

data class Chat(
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val isRead: Boolean = false
)

data class Room(
    val id: String = "",
    val userIds: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastUpdated: String = ""
)

@Composable
fun UserListScreen(currentUserId: String) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val userName = sharedPreferences.getString("userName", "User") // Lấy tên người dùng từ SharedPreferences

    val database = FirebaseDatabase.getInstance().reference
    var users by remember { mutableStateOf(listOf<User>()) }
    var rooms by remember { mutableStateOf(listOf<Room>()) }

    // Fetch users and rooms from Firebase
    LaunchedEffect(Unit) {
        database.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users = snapshot.children.mapNotNull {
                    it.getValue(User::class.java)?.copy(id = it.key ?: "")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error fetching users: ${error.message}")
            }
        })

        database.child("rooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rooms = snapshot.children.mapNotNull {
                    it.getValue(Room::class.java)?.copy(id = it.key ?: "")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error fetching rooms: ${error.message}")
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FD))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello,",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = userName ?: "User", // Hiển thị tên người dùng
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem("All Chats", isSelected = true)
            TabItem("Groups", isSelected = false)
            TabItem("Contacts", isSelected = false)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Room List
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            rooms.forEach { room ->
                val user = users.find { it.id in room.userIds && it.id != currentUserId }
                if (user != null) {
                    UserRow(
                        name = user.name,
                        message = room.lastMessage,
                        time = room.lastUpdated,
                        avatar = user.avatar
                    )
                }
            }
        }
    }
}


@Composable
fun TabItem(title: String, isSelected: Boolean) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) Color(0xFF6200EA) else Color.Gray,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(
                color = if (isSelected) Color(0xFF6200EA).copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun UserRow(
    name: String,
    message: String,
    time: String,
    avatar: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Picture
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color.LightGray, CircleShape)
        ) {
            if (avatar.isNullOrEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_placeholder),
                    contentDescription = "User Avatar",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(model = avatar),
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Message Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Time and Unread Messages
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = time,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
