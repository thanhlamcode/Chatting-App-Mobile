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

@Composable
fun UserListScreen(navController: NavController) {
    val database = FirebaseDatabase.getInstance().reference
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val currentUserId = sharedPreferences.getString("currentUserId", "") ?: ""

    var userList by remember { mutableStateOf(listOf<User>()) }
    var errorMessage by remember { mutableStateOf("") }

    // Tải danh sách người dùng
    LaunchedEffect(Unit) {
        database.child("users").get()
            .addOnSuccessListener { snapshot ->
                snapshot.children.forEach { child ->
                    val userId = child.key ?: return@forEach
                    database.child("users").child(userId).get()
                        .addOnSuccessListener { userSnapshot ->
                            val userName = userSnapshot.child("name").getValue(String::class.java) ?: "Người dùng"
                            val avatarUrl = userSnapshot.child("avatar").getValue(String::class.java) ?: ""

                            userList = userList + User(userId, userName, avatarUrl)
                        }
                }
            }
    }

    // Hiển thị giao diện danh sách người dùng
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

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(userList) { user ->
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
                painterResource(id = R.drawable.ic_placeholder) // Avatar mặc định
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