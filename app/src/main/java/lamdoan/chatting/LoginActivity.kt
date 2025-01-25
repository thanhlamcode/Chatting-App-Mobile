package lamdoan.chatting

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val database = FirebaseDatabase.getInstance().reference.child("users")
    val context = LocalContext.current

    // Kiểm tra trạng thái đăng nhập
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            navController.navigate("UserListScreen") {
                popUpTo("sign_in") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Hình nền
        Image(
            painter = painterResource(id = R.drawable.img),
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
                text = "Welcome Back",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
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

            // Email input
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text(text = "Email", color = Color.White) },
                placeholder = { Text("Enter your email", color = Color.White.copy(alpha = 0.5f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password input
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(text = "Password", color = Color.White) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                        Icon(
                            painter = painterResource(
                                id = if (isPasswordVisible.value) R.drawable.ic_eye else R.drawable.ic_eye_off
                            ),
                            contentDescription = "Toggle Password Visibility",
                            tint = Color.White
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login button
            Button(
                onClick = {
                    scope.launch {
                        if (email.value.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                            Toasty.warning(context, "Please enter a valid email address.", Toast.LENGTH_SHORT, true).show()
                            return@launch
                        }
                        if (password.value.isBlank()) {
                            Toasty.warning(context, "Please enter your password.", Toast.LENGTH_SHORT, true).show()
                            return@launch
                        }

                        database.orderByChild("email").equalTo(email.value)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val user = snapshot.children.firstOrNull()?.getValue(User::class.java)
                                        if (user?.password == password.value) {
                                            Toasty.success(
                                                context,
                                                "Login successful!",
                                                Toast.LENGTH_SHORT,
                                                true
                                            ).show()

                                            // Save login state and user ID
                                            val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                                            with(sharedPreferences.edit()) {
                                                putBoolean("isLoggedIn", true)
                                                putString("userName", user.name)
                                                putString("currentUserId", user.id) // Save the current user ID
                                                apply()
                                            }

                                            // Navigate to UserListScreen
                                            navController.navigate("UserListScreen") {
                                                popUpTo("sign_in") { inclusive = true }
                                            }
                                        } else {
                                            Toasty.error(context, "Incorrect password!", Toast.LENGTH_SHORT, true).show()
                                        }
                                    } else {
                                        Toasty.info(context, "Email not found!", Toast.LENGTH_SHORT, true).show()
                                    }
                                }


                                override fun onCancelled(error: DatabaseError) {
                                    Toasty.error(context, "Login failed: ${error.message}", Toast.LENGTH_SHORT, true).show()
                                }
                            })
                    }
                },
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

            Text(
                text = "Don’t have an account? Sign up",
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    navController.navigate("create_account")
                }
            )
        }
    }
}

