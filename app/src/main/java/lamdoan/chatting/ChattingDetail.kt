package lamdoan.chatting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen() {
    val messages = remember {
        listOf(
            MessageItem("Hey 👋", isFromMe = false),
            MessageItem("Are you available for a New UI Project?", isFromMe = false),
            MessageItem("Hello!", isFromMe = true),
            MessageItem("Yes, have some space for the new task", isFromMe = true),
            MessageItem("Cool, should I share the details now?", isFromMe = false),
            MessageItem("Yes Sure, please", isFromMe = true),
            MessageItem("Great, here is the SOW of the Project", isFromMe = false),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Đặt màu nền thành màu trắng
    ) {
        // Header
        ChatHeader()

        Spacer(modifier = Modifier.height(8.dp))

        // Messages
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            messages.forEach { message ->
                ChatBubble(message.text, message.isFromMe)
            }

            // File Attachment Example
            FileAttachmentItem(fileName = "UI Brief.docx", fileSize = "269.18 KB")
        }

        // Input Bar
        ChatInputBar()
    }
}

@Composable
fun ChatHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Handle back */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_profile_picture),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(40.dp)
                .background(Color.Gray, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text("Larry Machigo", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
            Text("Online", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { /* Handle call */ }) {
                Icon(Icons.Outlined.Call, contentDescription = "Call", tint = Color.White)
            }
            IconButton(onClick = { /* Handle video call */ }) {
                Icon(Icons.Outlined.VideoCall, contentDescription = "Video Call", tint = Color.White)
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isFromMe: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = if (isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Text(
            text = text,
            modifier = Modifier
                .background(
                    color = if (isFromMe) Color(0xFF4CAF50) else Color(0xFFBB86FC),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp),
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
fun FileAttachmentItem(fileName: String, fileSize: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFBB86FC), RoundedCornerShape(16.dp))
            .padding(12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Filled.Attachment, contentDescription = "File", tint = Color.White)
            Column {
                Text(fileName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(fileSize, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4CAF50))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Handle microphone */ }) {
            Icon(painter = painterResource(id = R.drawable.ic_microphone), contentDescription = "Microphone", tint = Color.White)
        }

        TextField(
            value = "",
            onValueChange = { /* Handle text change */ },
            placeholder = {
                Text(
                    text = "Type a message",
                    color = Color.White.copy(alpha = 0.5f)
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            maxLines = 1
        )


        IconButton(onClick = { /* Handle send */ }) {
            Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}

data class MessageItem(val text: String, val isFromMe: Boolean)

@Preview(showBackground = true)
@Composable
fun PreviewChatDetailScreen() {
    ChatDetailScreen()
}
