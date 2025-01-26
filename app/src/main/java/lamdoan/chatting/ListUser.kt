package lamdoan.chatting

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(navController: NavController) {
    val database = FirebaseDatabase.getInstance().reference
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val currentUserId = sharedPreferences.getString("currentUserId", "") ?: ""

    var userList by remember { mutableStateOf(listOf<User>()) }
    var filteredUserList by remember { mutableStateOf(listOf<User>()) }
    var searchQuery by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Tải danh sách người dùng
    LaunchedEffect(Unit) {
        database.child("users").get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.children.mapNotNull { child ->
                    val userId = child.key ?: return@mapNotNull null
                    val userName = child.child("name").getValue(String::class.java) ?: "Người dùng"
                    val avatarUrl = child.child("avatar").getValue(String::class.java) ?: ""

                    if (userId != currentUserId) {
                        User(userId, userName, avatarUrl)
                    } else {
                        null
                    }
                }
                userList = users
                filteredUserList = users
            }
            .addOnFailureListener { error ->
                errorMessage = "Lỗi khi tải danh sách người dùng: ${error.message}"
            }
    }

    // Giao diện
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C))
            .padding(16.dp)
    ) {
        Text(
            text = "Danh sách người dùng",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Thanh tìm kiếm
        TextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                filteredUserList = if (query.isEmpty()) {
                    userList
                } else {
                    userList.filter { it.name.contains(query, ignoreCase = true) }
                }
            },
            placeholder = {
                Text(
                    text = "Tìm kiếm người dùng...",
                    color = Color(0xFFB0B0B0) // Màu placeholder
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF3A3A3A)),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent, // Màu nền bên trong TextField
                unfocusedIndicatorColor = Color.Transparent, // Bỏ viền khi không focus
                focusedIndicatorColor = Color.Transparent, // Bỏ viền khi focus
                cursorColor = Color.White, // Màu con trỏ
                focusedTextColor = Color.White, // Màu chữ khi TextField được focus
                unfocusedTextColor = Color.White // Màu chữ khi TextField không được focus
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị danh sách người dùng
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredUserList) { user ->
                    UserCard(user = user, onClick = {
                        navController.navigate("chat_detail/${user.id}")
                    })
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3A3A3A))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hiển thị avatar
        Image(
            painter = if (user.avatar.isNotEmpty()) {
                rememberAsyncImagePainter(model = user.avatar)
            } else {
                painterResource(id = R.drawable.ic_placeholder)
            },
            contentDescription = "Avatar",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Hiển thị thông tin người dùng
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}