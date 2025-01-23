package lamdoan.chatting

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    // States for input fields
    val fullName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.img), // Replace with your image
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Sign up to get started!",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name Input
            OutlinedTextField(
                value = fullName.value,
                onValueChange = { fullName.value = it },
                label = { Text("Full Name", color = Color.White) },
                placeholder = { Text("Enter your name", color = Color.White.copy(alpha = 0.5f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.White), // Màu chữ nhập vào
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.White, // Màu con trỏ
                    focusedBorderColor = Color.White, // Viền màu trắng khi focus
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f), // Viền màu trắng nhạt khi không focus
                    focusedLabelColor = Color.White, // Màu label khi focus
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f) // Màu label khi không focus
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Input
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email Address", color = Color.White) },
                placeholder = { Text("Enter your email", color = Color.White.copy(alpha = 0.5f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.White), // Màu chữ nhập vào
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.White, // Màu con trỏ
                    focusedBorderColor = Color.White, // Viền màu trắng khi focus
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f), // Viền màu trắng nhạt khi không focus
                    focusedLabelColor = Color.White, // Màu label khi focus
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f) // Màu label khi không focus
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password", color = Color.White) },
                placeholder = { Text("Enter your password", color = Color.White.copy(alpha = 0.5f)) },
                visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                        Icon(
                            painter = painterResource(
                                id = if (isPasswordVisible.value) R.drawable.ic_eye else R.drawable.ic_eye_off
                            ),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.White), // Màu chữ nhập vào
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.White, // Màu con trỏ
                    focusedBorderColor = Color.White, // Viền màu trắng khi focus
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f), // Viền màu trắng nhạt khi không focus
                    focusedLabelColor = Color.White, // Màu label khi focus
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f) // Màu label khi không focus
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Input
            OutlinedTextField(
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                label = { Text("Confirm Password", color = Color.White) },
                placeholder = { Text("Confirm your password", color = Color.White.copy(alpha = 0.5f)) },
                visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.White), // Màu chữ nhập vào
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.White, // Màu con trỏ
                    focusedBorderColor = Color.White, // Viền màu trắng khi focus
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f), // Viền màu trắng nhạt khi không focus
                    focusedLabelColor = Color.White, // Màu label khi focus
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f) // Màu label khi không focus
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Button
            Button(
                onClick = { navController.navigate("sign_in") }, // Navigate to Sign In screen after sign up
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Sign Up", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    val navController = rememberNavController()
    SignUpScreen(navController)
}
