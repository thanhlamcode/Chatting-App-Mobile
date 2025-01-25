package lamdoan.chatting

import android.content.Context
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.*

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatar: String = ""
)

data class Room(
    val id: String = "",
    val userIds: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val isSeen: Map<String, Boolean> = emptyMap() // Add this line
)

@Composable
fun ChatAndUserTabsScreen(currentUserId: String, navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Đoạn Chat", "Người Dùng")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
    ) {
        // Spacer để đẩy TabRow xuống dưới một chút
        Spacer(modifier = Modifier.height(40.dp)) // Tạo khoảng cách với cạnh trên

        // TabRow
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Black,
            contentColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp) // Thêm khoảng cách hai bên (tuỳ chọn)
                .zIndex(1f) // Đảm bảo TabRow không bị che
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, color = if (selectedTabIndex == index) Color.White else Color.Gray) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách giữa TabRow và nội dung

        // Nội dung của từng tab
        when (selectedTabIndex) {
            0 -> ChatListScreen(currentUserId = currentUserId, navController = navController)
            1 -> UserListScreen(currentUserId = currentUserId, navController = navController)
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(currentUserId: String, navController: NavController) {
    val database = FirebaseDatabase.getInstance().reference
    var rooms by remember { mutableStateOf(listOf<Room>()) }
    var users by remember { mutableStateOf(listOf<User>()) }
    var searchText by remember { mutableStateOf("") }

    // Fetch data from Firebase
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

        // Filtered chat rooms
        val filteredRooms = rooms.filter { room ->
            room.userIds.contains(currentUserId) &&
                    users.any { user -> user.id in room.userIds && user.name.contains(searchText, true) }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            filteredRooms.forEach { room ->
                val otherUserId = room.userIds.first { it != currentUserId }
                val otherUser = users.find { it.id == otherUserId }

                if (otherUser != null) {
                    UserRow(
                        name = otherUser.name,
                        lastMessage = room.lastMessage,
                        time = room.lastUpdated,
                        isSeen = room.isSeen[currentUserId] ?: true,
                        avatar = otherUser.avatar,
                        onClick = {
                            navController.navigate("ChatDetailScreen/${otherUser.id}/${room.id}")
                        }
                    )
                }
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
        // Avatar
        Image(
            painter = if (avatar.isNullOrEmpty()) painterResource(id = R.drawable.ic_placeholder)
            else rememberAsyncImagePainter(model = avatar),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        // User info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            )
            Text(
                text = lastMessage,
                color = if (isSeen) Color.Gray else Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Last updated time
        Text(
            text = getTimeAgo(time),
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

fun getTimeAgo(time: Long): String {
    val diff = System.currentTimeMillis() - time
    val minutes = diff / 60000
    val hours = diff / 3600000

    return when {
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        else -> "${hours / 24} ngày trước"
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(currentUserId: String, navController: NavController) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().reference
    var users by remember { mutableStateOf(listOf<User>()) }
    var rooms by remember { mutableStateOf(listOf<Room>()) }
    var searchText by remember { mutableStateOf("") }

    // Lấy danh sách người dùng và phòng từ Firebase
    LaunchedEffect(Unit) {
        database.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users = snapshot.children.mapNotNull {
                    it.getValue(User::class.java)?.copy(id = it.key ?: "")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Lỗi khi tải người dùng: ${error.message}")
            }
        })

        database.child("rooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rooms = snapshot.children.mapNotNull {
                    it.getValue(Room::class.java)?.copy(id = it.key ?: "")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Lỗi khi tải phòng: ${error.message}")
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .padding(16.dp)
    ) {
        // Tiêu đề
        Text(
            text = "Danh sách người dùng",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ô tìm kiếm
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Tìm kiếm người dùng...", color = Color.White.copy(alpha = 0.5f)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Danh sách người dùng
        val filteredUsers = users.filter { user ->
            user.name.contains(searchText, ignoreCase = true) && user.id != currentUserId
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filteredUsers.forEach { user ->
                UserRow(
                    name = user.name,
                    message = user.email,
                    avatar = user.avatar,
                    onClick = {
                        // Kiểm tra xem phòng đã tồn tại chưa
                        val existingRoom = rooms.find { room ->
                            room.userIds.contains(currentUserId) && room.userIds.contains(user.id)
                        }

                        val roomId = existingRoom?.id ?: run {
                            // Tạo phòng mới nếu chưa tồn tại
                            val newRoom = Room(
                                id = database.child("rooms").push().key ?: "",
                                userIds = listOf(currentUserId, user.id),
                                lastMessage = "",
                                lastUpdated = System.currentTimeMillis()
                            )
                            database.child("rooms").child(newRoom.id).setValue(newRoom)
                            newRoom.id
                        }

                        navController.navigate("ChatDetailScreen/${user.id}/$roomId")
                    }
                )
            }

            // Thông báo nếu không tìm thấy người dùng
            if (filteredUsers.isEmpty()) {
                Text(
                    text = "Không tìm thấy người dùng nào.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun UserRow(
    name: String,
    message: String,
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
        // Ảnh đại diện
        Image(
            painter = if (avatar.isNullOrEmpty()) painterResource(id = R.drawable.ic_placeholder)
            else rememberAsyncImagePainter(model = avatar),
            contentDescription = "Ảnh đại diện",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Thông tin người dùng
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
            Text(text = message, color = Color.Gray, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
