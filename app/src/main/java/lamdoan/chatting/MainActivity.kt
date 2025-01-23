package lamdoan.chatting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import lamdoan.chatting.ui.theme.ChattingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattingAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") { MainScreen(navController = navController) } // Truyền navController
                    composable("sign_in") { LoginScreen(navController = navController) } // Truyền navController
                    composable("create_account") { SignUpScreen(navController = navController) } // Truyền navController
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: androidx.navigation.NavController) {
    // Background image
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.img), // Replace with your image resource
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title
            Text(
                text = "The best app\nfor your chatting",
                fontSize = 52.sp, // Kích thước chữ lớn hơn
                fontWeight = FontWeight.Bold, // In đậm để tạo điểm nhấn
                color = Color.White, // Màu chữ trắng nổi bật
                lineHeight = 60.sp, // Khoảng cách dòng phù hợp với fontSize
                modifier = Modifier
                    .fillMaxWidth() // Đảm bảo chữ trải đều theo chiều ngang
                    .padding(horizontal = 16.dp, vertical = 32.dp) // Cách đều các cạnh
                    .align(Alignment.CenterHorizontally)
            )


            // Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sign In Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .clickable {
                            navController.navigate("sign_in") // Navigate to the sign-in screen
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign in",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Create Account Text
                Text(
                    text = "Create an account",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.clickable {
                        navController.navigate("create_account") // Navigate to the create account screen
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    // Provide a mock NavController for the preview
    val navController = rememberNavController()
    MainScreen(navController)
}
