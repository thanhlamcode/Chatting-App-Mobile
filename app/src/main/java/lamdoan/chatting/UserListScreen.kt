package lamdoan.chatting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lamdoan.chatting.R

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserListScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FD))
            .padding(16.dp)
    ) {
        // Header
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text "Hello, Johan"
            Column {
                Text(
                    text = "Hello,",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Johan",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Icons (Search and Menu)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Handle search action */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search Icon",
                        tint = Color(0xFF6200EA),
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { /* Handle menu action */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_menu),
                        contentDescription = "Menu Icon",
                        tint = Color(0xFF6200EA),
                        modifier = Modifier.size(24.dp)
                    )
                }
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

        // User List
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(6) { index ->
                UserRow(
                    name = "User $index",
                    message = "This is a sample message",
                    time = "09:38 AM",
                    unreadCount = if (index % 2 == 0) 2 else 0,
                    isPinned = index == 0
                )
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
    unreadCount: Int = 0,
    isPinned: Boolean = false
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
            Image(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = "User Avatar",
                modifier = Modifier.fillMaxSize()
            )
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
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(20.dp)
                        .background(Color(0xFF6200EA), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = unreadCount.toString(),
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Pin Icon
        if (isPinned) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pin),
                contentDescription = "Pinned",
                tint = Color(0xFF6200EA),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
