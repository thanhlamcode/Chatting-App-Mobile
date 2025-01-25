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
    val lastUpdated: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(currentUserId: String, navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val userName = sharedPreferences.getString("userName", "User")
    val currentUserId = sharedPreferences.getString("currentUserId", "") // Get the current user ID

    val database = FirebaseDatabase.getInstance().reference
    var users by remember { mutableStateOf(listOf<User>()) }
    var rooms by remember { mutableStateOf(listOf<Room>()) }
    var searchText by remember { mutableStateOf("") }

    // Fetch users and rooms from Firebase
    LaunchedEffect(Unit) {
        database.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users = snapshot.children.mapNotNull {
                    it.getValue(User::class.java)?.copy(id = it.key ?: "")
                }.filter { it.id != currentUserId } // Exclude current user
                println("DEBUG: Filtered users: $users")
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

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
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
                    text = userName ?: "User",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    // Logout logic
                    with(sharedPreferences.edit()) {
                        clear()
                        apply()
                    }
                    navController.navigate("sign_in") {
                        popUpTo("UserListScreen") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Logout", color = Color.White, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filtered users
        val filteredUsers = users.filter { user ->
            user.name.contains(searchText, ignoreCase = true) && user.id != currentUserId
        }

        Column {
            filteredUsers.forEach { user ->
                UserRow(
                    name = user.name,
                    message = user.email,
                    time = "",
                    avatar = user.avatar,
                    onClick = { navController.navigate("ChatDetailScreen/${user.id}") }
                )
            }
        }
    }
}


@Composable
fun UserRow(
    name: String,
    message: String,
    time: String,
    avatar: String? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Picture
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color.Gray, CircleShape)
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
                color = Color.White
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

        // Time
        if (time.isNotEmpty()) {
            Text(
                text = time,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
