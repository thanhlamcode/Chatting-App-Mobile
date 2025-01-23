package lamdoan.chatting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreen() {
    // States for input fields
    val fullName = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val rememberMeChecked = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.img), // Replace with your image
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Ensures the image fills the screen
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Căn giữa theo chiều dọc
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Welcome Back",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White, // White text for contrast
                textAlign = TextAlign.Center
            )

            Text(
                text = "Login to your account",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = fullName.value,
                onValueChange = { fullName.value = it },
                label = {
                    Text(
                        text = "Full Name",
                        color = Color.White // Màu label
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user), // Icon user
                        contentDescription = "User Icon",
                        tint = Color.White // Màu icon
                    )
                },
                placeholder = {
                    Text(
                        text = "Enter your name",
                        color = Color.White.copy(alpha = 0.5f) // Màu placeholder
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White, // Viền màu trắng khi focus
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f), // Viền màu trắng nhạt khi không focus
                    focusedLabelColor = Color.White, // Màu label khi focus
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f), // Màu label khi không focus
                    cursorColor = Color.White // Màu con trỏ
                )
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = {
                    Text(
                        text = "Password",
                        color = Color.White // Màu label
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock), // Icon khóa
                        contentDescription = "Lock Icon",
                        tint = Color.White // Màu icon
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                        Icon(
                            painter = painterResource(
                                id = if (isPasswordVisible.value) R.drawable.ic_eye
                                else R.drawable.ic_eye_off
                            ), // Icon toggle hiện/ẩn mật khẩu
                            contentDescription = "Toggle Password Visibility",
                            tint = Color.White // Màu icon
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                placeholder = {
                    Text(
                        text = "Enter your password",
                        color = Color.White.copy(alpha = 0.5f) // Màu placeholder
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White, // Màu viền khi focus
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f), // Màu viền khi không focus
                    focusedLabelColor = Color.White, // Màu label khi focus
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f), // Màu label khi không focus
                    cursorColor = Color.White // Màu con trỏ
                )
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Remember Me and Forgot Password
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMeChecked.value,
                        onCheckedChange = { rememberMeChecked.value = it }
                    )
                    Text("Remember Me", fontSize = 14.sp, color = Color.White)
                }

                Text(
                    text = "Forgot Password?",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.clickable { /* Handle click */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = { /* Handle login */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Login", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up Text
            Text(
                text = "Don’t have an account? Sign up",
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { /* Handle sign up */ }
            )
        }
    }
}

