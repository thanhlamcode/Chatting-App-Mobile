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

@Composable
fun ChatAndUserTabsScreen(currentUserId: String, navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Đoạn Chat", "Người Dùng", "Cài Đặt")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
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
            0 -> ChatListScreen(navController)
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
