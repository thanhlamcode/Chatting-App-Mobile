package lamdoan.chatting

import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase

@Composable
fun UserListScreen(navController: NavController) {
    val database = FirebaseDatabase.getInstance().reference
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val currentUserId = sharedPreferences.getString("currentUserId", "") ?: ""

    var userList by remember { mutableStateOf(listOf<User>()) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        database.child("users").get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.children.mapNotNull { child ->
                    val userId = child.key ?: return@mapNotNull null
                    val userName = child.child("name").getValue(String::class.java) ?: "Unknown"
                    val avatarUrl = child.child("avatar").getValue(String::class.java) ?: ""

                    // Loại bỏ người dùng hiện tại
                    if (userId != currentUserId) {
                        User(userId, userName, avatarUrl)
                    } else {
                        null
                    }
                }
                userList = users
            }
            .addOnFailureListener { error ->
                errorMessage = "Lỗi khi tải danh sách người dùng: ${error.message}"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Danh sách người dùng",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(userList) { user ->
                    UserCard(user = user, navController = navController)
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFEFEFEF))
            .clickable {
                navController.navigate("chat/${user.id}")
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(user.avatar),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "ID: ${user.id}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
