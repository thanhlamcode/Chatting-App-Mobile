package lamdoan.chatting

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val database = FirebaseDatabase.getInstance().reference
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var avatarUrl by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    var uploadMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("currentUserId", "") ?: ""

        if (userId.isNotEmpty()) {
            val userRef = database.child("users").child(userId)

            // Lấy ảnh đại diện
            userRef.child("avatar").get().addOnSuccessListener { snapshot ->
                avatarUrl = snapshot.getValue(String::class.java) ?: ""
            }.addOnFailureListener { error ->
                uploadMessage = "Lỗi khi tải ảnh đại diện: ${error.message}"
            }

            // Lấy tên hiển thị
            userRef.child("name").get().addOnSuccessListener { snapshot ->
                displayName = snapshot.getValue(String::class.java) ?: "Người dùng"
            }.addOnFailureListener { error ->
                uploadMessage = "Lỗi khi tải tên người dùng: ${error.message}"
            }
        } else {
            uploadMessage = "Không tìm thấy ID người dùng. Vui lòng đăng nhập lại!"
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tiêu đề
        Text(
            text = "Cài Đặt",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị ID người dùng
        Text(
            text = "ID người dùng: $userId",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị ảnh đại diện
        if (avatarUrl.isNotBlank()) {
            Image(
                painter = rememberAsyncImagePainter(avatarUrl),
                contentDescription = "Ảnh đại diện",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "Chưa có ảnh đại diện",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input tên hiển thị
        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Tên hiển thị", color = Color.White.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                cursorColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input URL ảnh đại diện
        OutlinedTextField(
            value = avatarUrl,
            onValueChange = { avatarUrl = it },
            label = { Text("Nhập URL ảnh đại diện", color = Color.White.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                cursorColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nút cập nhật thông tin
        // Nút cập nhật thông tin
        Button(
            onClick = {
                if (displayName.isNotBlank() && avatarUrl.isNotBlank()) {
                    isUploading = true

                    // Lấy currentUserId từ SharedPreferences
                    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                    val currentUserId = sharedPreferences.getString("currentUserId", null)

                    if (currentUserId != null) {
                        val userRef = database.child("users").child(currentUserId)

                        val updates = mapOf(
                            "name" to displayName,
                            "avatar" to avatarUrl
                        )

                        userRef.updateChildren(updates).addOnSuccessListener {
                            isUploading = false
                            uploadMessage = "Cập nhật thông tin thành công!"
                        }.addOnFailureListener { error ->
                            isUploading = false
                            uploadMessage = "Lỗi: ${error.message}"
                        }
                    } else {
                        isUploading = false
                        uploadMessage = "Không tìm thấy ID người dùng. Vui lòng đăng nhập lại!"
                    }
                } else {
                    uploadMessage = "Tên và URL không được để trống!"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            enabled = !isUploading
        ) {
            Text(text = if (isUploading) "Đang lưu..." else "Cập nhật thông tin", color = Color.White)
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị thông báo
        if (uploadMessage.isNotBlank()) {
            Text(text = uploadMessage, color = Color.White, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Nút đăng xuất
        Button(
            onClick = {
                val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    clear() // Xóa tất cả dữ liệu đăng nhập
                    apply()
                }
                auth.signOut()
                navController.navigate("sign_in") {
                    popUpTo("UserListScreen") { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(text = "Đăng xuất", color = Color.White)
        }
    }
}
