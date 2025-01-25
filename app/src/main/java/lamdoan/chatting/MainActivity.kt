package lamdoan.chatting

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import lamdoan.chatting.auth.LoginScreen
import lamdoan.chatting.auth.SignUpScreen
import lamdoan.chatting.ui.theme.ChattingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lấy trạng thái đăng nhập từ SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val currentUserId = sharedPreferences.getString("currentUserId", "") ?: ""
        val startDestination = if (isLoggedIn) "UserListScreen" else "main"

        setContent {
            ChattingAppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("main") { MainScreen(navController = navController) }
                    composable("sign_in") { LoginScreen(navController = navController) }
                    composable("create_account") { SignUpScreen(navController = navController) }
                    composable("UserListScreen") {
                        // Đảm bảo truyền currentUserId vào UserListScreen
                        ChatAndUserTabsScreen(currentUserId = currentUserId, navController = navController)
                    }
                    composable("ChangeAvatarScreen") {
                        ChangeAvatarScreen(navController = navController)
                    }
                    composable("ChangeNameScreen") {
                        ChangeNameScreen(navController = navController)
                    }
                    composable("chat_detail/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        ChatDetailScreen(navController, userId)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: androidx.navigation.NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "The best app\nfor your chatting",
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 60.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .clickable {
                            navController.navigate("sign_in")
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

                Text(
                    text = "Create an account",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.clickable {
                        navController.navigate("create_account")
                    }
                )
            }
        }
    }
}
