package lamdoan.chatting

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.*

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatar: String = "",
    val password: String = "",
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(currentUserId: String, navController: NavController) {
    val database = FirebaseDatabase.getInstance().reference
    var rooms by remember { mutableStateOf(listOf<Room>()) }
    var users by remember { mutableStateOf(listOf<User>()) }
    var searchText by remember { mutableStateOf("") }

    // Fetch data from Firebase
    LaunchedEffect(Unit) {
        // Fetch users
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

        // Fetch rooms and filter by current user
        database.child("rooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rooms = snapshot.children.mapNotNull {
                    val room = it.getValue(Room::class.java)?.copy(id = it.key ?: "")
                    room?.takeIf { it.userIds.contains(currentUserId) } // Chỉ lấy phòng mà người dùng hiện tại tham gia
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error fetching rooms: ${error.message}")
            }
        })
    }

    // Main UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Đoạn Chat",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Tìm kiếm...", color = Color.White.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter and sort rooms
        val filteredRooms = rooms.filter { room ->
            users.any { user -> user.id in room.userIds && user.name.contains(searchText, ignoreCase = true) }
        }.sortedByDescending { it.lastUpdated }

        // Room list
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            filteredRooms.forEach { room ->
                val otherUserId = room.userIds.firstOrNull { it != currentUserId }
                val otherUser = users.find { it.id == otherUserId }
            }

            if (filteredRooms.isEmpty()) {
                Text(
                    text = "Không có đoạn chat nào.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val currentUserId = remember { sharedPreferences.getString("currentUserId", "") ?: "" }

    if (currentUserId.isEmpty()) {
        // Xử lý khi không tìm thấy currentUserId (đăng xuất hoặc điều hướng sang màn hình đăng nhập)
        Text(
            text = "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.",
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    val database = FirebaseDatabase.getInstance().reference
    var rooms by remember { mutableStateOf(listOf<Room>()) }
    var users by remember { mutableStateOf(listOf<User>()) }
    var searchText by remember { mutableStateOf("") }

    // Fetch data from Firebase
    LaunchedEffect(Unit) {
        // Fetch users
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

        // Fetch rooms and filter by current user
        database.child("rooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rooms = snapshot.children.mapNotNull {
                    val room = it.getValue(Room::class.java)?.copy(id = it.key ?: "")
                    room?.takeIf { it.userIds.contains(currentUserId) } // Chỉ lấy phòng của currentUserId
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error fetching rooms: ${error.message}")
            }
        })
    }

    // Main UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Đoạn Chat",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Tìm kiếm...", color = Color.White.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter and sort rooms
        val filteredRooms = rooms.filter { room ->
            users.any { user -> user.id in room.userIds && user.name.contains(searchText, ignoreCase = true) }
        }.sortedByDescending { it.lastUpdated }

        // Room list
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            filteredRooms.forEach { room ->
                val otherUserId = room.userIds.firstOrNull { it != currentUserId }
                val otherUser = users.find { it.id == otherUserId }

            }

            if (filteredRooms.isEmpty()) {
                Text(
                    text = "Không có đoạn chat nào.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@Composable
fun ChatAndUserTabsScreen(currentUserId: String, navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Đoạn Chat", "Người Dùng", "Cài Đặt")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .padding(top = 40.dp) // Cách mép trên 40dp
    ) {
        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Black,
            contentColor = Color.White
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, color = if (selectedTabIndex == index) Color.White else Color.Gray) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nội dung cho từng tab
        when (selectedTabIndex) {
            0 -> ChatListScreen(currentUserId, navController)
            1 -> UserListScreen(navController)
            2 -> SettingsScreen(navController)
        }
    }
}


@Composable
fun UserRow(
    name: String,
    lastMessage: String,
    time: Long,
    isSeen: Boolean,
    avatar: String?,
    onClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (avatar.isNullOrEmpty()) Color.Gray else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (avatar.isNullOrEmpty()) {
                Text(name.firstOrNull()?.uppercase() ?: "", color = Color.White, fontWeight = FontWeight.Bold)
            } else {
                Image(
                    painter = rememberAsyncImagePainter(model = avatar),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
            Text(
                lastMessage,
                color = if (isSeen) Color.Gray else Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = getTimeAgo(time),
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

fun getTimeAgo(time: Long): String {
    val diff = System.currentTimeMillis() - time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Vừa xong"
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        days < 7 -> "$days ngày trước"
        else -> "${days / 7} tuần trước"
    }
}
